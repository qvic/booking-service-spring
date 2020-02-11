package com.epam.bookingservice.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Builder(toBuilder = true)
@Getter
public class User implements Serializable {

    private final Integer id;

    @NotNull
    @Length(min = 1, max = 200)
    private final String name;

    @NotNull
    @Email
    private final String email;

    @NotNull
    @Length(min = 5, max = 200)
    private final String password;

    private final Role role;
}
