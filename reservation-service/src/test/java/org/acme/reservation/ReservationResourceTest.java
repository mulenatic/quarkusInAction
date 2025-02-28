package org.acme.reservation;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import java.net.URL;
import java.time.LocalDate;
import java.util.Collections;

import org.acme.reservation.entity.Reservation;
import org.acme.reservation.inventory.Car;
import org.acme.reservation.inventory.GraphQLInventoryClient;
import org.acme.reservation.rest.ReservationResource;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.DisabledOnIntegrationTest;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

@QuarkusTest
public class ReservationResourceTest {

  @TestHTTPEndpoint(ReservationResource.class)
  @TestHTTPResource
  URL reservationResource;

  @TestHTTPEndpoint(ReservationResource.class)
  @TestHTTPResource("availability")
  URL availability;

  @TestHTTPEndpoint(ReservationResource.class)
  @TestHTTPResource("{id}")
  String remove;

  @TestHTTPEndpoint(ReservationResource.class)
  @TestHTTPResource("all")
  URL all;

  @Test
  public void testReservationIds() {
     createReservationAndAssureIdGiven();
  }

  private Reservation createReservationAndAssureIdGiven() {
    Reservation reservation = new Reservation();
    reservation.startDay = LocalDate.now().plusDays(5);
    reservation.endDay = LocalDate.now().plusDays(12);
    reservation.carId = 384L;

    Response response = RestAssured
        .given()
        .contentType(ContentType.JSON)
        .body(reservation)
        .when()
        .post(reservationResource);

    response
        .then()
        .statusCode(200)
        .body("id", notNullValue());

    return response.as(Reservation.class);
  }

  @Test
  public void testRemoveReservation() {
    Reservation reservation = createReservationAndAssureIdGiven();
    Reservation reservationToRemove = createReservationAndAssureIdGiven();

    RestAssured
        .given()
        .when()
        .delete(remove, reservationToRemove.id)
        .then()
        .statusCode(204);

    RestAssured
        .given()
        .when()
        .get(all)
        .then()
        .statusCode(200)
        .body("$", not(empty()))
        .body("size()", greaterThan(0));

  }

  @DisabledOnIntegrationTest(forArtifactTypes = DisabledOnIntegrationTest.ArtifactType.NATIVE_BINARY)
  @Test
  public void testMakingAReservationAndCheckAvailability() {
    GraphQLInventoryClient mock = Mockito.mock(GraphQLInventoryClient.class);
    Car peugeot = new Car(1L, "ABC123", "Peugeot", "3006");
    Mockito.when(mock.allCars())
        .thenReturn(Collections.singletonList(peugeot));
    QuarkusMock.installMockForType(mock, GraphQLInventoryClient.class);

    String startDate = "2022-01-01";
    String endDate = "2022-01-10";
    // List available cars for our requested timeslot and choose one
    Car[] cars = RestAssured.given()
        .queryParam("startDate", startDate)
        .queryParam("endDate", endDate)
        .when().get(availability)
        .then().statusCode(200)
        .extract().as(Car[].class);
    Car car = cars[0];

    // Prepare the reservation object
    Reservation reservation = new Reservation();
    reservation.carId = car.id;
    reservation.startDay = LocalDate.parse(startDate);
    reservation.endDay = LocalDate.parse(endDate);

    // Submit the reservation
    Response response = RestAssured.given()
        .contentType(ContentType.JSON)
        .body(reservation)
        .when()
        .post(reservationResource);

    response.then()
        .statusCode(200)
        .body("carId", is(car.id.intValue()));

    // Verify that this car doesn't show as available anymore
    RestAssured.given()
        .queryParam("startDate", startDate)
        .queryParam("endDate", endDate)
        .when().get(availability)
        .then().statusCode(200)
        .body("findAll { car -> card.id == " + car.id + "}", hasSize(0));
  }

}
