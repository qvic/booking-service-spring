package com.epam.bookingservice.service;

import com.epam.bookingservice.domain.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {

    User register(User user);
}
