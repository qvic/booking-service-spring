package com.salon.booking.domain;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Builder(toBuilder = true)
@RequiredArgsConstructor
@Value
public class Feedback {

    private final String text;
    private final User worker;
}
