package com.epam.bookingservice.service;

import com.epam.bookingservice.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    Page<User> findAll(Pageable pageable);

    List<User> findAllWorkers();
}
