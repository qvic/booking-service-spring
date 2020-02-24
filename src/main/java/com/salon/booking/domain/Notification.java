package com.salon.booking.domain;

import lombok.Value;

@Value
public class Notification {

    private final Integer id;
    private final Order order;
    private final Boolean read;
}