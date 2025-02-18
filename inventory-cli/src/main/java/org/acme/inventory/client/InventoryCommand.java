package org.acme.inventory.client;

import org.acme.inventory.model.InsertCarRequest;
import org.acme.inventory.model.InventoryService;
import org.acme.inventory.model.RemoveCarRequest;

import io.quarkus.grpc.GrpcClient;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class InventoryCommand implements QuarkusApplication {

  private static final String USAGE = "Usage: inventory <add>|<remove> <licensePlateNumber> <manufacturer> <model>";

  @GrpcClient("inventory")
  InventoryService inventory;

  public void add(String licensePlateNumber, String manufacturer, String model) {
    inventory.add(
        InsertCarRequest.newBuilder()
            .setLicensePlateNumber(licensePlateNumber)
            .setManufacturer(manufacturer)
            .setModel(model)
            .build())
        .onItem().invoke(carResponse -> System.out.println("Inserted new car " + carResponse))
        .await().indefinitely();
  }

  public void remove(String licensePlateNumber) {
    inventory.remove(
        RemoveCarRequest.newBuilder()
            .setLicensePlateNumber(licensePlateNumber)
            .build())
        .onItem().invoke(carResponse -> System.out.println("Removed car " + carResponse))
        .await().indefinitely();
  }

  @Override
  public int run(String... args) throws Exception {
    String action = args.length > 0 ? args[0] : null;
    if ("add".equals(action) && args.length >= 4) {
      add(args[1], args[2], args[3]);
      return 0;
    } else if ("remove".equals(action) && args.length >= 2) {
      remove(args[1]);
      return 0;
    }

    System.err.println(USAGE);
    return 1;

  }

}
