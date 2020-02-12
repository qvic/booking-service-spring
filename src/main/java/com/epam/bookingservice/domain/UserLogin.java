package com.epam.bookingservice.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Value
public class UserLogin {

    @NotNull
    @Email
    private final String email;

    @NotNull
    @Length(min = 5, max = 200)
    private final String password;
}

