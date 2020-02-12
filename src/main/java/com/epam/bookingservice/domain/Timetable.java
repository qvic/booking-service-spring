package com.epam.bookingservice.domain;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
public class Timetable {

    private final LocalDate date;
    private final List<Timeslot> rows;
}
