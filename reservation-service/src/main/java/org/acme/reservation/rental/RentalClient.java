package org.acme.reservation.rental;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestPath;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@RegisterRestClient(configKey = "rental")
@Path("/rental")
public interface RentalClient {
  
  @POST
  @Path("/start/{userId}/{reservationId}")
  Uni<Rental> start(@RestPath String userId, @RestPath Long reservationId);

}
