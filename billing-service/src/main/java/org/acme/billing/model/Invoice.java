package org.acme.billing.model;

import java.time.LocalDate;

import io.quarkus.mongodb.panache.PanacheMongoEntity;

public class Invoice extends PanacheMongoEntity {

    public double totalPrice;
    public boolean paid;
    public Reservation reservation;

    public static final class Reservation {
        public Long id;
        public Long carId;
        public LocalDate startDay;
        public LocalDate enddDay;

        @Override
        public String toString() {
            return "Reservation [id=" + id + ", carId=" + carId + ", startDay=" + startDay + ", enddDay=" + enddDay
                    + ", userId=" + userId + "]";
        }

        public String userId;
    }

    public Invoice(double totalPrice, boolean paid, Reservation reservation) {
        this.totalPrice = totalPrice;
        this.paid = paid;
        this.reservation = reservation;
    }

    @Override
    public String toString() {
        return "Invoice [totalPrice=" + totalPrice + ", paid=" + paid + ", reservation=" + reservation + "]";
    }

}
