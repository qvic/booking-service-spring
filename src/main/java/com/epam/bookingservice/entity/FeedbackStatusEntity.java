package com.epam.bookingservice.entity;

import com.epam.bookingservice.utility.EnumUtility;

import java.util.Optional;

public enum FeedbackStatusEntity {

    CREATED, APPROVED;

    public static Optional<FeedbackStatusEntity> findByName(String name) {
        return EnumUtility.findByName(name, values());
    }
}
