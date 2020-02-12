package com.epam.bookingservice.service;

import com.epam.bookingservice.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    User register(User user);

    Page<User> findAll(Pageable pageable);

    List<User> findAllWorkers();
}
