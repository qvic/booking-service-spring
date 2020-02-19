package com.salon.booking.service;

import com.salon.booking.domain.Order;
import com.salon.booking.domain.Service;

import java.util.List;

public interface OrderService {

    List<Order> findAllByClientId(Integer clientId);

    List<Order> findAllByWorker(Integer workerId);

    List<Service> findAllServices();
}
