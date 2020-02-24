package com.salon.booking.domain;

import lombok.Builder;
import lombok.Value;

import java.time.Duration;

@Builder(toBuilder = true)
@Value
public class SalonService {

    private final Integer id;
    private final String name;
    private final Duration duration;
    private final Integer price;
}
