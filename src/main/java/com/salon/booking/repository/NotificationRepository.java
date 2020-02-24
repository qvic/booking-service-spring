package com.salon.booking.repository;

import com.salon.booking.entity.NotificationEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends CrudRepository<NotificationEntity, Integer> {

    List<NotificationEntity> findAllByReadIsFalseAndOrderClientId(Integer userId);

    Optional<NotificationEntity> findByOrderId(Integer orderId);

    List<NotificationEntity> findAllByReadIsTrueAndOrderClientId(Integer userId);

    List<NotificationEntity> findAllByOrderClientId(Integer userId);
}
