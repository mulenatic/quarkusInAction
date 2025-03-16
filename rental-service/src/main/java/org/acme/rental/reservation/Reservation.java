package org.acme.rental.reservation;

import java.time.LocalDate;

public class Reservation {

    public Long carId;
    public LocalDate startDay;
    public LocalDate endDay;
    public String userId;

    public Reservation(Long carId, LocalDate startDay, LocalDate endDay, String userId) {
        this.carId = carId;
        this.startDay = startDay;
        this.endDay = endDay;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Reservation [carId=" + carId + ", startDay=" + startDay + ", endDay=" + endDay + ", userId=" + userId
                + "]";
    }

}
