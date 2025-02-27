package org.acme.rental;

import java.util.List;

public interface RentalsRepository {

  List<Rental> getAll();

  Rental create(String userId, Long reservationId);

}
