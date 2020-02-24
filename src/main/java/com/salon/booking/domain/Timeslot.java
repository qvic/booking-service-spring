package com.salon.booking.domain;

import lombok.Builder;
import lombok.Value;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Builder(toBuilder = true)
@Value
public class Timeslot {

    private final Integer id;
    private final LocalTime fromTime;
    private final Duration duration;
    private final LocalDate date;
    private final List<Order> orders;
}
