package org.acme.reservation.billing;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BillingService {

    @Incoming("invoices-rabbitmq")
    public void processInvoice(JsonObject json) {
        Invoice invoice = json.mapTo(Invoice.class);
        System.out.println("Processing received invoice: " + invoice);
    }

}
