package com.epam.bookingservice.service;

import com.epam.bookingservice.domain.Order;
import com.epam.bookingservice.domain.Service;

import java.util.List;

public interface OrderService {

    List<Order> findAllByClientId(Integer clientId);

    List<Order> findAllByWorker(Integer workerId);

    List<Service> findAllServices();
}
