package com.salon.booking.domain;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Value
public class Order {

    private final Integer id;
    private final LocalDateTime date;
    private final User worker;
    private final User client;
    private final SalonService service;
}