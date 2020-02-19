package com.salon.booking.service;

import com.salon.booking.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    Page<User> findAll(Pageable pageable);

    List<User> findAllWorkers();
}
