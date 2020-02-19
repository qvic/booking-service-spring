package com.salon.booking.domain;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Builder(toBuilder = true)
@Value
public class Timeslot {

    private final Integer id;
    private final LocalTime fromTime;
    private final LocalTime toTime;
    private final LocalDate date;
    private final Order order;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Timeslot timeslot = (Timeslot) o;
        return Objects.equals(id, timeslot.id) &&
                Objects.equals(fromTime, timeslot.fromTime) &&
                Objects.equals(toTime, timeslot.toTime) &&
                Objects.equals(date, timeslot.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fromTime, toTime, date, order);
    }
}
