package com.salon.booking.mapper;

import com.salon.booking.domain.Order;
import com.salon.booking.domain.SalonService;
import com.salon.booking.domain.User;
import com.salon.booking.entity.OrderEntity;
import com.salon.booking.entity.ServiceEntity;
import com.salon.booking.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OrderMapper implements Mapper<OrderEntity, Order> {

    private final Mapper<UserEntity, User> userMapper;
    private final Mapper<ServiceEntity, SalonService> serviceMapper;

    @Override
    public OrderEntity mapDomainToEntity(Order order) {
        if (order == null) {
            return null;
        }

        return OrderEntity.builder()
                .id(order.getId())
                .date(order.getDate())
                .client(userMapper.mapDomainToEntity(order.getClient()))
                .worker(userMapper.mapDomainToEntity(order.getWorker()))
                .service(serviceMapper.mapDomainToEntity(order.getService()))
                .build();
    }

    @Override
    public Order mapEntityToDomain(OrderEntity order) {
        if (order == null) {
            return null;
        }

        return Order.builder()
                .id(order.getId())
                .date(order.getDate())
                .worker(userMapper.mapEntityToDomain(order.getWorker()))
                .client(userMapper.mapEntityToDomain(order.getClient()))
                .service(serviceMapper.mapEntityToDomain(order.getService()))
                .build();
    }
}
