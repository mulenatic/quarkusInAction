package org.acme.reservation.rest;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.acme.reservation.billing.Invoice;
import org.acme.reservation.entity.Reservation;
import org.acme.reservation.inventory.Car;
import org.acme.reservation.inventory.GraphQLInventoryClient;
import org.acme.reservation.inventory.InventoryClient;
import org.acme.reservation.rental.RentalClient;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.graphql.client.GraphQLClient;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("reservation")
@Produces(MediaType.APPLICATION_JSON)
public class ReservationResource {

  public static final double STANDARD_RATE_PER_DAY = 19.99;

  private final InventoryClient inventoryClient;
  private final RentalClient rentalClient;

  @Inject
  @Channel("invoices")
  MutinyEmitter<Invoice> invoiceEmitter;

  @Inject
  SecurityContext securityContext;

  public ReservationResource(@GraphQLClient("inventory") GraphQLInventoryClient inventoryClient,
      @RestClient RentalClient rentalClient) {
    this.inventoryClient = inventoryClient;
    this.rentalClient = rentalClient;
  }

  @GET
  @Path("availability")
  @Retry(maxRetries = 25, delay = 1000)
  @Fallback(fallbackMethod = "availabilityFallback")
  public Uni<Collection<Car>> availability(@RestQuery LocalDate startDate, @RestQuery LocalDate endDate) {

    // obtain all cars from inventory
    Uni<List<Car>> availableCarsUni = inventoryClient.allCars();
    // get all current reservations
    Uni<List<Reservation>> reservationsUni = Reservation.listAll();

    // get all current reservations
    return Uni.combine().all().unis(availableCarsUni, reservationsUni)
        .with((availableCars, reservations) -> {
          // create a map from id to car
          Map<Long, Car> carsById = new HashMap<>();
          for (Car car : availableCars) {
            carsById.put(car.id, car);
          }

          // for each reservation, remove the car from the map
          for (Reservation reservation : reservations) {
            if (reservation.isReserved(startDate, endDate)) {
              carsById.remove(reservation.carId);
            }
          }
          return carsById.values();
        });

  }

  public Uni<Collection<Car>> availabilityFallback(@RestQuery LocalDate startDate, @RestQuery LocalDate endDate) {
    return Uni.createFrom().item(List.of());
  }

  @Consumes(MediaType.APPLICATION_JSON)
  @POST
  @WithTransaction
  public Uni<Reservation> make(Reservation reservation) {
    reservation.userId = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName()
        : "anonymous";

    return reservation.<Reservation>persist()
        .onItem()
        .call(persistedRerservation -> {
          Log.info("Successfully reserved reservation " + persistedRerservation);

          Uni<Void> invoiceUni = invoiceEmitter
              .send(new Invoice(reservation, computePrice(reservation)))
              .onFailure().invoke(throwable -> Log.errorf("Couldn't create invoice for %s. %s%n", persistedRerservation,
                  throwable.getMessage()));

          if (persistedRerservation.startDay.equals(LocalDate.now())) {
            return invoiceUni
                .chain(() -> rentalClient
                    .start(persistedRerservation.userId, persistedRerservation.id)
                    .onItem().invoke(rental -> Log.info("Successfully started rental " + rental))
                    .replaceWith(persistedRerservation));
          }

          return invoiceUni.replaceWith(persistedRerservation);
        });

  }

  private double computePrice(Reservation reservation) {
    return (ChronoUnit.DAYS.between(reservation.startDay, reservation.endDay) + 1) * STANDARD_RATE_PER_DAY;
  }

  @Path("{reservationId}")
  @DELETE
  @WithTransaction
  public Uni<Void> cancel(@RestPath Long reservationId) {
    Reservation.deleteById(reservationId);
    return Uni.createFrom().voidItem();
  }

  @GET
  @Path("all")
  public Uni<List<Reservation>> allReservations() {
    String userId = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName()
        : null;

    return Reservation.<Reservation>listAll()
        .onItem().transform(reservations -> reservations.stream()
            .filter(reservation -> userId == null || reservation.userId.equals(userId))
            .collect(Collectors.toList()));
  }

}
