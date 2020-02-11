package com.epam.bookingservice.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class FeedbackEntity {

    private final Integer id;
    private final String text;
    private final FeedbackStatusEntity status;
    private final UserEntity worker;
}


