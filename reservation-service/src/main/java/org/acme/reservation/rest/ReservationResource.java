package org.acme.reservation.rest;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acme.reservation.inventory.Car;
import org.acme.reservation.inventory.GraphQLInventoryClient;
import org.acme.reservation.inventory.InventoryClient;
import org.acme.reservation.rental.Rental;
import org.acme.reservation.rental.RentalClient;
import org.acme.reservation.reservation.Reservation;
import org.acme.reservation.reservation.ReservationsRepository;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestQuery;

import io.quarkus.logging.Log;
import io.smallrye.graphql.client.GraphQLClient;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("reservation")
@Produces(MediaType.APPLICATION_JSON)
public class ReservationResource {

  private final ReservationsRepository reservationsRepository;
  private final InventoryClient inventoryClient;
  private final RentalClient rentalClient;

  public ReservationResource(ReservationsRepository reservationsRepository,
      @GraphQLClient("inventory") GraphQLInventoryClient inventoryClient,
      @RestClient RentalClient rentalClient) {
    this.reservationsRepository = reservationsRepository;
    this.inventoryClient = inventoryClient;
    this.rentalClient = rentalClient;
  }

  @GET
  @Path("availability")
  public Collection<Car> availability(@RestQuery LocalDate startDay, @RestQuery LocalDate endDate) {

    // obtain all cars from inventory
    List<Car> availableCars = inventoryClient.allCars();
    // create a map from id to car
    Map<Long, Car> carsById = new HashMap<>();
    for (Car car : availableCars) {
      carsById.put(car.id, car);
    }

    // get all current reservations
    List<Reservation> reservations = reservationsRepository.findAll();
    // for each reservation, remove the car from the map
    for (Reservation reservation : reservations) {
      if (reservation.isReserved(startDay, endDate)) {
        carsById.remove(reservation.carId);
      }
    }

    return carsById.values();

  }

  @Consumes(MediaType.APPLICATION_JSON)
  @POST
  public Reservation make(Reservation reservation) {
    Reservation result = reservationsRepository.save(reservation);

    // this is just a dummy value for the time being
    String userId = "x";

    if (reservation.startDay.equals(LocalDate.now())) {
      Rental rental = rentalClient.start(userId, result.id);
      Log.info("Successfully started rental " + rental);
    }

    return result;
  }

}
