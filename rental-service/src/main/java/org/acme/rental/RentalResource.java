package org.acme.rental;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

import io.quarkus.logging.Log;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/rental")
public class RentalResource {

  private final AtomicLong id = new AtomicLong();

  @Path("/start/{userid}/{reservationId}")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Rental start(String userId, Long reservationId) {
    Log.infof("Starting rental for %s with reservation %s", userId, reservationId);
    return new Rental(id.incrementAndGet(), userId, reservationId, LocalDate.now());
  }

}
