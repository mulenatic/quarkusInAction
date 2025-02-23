package org.acme.users;

import java.time.LocalDate;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestQuery;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
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

}
