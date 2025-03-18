package org.acme.billing.model;

import java.time.LocalDate;

import io.quarkus.mongodb.panache.PanacheMongoEntity;

public class InvoiceAdjust extends PanacheMongoEntity {

    public String userId;
    public LocalDate actualEndDate;
    public double price;
    public boolean paid;

    @Override
    public String toString() {
        return "InvoiceAdjust [userId=" + userId + ", actualEndDate=" + actualEndDate + ", price=" + price + ", paid="
                + paid + "]";
    }

}
