package com.epam.bookingservice.domain;

import com.epam.bookingservice.utility.EnumUtility;

import java.util.Optional;

public enum FeedbackStatus {
    APPROVED, CREATED;

    public static Optional<FeedbackStatus> findByName(String name) {
        return EnumUtility.findByName(name, values());
    }
}
