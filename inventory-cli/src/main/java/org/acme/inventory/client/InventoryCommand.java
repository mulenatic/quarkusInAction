package org.acme.inventory.client;

import org.acme.inventory.model.InsertCarRequest;
import org.acme.inventory.model.InventoryService;
import org.acme.inventory.model.RemoveCarRequest;

import io.quarkus.grpc.GrpcClient;

public class InventoryCommand {

  @GrpcClient("inventory")
  InventoryService inventory;

  public void add(String licensePlateNumber, String manufacturer, String model) {
    inventory.add(
        InsertCarRequest.newBuilder()
            .setLicensePlateNumber(licensePlateNumber)
            .setManufacturer(manufacturer)
            .setModel(model)
            .build())
        .onItem().invoke(carResponse -> System.out.println("Inserted new car" + carResponse))
        .await().indefinitely();
  }

  public void remove(String licensePlateNumber) {
    inventory.remove(
        RemoveCarRequest.newBuilder()
            .setLicensePlateNumber(licensePlateNumber)
            .build())
        .onItem().invoke(carResponse -> System.out.println("Removed car" + carResponse))
        .await().indefinitely();
  }

}
