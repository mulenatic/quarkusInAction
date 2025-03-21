package org.acme.rental.reservation;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestPath;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@RegisterRestClient(configKey = "reservation")
@Path("/admin/reservation")
public interface ReservationClient {

    @GET
    @Path("/{reservationId}")
    public Reservation getById(@RestPath Long reservationId);

}
