package org.acme.rental.reservation;

import java.time.LocalDate;

public class Reservation {

    public Long carId;
    public LocalDate startDay;
    public LocalDate endDay;
    public String userId;

    @Override
    public String toString() {
        return "Reservation [carId=" + carId + ", startDay=" + startDay + ", endDay=" + endDay + ", userId=" + userId
                + "]";
    }

}
