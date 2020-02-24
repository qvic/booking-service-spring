package com.salon.booking.service;

import com.salon.booking.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {

    Page<User> findAllWorkers(Pageable properties);

    Page<User> findAllClients(Pageable properties);

    void promoteToWorker(Integer clientId);

    Optional<User> findById(Integer id);
}
