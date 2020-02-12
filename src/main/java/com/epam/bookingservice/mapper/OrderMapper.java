package com.epam.bookingservice.mapper;

import com.epam.bookingservice.domain.Order;
import com.epam.bookingservice.domain.Service;
import com.epam.bookingservice.domain.User;
import com.epam.bookingservice.entity.OrderEntity;
import com.epam.bookingservice.entity.ServiceEntity;
import com.epam.bookingservice.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper implements Mapper<OrderEntity, Order> {

    private Mapper<UserEntity, User> userMapper;
    private Mapper<ServiceEntity, Service> serviceMapper;

    public OrderMapper(Mapper<UserEntity, User> userMapper, Mapper<ServiceEntity, Service> serviceMapper) {
        this.userMapper = userMapper;
        this.serviceMapper = serviceMapper;
    }

    @Override
    public OrderEntity mapDomainToEntity(Order domain) {
        if (domain == null) {
            return null;
        }

        return OrderEntity.builder()
                .date(domain.getDate())
                .client(userMapper.mapDomainToEntity(domain.getClient()))
                .worker(userMapper.mapDomainToEntity(domain.getWorker()))
                .service(serviceMapper.mapDomainToEntity(domain.getService()))
                .build();
    }

    @Override
    public Order mapEntityToDomain(OrderEntity entity) {
        if (entity == null) {
            return null;
        }

        return Order.builder()
                .date(entity.getDate())
                .worker(userMapper.mapEntityToDomain(entity.getWorker()))
                .client(userMapper.mapEntityToDomain(entity.getClient()))
                .service(serviceMapper.mapEntityToDomain(entity.getService()))
                .build();
    }
}
