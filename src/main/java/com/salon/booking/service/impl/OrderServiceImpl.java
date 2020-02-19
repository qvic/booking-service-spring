package com.salon.booking.service.impl;

import com.salon.booking.domain.Order;
import com.salon.booking.entity.OrderEntity;
import com.salon.booking.entity.ServiceEntity;
import com.salon.booking.entity.UserEntity;
import com.salon.booking.mapper.Mapper;
import com.salon.booking.repository.ServiceRepository;
import com.salon.booking.repository.UserRepository;
import com.salon.booking.service.OrderService;
import com.salon.booking.repository.OrderRepository;
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
    private final Mapper<ServiceEntity, com.salon.booking.domain.Service> serviceMapper;

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
    public List<com.salon.booking.domain.Service> findAllServices() {
        return serviceRepository.findAll().stream()
                .map(serviceMapper::mapEntityToDomain)
                .collect(Collectors.toList());

    }
}
