package org.acme.billing;

import java.util.Random;

import org.acme.billing.data.InvoiceConfirmation;
import org.acme.billing.model.Invoice;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.quarkus.logging.Log;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PaymentRequester {

    private final Random random = new Random();

    @Incoming("invoices-requests")
    @Outgoing("invoices-confirmations")
    @Blocking
    public InvoiceConfirmation requestPayment(Invoice invoice) {
        payment(invoice.reservation.userId, invoice.totalPrice, invoice);

        invoice.paid = true;
        invoice.persist();

        return new InvoiceConfirmation(invoice, true);

    }

    private void payment(String user, double price, Object data) {
        Log.infof("Request for payment user: %s, price: %s, data: %s", user, price, data);
        try {
            Thread.sleep(random.nextInt(1000, 5000));
        } catch (InterruptedException e) {
            Log.error("Sleep interrupted", e);
        }

    }

/*       uncomment in order to consume confirmation here
      
      @Incoming("invoices-confirmations")
      public void consume(InvoiceConfirmation invoiceConfirmation) {
      System.out.println(invoiceConfirmation);
      } */

}
