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
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;

import io.quarkus.logging.Log;
import io.smallrye.graphql.client.GraphQLClient;
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
  public Collection<Car> availability(@RestQuery LocalDate startDate, @RestQuery LocalDate endDate) {

    // obtain all cars from inventory
    List<Car> availableCars = inventoryClient.allCars();
    // create a map from id to car
    Map<Long, Car> carsById = new HashMap<>();
    for (Car car : availableCars) {
      carsById.put(car.id, car);
    }

    // get all current reservations
    List<Reservation> reservations = Reservation.listAll();
    // for each reservation, remove the car from the map
    for (Reservation reservation : reservations) {
      if (reservation.isReserved(startDate, endDate)) {
        carsById.remove(reservation.carId);
      }
    }

    return carsById.values();

  }

  @Consumes(MediaType.APPLICATION_JSON)
  @POST
  @Transactional
  public Reservation make(Reservation reservation) {
    reservation.userId = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName()
        : "anonymous";

    reservation.persist();

    // this is just a dummy value for the time being
    String userId = "x";

    if (reservation.startDay.equals(LocalDate.now())) {
      Rental rental = rentalClient.start(userId, reservation.id);
      Log.info("Successfully started rental " + rental);
    }

    return reservation;
  }

  @Path("{reservationId}")
  @DELETE
  @Transactional
  public void cancel(@RestPath Long reservationId) {
    Reservation.deleteById(reservationId);
  }

  @GET
  @Path("all")
  public Collection<Reservation> allReservations() {
    String userId = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName()
        : null;

    return Reservation.<Reservation>streamAll()
        .filter(reservation -> reservation.userId.equals(userId) || userId == null)
        .collect(Collectors.toList());

  }

}
