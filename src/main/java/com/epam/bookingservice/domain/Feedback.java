package com.epam.bookingservice.domain;

import lombok.Builder;
import lombok.Value;

@Builder(toBuilder = true)
@Value
public class Feedback {

    private final String text;
    private final User worker;
}
