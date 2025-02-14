package org.acme.reservation.inventory;

import java.util.List;

import jakarta.inject.Singleton;

@Singleton
public class InMemoryInventoryClient implements InventoryClient {

  private static final List<Car> ALL_CARS = List.of(
    new Car(1L, "ABC-123", "Toyota", "Corolla"),
    new Car(2L, "ABC-132", "Honda", "Civic"),
    new Car(3L, "DFW-392", "Ford", "Kuga"),
    new Car(4L, "iow-924", "Ford", "Explorer")
  );

  @Override
  public List<Car> allCars() {
    return ALL_CARS;
  }

  

}
