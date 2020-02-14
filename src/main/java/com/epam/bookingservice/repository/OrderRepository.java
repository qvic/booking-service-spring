package com.epam.bookingservice.repository;

import com.epam.bookingservice.entity.OrderEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface OrderRepository extends PagingAndSortingRepository<OrderEntity, Integer> {

    List<OrderEntity> findAllByClientId(Integer id);

    List<OrderEntity> findAllByWorkerId(Integer id);
}
