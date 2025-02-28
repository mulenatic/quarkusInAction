package org.acme.reservation.entity;

import java.time.LocalDate;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Reservation extends PanacheEntity {

  public Long carId;
  public LocalDate startDay;
  public LocalDate endDay;
  public String userId;

  public static List<Reservation> findByCar(Long carId) {
    return list("carId", carId);
  }

  /**
   * Check if the given duration overlaps with this reservation
   * @param startDay
   * @param endDay
   * @return true if the dates overlap with the reservation, false otherwise
   */
  public boolean isReserved(LocalDate startDay, LocalDate endDay) {
    return (!this.endDay.isBefore(startDay) || this.startDay.isAfter(endDay));
  }

  @Override
  public String toString() {
    return "Reservation {id=" + id + ", carId=" + carId + ", startDay=" + startDay + ", endDay=" + endDay + ", userId="
        + userId + "}";
  }


}
