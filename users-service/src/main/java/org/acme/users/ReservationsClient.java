package org.acme.users;

import java.time.LocalDate;
import java.util.Collection;

import org.acme.users.model.Car;
import org.acme.users.model.Reservation;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;

import io.quarkus.oidc.token.propagation.AccessToken;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@RegisterRestClient(baseUri = "http://localhost:8081")
@AccessToken
@Path("reservation")
public interface ReservationsClient {

  @GET
  @Path("all")
  Collection<Reservation> allReservations();

  @POST
  Reservation make(Reservation reservation);

  @GET
  @Path("availability")
  Collection<Car> availability(
      @RestQuery LocalDate startDate,
      @RestQuery LocalDate endDate);

  @DELETE
  @Path("{reservationId}")
  void cancel(@RestPath Long reservationId);

}