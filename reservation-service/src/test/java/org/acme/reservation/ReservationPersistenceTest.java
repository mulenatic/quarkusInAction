package org.acme.reservation;

import java.time.LocalDate;

import org.acme.reservation.entity.Reservation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;

@QuarkusTest
public class ReservationPersistenceTest {

  @Test
  @Transactional
  public void testCreateReservation() {
    Reservation reservation = new Reservation();
    reservation.startDay = LocalDate.now().plusDays(5);
    reservation.endDay = LocalDate.now().plusDays(7);
    reservation.carId = 238L;
    reservation.persist();

    Assertions.assertNotNull(reservation.id);
    Assertions.assertEquals(1, Reservation.count());
    Reservation persistedReservation = Reservation.findById(reservation.id);
    Assertions.assertNotNull(persistedReservation);
    Assertions.assertEquals(reservation.carId, persistedReservation.carId);
  }

}
