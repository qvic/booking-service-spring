package com.epam.bookingservice.service;

import com.epam.bookingservice.domain.User;
import com.epam.bookingservice.domain.UserLogin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> login(UserLogin userLogin);

    User register(User user);

    Page<User> findAll(Pageable pageable);

    List<User> findAllWorkers();
}
