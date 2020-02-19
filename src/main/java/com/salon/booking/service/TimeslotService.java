package com.salon.booking.service;

import com.salon.booking.domain.Timeslot;
import com.salon.booking.domain.Timetable;

import java.time.LocalDate;
import java.util.List;

public interface TimeslotService {

    List<Timetable> findAllBetween(LocalDate fromInclusive, LocalDate toExclusive);

    void updateTimeslotWithOrder(Timeslot timeslot);
}
