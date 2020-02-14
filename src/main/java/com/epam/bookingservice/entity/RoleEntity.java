package com.epam.bookingservice.entity;

import com.epam.bookingservice.utility.EnumUtility;

import java.util.Optional;

public enum RoleEntity {

    CLIENT, WORKER, ADMIN;

    public static Optional<RoleEntity> findByName(String name) {
        return EnumUtility.findByName(name, values());
    }
}
