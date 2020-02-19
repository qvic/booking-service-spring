package com.salon.booking.mapper;

import com.salon.booking.domain.Order;
import com.salon.booking.domain.Service;
import com.salon.booking.entity.OrderEntity;
import com.salon.booking.entity.ServiceEntity;
import com.salon.booking.domain.User;
import com.salon.booking.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OrderMapper implements Mapper<OrderEntity, Order> {

    private final Mapper<UserEntity, User> userMapper;
    private final Mapper<ServiceEntity, Service> serviceMapper;

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
