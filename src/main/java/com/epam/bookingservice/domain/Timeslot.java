package com.epam.bookingservice.domain;

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
}
