package com.epam.bookingservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginForm {

    @NotNull
    @Email
    private String email;

    @NotNull
    @Length(min = 5, max = 200)
    private String password;
}

