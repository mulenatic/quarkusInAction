package org.acme.reservation;

import java.time.LocalDate;

import org.acme.reservation.entity.Reservation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.logging.Log;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;

@QuarkusTest
public class ReservationPersistenceTest {

  private static Reservation createReservation() {
    Reservation reservation = new Reservation();
    reservation.startDay = LocalDate.now().plusDays(5);
    reservation.endDay = LocalDate.now().plusDays(7);
    reservation.carId = 238L;
    reservation.persist();
    return reservation;
  }

  @Test
  @TestTransaction
  public void testCreateReservation() {
    Reservation reservation = createReservation();

    Assertions.assertNotNull(reservation.id);
    Assertions.assertEquals(1, Reservation.count());
    Reservation persistedReservation = Reservation.findById(reservation.id);
    Assertions.assertNotNull(persistedReservation);
    Assertions.assertEquals(reservation.carId, persistedReservation.carId);
  }

  @Test
  @TestTransaction
  public void testRemoveReservation() {

    Reservation reservation = createReservation();

    Reservation.deleteById(reservation.id);

  }

}
