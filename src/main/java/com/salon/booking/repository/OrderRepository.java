package com.salon.booking.repository;

import com.salon.booking.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface OrderRepository extends PagingAndSortingRepository<OrderEntity, Integer> {

    Page<OrderEntity> findAllByClientId(Integer clientId, Pageable pageable);

    Page<OrderEntity> findAllByWorkerId(Integer workerId, Pageable pageable);

    @Query("SELECT DISTINCT o FROM OrderEntity o INNER JOIN o.timeslots t WHERE (t.date > ?1 OR (t.date = ?1 AND t.fromTime > ?2)) AND o.client.id = ?3")
    List<OrderEntity> findAllFinishedAfter(LocalDate date, LocalTime time, Integer clientId);
}
