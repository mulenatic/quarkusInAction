package org.acme.reservation;

import java.time.LocalDate;

import org.acme.reservation.entity.Reservation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.TestReactiveTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.vertx.UniAsserter;

@QuarkusTest
public class ReservationPersistenceTest {

  private static Reservation createReservation() {
    Reservation reservation = new Reservation();
    reservation.startDay = LocalDate.now().plusDays(5);
    reservation.endDay = LocalDate.now().plusDays(7);
    reservation.carId = 238L;
    return reservation;
  }

  //@Test
  @TestReactiveTransaction
  public void testCreateReservation(UniAsserter asserter) {
    Reservation reservation = createReservation();

    asserter.<Reservation>assertThat(() -> reservation.persist(),
        r -> {
          Assertions.assertNotNull(r.id);
          asserter.putData("reservation.id", r.id);
        });

    asserter.assertEquals(() -> Reservation.count(), 1L);
    asserter.assertThat(
        () -> Reservation.<Reservation>findById(asserter.getData("reservation.id")),
        persistedRerservation -> {
          Assertions.assertNotNull(persistedRerservation);
          Assertions.assertEquals(reservation.carId, persistedRerservation.carId);
        });

  }

  //@Test
  @TestReactiveTransaction
  public void testRemoveReservation(UniAsserter asserter) {

    Reservation reservation = createReservation();

    asserter.<Reservation>assertThat(() -> reservation.persist(),
        r -> {
          Assertions.assertNotNull(r.id);
          asserter.putData("reservation.id", r.id);
        });

    asserter.<Boolean>assertThat(
        () -> Reservation.deleteById(reservation.id),
        result -> {
          Assertions.assertTrue(result);
        });

  }

}
