package org.acme.rental;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import io.quarkus.logging.Log;
import jakarta.inject.Singleton;

@Singleton
public class InMemoryRentalsRepository implements RentalsRepository {

  private final AtomicLong id = new AtomicLong();

  private final List<Rental> rentals = new CopyOnWriteArrayList<>();

  @Override
  public List<Rental> getAll() {
    return Collections.unmodifiableList(rentals);
  }

  @Override
  public Rental create(String userId, Long reservationId) {
    Log.infof("Starting rental for %s with reservation %s", userId, reservationId);
    Rental rental = new Rental(id.incrementAndGet(), userId, reservationId, LocalDate.now());
    rentals.add(rental);

    return rental;
  }

}
