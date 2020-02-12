package com.epam.bookingservice.domain;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Objects;

@Builder(toBuilder = true)
@Value
public class Order {

    private final LocalDateTime date;
    private final User worker;
    private final User client;
    private final Service service;
}
