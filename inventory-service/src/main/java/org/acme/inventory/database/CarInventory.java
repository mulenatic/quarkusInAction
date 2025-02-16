package org.acme.inventory.database;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.acme.inventory.model.Car;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CarInventory {

  private List<Car> cars;

  public static final AtomicLong ids = new AtomicLong();

  @PostConstruct
  void initialize() {
    cars = new CopyOnWriteArrayList<>();
    initialData();
  }

  private void initialData() {
    Car kuga = new Car();
    kuga.id = ids.incrementAndGet();
    kuga.licensePlateNumber = "ABC-123";
    kuga.manufacturer = "Ford";
    kuga.model = "Kuga";
    cars.add(kuga);
    
    Car explorer = new Car();
    explorer.id = ids.incrementAndGet();
    explorer.licensePlateNumber = "BCD-123";
    explorer.manufacturer = "Ford";
    explorer.model = "Explorer";
    cars.add(explorer);
  }

  public List<Car> getCars() {
    return cars;
  }

}
