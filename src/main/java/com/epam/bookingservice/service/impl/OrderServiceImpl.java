package com.epam.bookingservice.service.impl;

import com.epam.bookingservice.domain.Order;
import com.epam.bookingservice.entity.OrderEntity;
import com.epam.bookingservice.entity.ServiceEntity;
import com.epam.bookingservice.entity.UserEntity;
import com.epam.bookingservice.mapper.Mapper;
import com.epam.bookingservice.repository.OrderRepository;
import com.epam.bookingservice.repository.ServiceRepository;
import com.epam.bookingservice.repository.UserRepository;
import com.epam.bookingservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final Mapper<OrderEntity, Order> orderMapper;
    // rename domain service
    private final Mapper<ServiceEntity, com.epam.bookingservice.domain.Service> serviceMapper;

    @Override
    public List<Order> findAllByClientId(Integer clientId) {
        return orderRepository.findAllByClientId(clientId)
                .stream()
                .map(this::buildWithServiceAndWorker)
                .map(orderMapper::mapEntityToDomain)
                .collect(Collectors.toList());
    }

    private OrderEntity buildWithServiceAndWorker(OrderEntity orderEntity) {
        ServiceEntity serviceById = serviceRepository.findById(orderEntity.getService().getId())
                .orElseThrow(() -> new RuntimeException("Service id mapped to Order is not present in data source"));
        UserEntity workerById = userRepository.findById(orderEntity.getWorker().getId())
                .orElseThrow(() -> new RuntimeException("Worker id mapped to Order is not present in data source"));

        return orderEntity.toBuilder()
                .service(serviceById)
                .worker(workerById)
                .build();
    }

    @Override
    public List<Order> findAllByWorker(Integer workerId) {
        return orderRepository.findAllByWorkerId(workerId).stream()
                .map(orderMapper::mapEntityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<com.epam.bookingservice.domain.Service> findAllServices() {
        return serviceRepository.findAll().stream()
                .map(serviceMapper::mapEntityToDomain)
                .collect(Collectors.toList());

    }
}
