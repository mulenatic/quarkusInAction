package org.acme.reservation;

import static org.hamcrest.Matchers.notNullValue;

import java.net.URL;
import java.time.LocalDate;

import org.acme.reservation.reservation.Reservation;
import org.acme.reservation.rest.ReservationResource;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@QuarkusTest
public class ReservationRepositoryTest {

  @TestHTTPEndpoint(ReservationResource.class)
  @TestHTTPResource
  URL reservationResource;

  @Test
  public void testReservationIds() {
    Reservation reservation = new Reservation();
    reservation.startDay = LocalDate.now().plusDays(5);
    reservation.endDay = LocalDate.now().plusDays(12);
    reservation.carId = 384L;

    RestAssured
        .given()
        .contentType(ContentType.JSON)
        .body(reservation)
        .when()
        .post(reservationResource)
        .then()
        .statusCode(200)
        .body("id", notNullValue());
  }

}
