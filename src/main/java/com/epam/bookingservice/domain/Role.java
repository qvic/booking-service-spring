package com.epam.bookingservice.domain;

import com.epam.bookingservice.utility.EnumUtility;

import java.util.Optional;

public enum Role {
    CLIENT, ADMIN, WORKER;

    public static Optional<Role> findByName(String name) {
        return EnumUtility.findByName(name, values());
    }
}
