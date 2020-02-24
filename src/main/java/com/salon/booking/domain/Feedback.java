package com.salon.booking.domain;

import lombok.Builder;
import lombok.Value;

@Builder(toBuilder = true)
@Value
public class Feedback {

    private final Integer id;
    private final String text;
    private final Order order;
    private final FeedbackStatus status;
}