package org.acme.reservation.rest;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.acme.reservation.entity.Reservation;
import org.acme.reservation.inventory.Car;
import org.acme.reservation.inventory.GraphQLInventoryClient;
import org.acme.reservation.inventory.InventoryClient;
import org.acme.reservation.rental.Rental;
import org.acme.reservation.rental.RentalClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ResponseStatus;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.graphql.client.GraphQLClient;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
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

  private final InventoryClient inventoryClient;
  private final RentalClient rentalClient;

  @Inject
  SecurityContext securityContext;

  public ReservationResource(@GraphQLClient("inventory") GraphQLInventoryClient inventoryClient,
      @RestClient RentalClient rentalClient) {
    this.inventoryClient = inventoryClient;
    this.rentalClient = rentalClient;
  }

  @GET
  @Path("availability")
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

          if (persistedRerservation.startDay.equals(LocalDate.now())) {
            rentalClient
                .start(persistedRerservation.userId, persistedRerservation.id)
                .onItem().invoke(rental -> Log.info("Successfully started rental " + rental))
                .replaceWith(persistedRerservation);
          }

          return Uni.createFrom().item(persistedRerservation);
        });

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
