package org.acme.rental;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/rental")
@Produces(MediaType.APPLICATION_JSON)
public class RentalResource {

  private final RentalsRepository rentalsRepository;

  public RentalResource(RentalsRepository rentalsRepository) {
    this.rentalsRepository = rentalsRepository;
  }

  @Path("/start/{userId}/{reservationId}")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Rental start(String userId, Long reservationId) {
    return rentalsRepository.create(userId, reservationId);
  }

  @GET
  public List<Rental> getAll() {
    return rentalsRepository.getAll();
  }

}
