package com.salon.booking.service;

import com.salon.booking.domain.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {

    User register(User user);
}
