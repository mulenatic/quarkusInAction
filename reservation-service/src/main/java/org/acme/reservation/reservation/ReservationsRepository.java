package org.acme.reservation.reservation;

import java.util.List;

import org.acme.reservation.entity.Reservation;

public interface ReservationsRepository {

  List<Reservation> findAll();

  Reservation save(Reservation reservation);

  void remove(Long reservationId);

}
