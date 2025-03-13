package org.acme.rental;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.acme.rental.entity.Rental;

import io.quarkus.logging.Log;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/rental")
@Produces(MediaType.APPLICATION_JSON)
public class RentalResource {

  @Path("/start/{userId}/{reservationId}")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Rental start(String userId, Long reservationId) {
    Log.infof("Starting rental for %s with reservation %s", userId, reservationId);

    Rental rental = new Rental();
    rental.userId = userId;
    rental.reservationId = reservationId;
    rental.startDate = LocalDate.now();
    rental.active = true;

    rental.persist();
    return rental;
  }

  @PUT
  @Path("/end/{userId}/{reservationId}")
  public Rental end(String userId, Long reservationId) {
    Log.infof("Ending rental for %s with reservation %s", userId, reservationId);

    Optional<Rental> optionalRental = Rental.findByUserAndReservationIdsOptional(userId, reservationId);

    if (optionalRental.isPresent()) {
      Rental rental = optionalRental.get();
      rental.endDate = LocalDate.now();
      rental.active = false;
      rental.update();
      return rental;
    } else {
      throw new NotFoundException("Rental not found");
    }

  }

  @GET
  public List<Rental> getAll() {
    return Rental.listAll();
  }

  @GET
  @Path("/active")
  public List<Rental> listActive() {
    return Rental.listActive();
  }

}
