package com.epam.bookingservice.domain;

import lombok.Builder;
import lombok.Value;

@Builder(toBuilder = true)
@Value
public class Service {

    private final Integer id;
    private final String name;
    private final Integer durationInTimeslots;
    private final Integer price;
}
