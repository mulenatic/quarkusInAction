package org.acme.rental;

import java.util.List;

import org.acme.rental.entity.Rental;

public interface RentalsRepository {

  List<Rental> getAll();

  Rental create(String userId, Long reservationId);

}
