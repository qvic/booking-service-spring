package com.epam.bookingservice.entity;

import java.util.Arrays;
import java.util.Optional;

public enum FeedbackStatusEntity {

    CREATED, APPROVED;

    public static Optional<FeedbackStatusEntity> findByName(String name) {
        return Arrays.stream(values())
                .filter(roleEntity -> roleEntity.name().equals(name))
                .findAny();
    }
}
