package org.acme.users;

import java.time.LocalDate;
import java.util.Collection;

import org.acme.users.model.Car;
import org.acme.users.model.Reservation;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestResponse;

import io.quarkus.logging.Log;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/")
public class ReservationsResource {

  @CheckedTemplate
  public static class Templates {
    public static native TemplateInstance index(
        LocalDate startDate,
        LocalDate endDate,
        String name);

    public static native TemplateInstance listofreservations(
        Collection<Reservation> reservations);

    public static native TemplateInstance availablecars(
        Collection<Car> cars,
        LocalDate startDate,
        LocalDate endDate);
  }

  @Inject
  SecurityContext securityContext;

  @RestClient
  ReservationsClient client;

  @GET
  @Produces(MediaType.TEXT_HTML)
  public TemplateInstance index(@RestQuery LocalDate startDate, @RestQuery LocalDate enddDate) {

    if (startDate == null) {
      startDate = LocalDate.now().plusDays(1L);
    }
    if (enddDate == null) {
      enddDate = LocalDate.now().plusDays(7L);
    }

    return Templates.index(startDate, enddDate, securityContext.getUserPrincipal().getName());

  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  @Path("/get")
  public TemplateInstance getResevations() {
    Collection<Reservation> reservations = client.allReservations();
    return Templates.listofreservations(reservations);
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  @Path("/available")
  public TemplateInstance getAvailableCars(@RestQuery LocalDate startDate, @RestQuery LocalDate endDate) {
    Collection<Car> availableCars = client.availability(startDate, endDate);
    return Templates.availablecars(availableCars, startDate, endDate);
  }

  @POST
  @Produces(MediaType.TEXT_HTML)
  @Path("/reserve")
  public RestResponse<TemplateInstance> create(
      @RestForm LocalDate startDate,
      @RestForm LocalDate endDate,
      @RestForm Long carId) {
    Reservation reservation = new Reservation();
    reservation.startDay = startDate;
    reservation.endDay = endDate;
    reservation.carId = carId;
    client.make(reservation);

    return RestResponse.ResponseBuilder
        .ok(getResevations())
        .header("HX-Trigger-After-Swap", "update-available-cars-list")
        .build();
  }

  @DELETE
  @Produces(MediaType.TEXT_HTML)
  @Path("/cancel/{reservationId}")
  public RestResponse<TemplateInstance> cancel(@RestPath Long reservationId) {

    client.cancel(reservationId);

    return RestResponse.ResponseBuilder
        .ok(getResevations())
        .header("HX-Trigger-After-Swap", "update-available-cars-list")
        .build();

  }

}
