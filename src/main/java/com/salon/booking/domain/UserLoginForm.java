package com.salon.booking.domain;

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

    @NotNull(message = "{validation.email.not_null}")
    @Email(message = "{validation.email.invalid}")
    private String email;

    @NotNull(message = "{validation.password.not_null}")
    @Length(min = 5, max = 200, message = "{validation.password.length}")
    private String password;
}

