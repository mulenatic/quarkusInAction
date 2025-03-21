package org.acme.statistics.car;

import java.time.Instant;

import org.acme.statistics.car.inventory.GraphQLInventoryClient;

import io.quarkus.funqy.Funq;
import io.smallrye.graphql.client.GraphQLClient;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;

public class CarStatistics {

    @Inject
    @GraphQLClient("inventory")
    GraphQLInventoryClient inventoryClient;

    @Funq
    public Uni<String> getCarStatistics() {
        return inventoryClient.allCars()
        .map(cars -> ("The Car Rental car statistics created at %s" +
        "Number of available cars: %d")
        .formatted(Instant.now(), cars.size()));
    }

}
