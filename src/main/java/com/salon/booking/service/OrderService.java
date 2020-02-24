package com.salon.booking.service;

import com.salon.booking.domain.Order;
import com.salon.booking.domain.SalonService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    Page<Order> findAllByClientId(Integer clientId, Pageable pageable);

    Page<Order> findAllByWorkerId(Integer workerId, Pageable pageable);

    void saveOrderUpdatingTimeslots(Integer selectedTimeslotId, Order order);

    List<Order> findFinishedOrdersAfter(LocalDateTime dateTime, Integer clientId);

    List<SalonService> findAllServices();

    Optional<SalonService> findServiceById(Integer id);

    Optional<Order> findById(Integer id);
}
