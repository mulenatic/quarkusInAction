package org.acme.billing.model;

import java.time.LocalDate;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity
public class Invoice extends PanacheMongoEntity {

    public double totalPrice;
    public boolean paid;
    public Reservation reservation;

    public static final class Reservation {
        public Long id;
        public Long carId;
        public LocalDate startDay;
        public LocalDate enddDay;
        public String userId;
    }

    public Invoice(double totalPrice, boolean paid, Reservation reservation) {
        this.id = null;
        this.totalPrice = totalPrice;
        this.paid = paid;
        this.reservation = reservation;
    }

}
