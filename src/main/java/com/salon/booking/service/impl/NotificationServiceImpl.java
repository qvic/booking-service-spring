package com.salon.booking.service.impl;

import com.salon.booking.domain.Notification;
import com.salon.booking.domain.Order;
import com.salon.booking.entity.NotificationEntity;
import com.salon.booking.entity.OrderEntity;
import com.salon.booking.mapper.Mapper;
import com.salon.booking.repository.NotificationRepository;
import com.salon.booking.service.NotificationService;
import com.salon.booking.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {

    private final OrderService orderService;
    private final NotificationRepository notificationRepository;
    private final Mapper<NotificationEntity, Notification> notificationMapper;

    @Override
    public List<Notification> findAllUnreadAndMarkAllAsRead(Integer userId) {
        List<Notification> notifications = notificationRepository.findAllByReadIsFalseAndOrderClientId(userId).stream()
                .map(this::buildNotificationWithOrder)
                .collect(Collectors.toList());

        markAllAsRead(userId);

        return notifications;
    }

    private void markAllAsRead(Integer userId) {
        List<NotificationEntity> updatedNotifications = notificationRepository.findAllByOrderClientId(userId).stream()
                .map(notificationEntity -> notificationEntity.toBuilder()
                        .read(true)
                        .build())
                .collect(Collectors.toList());

        notificationRepository.saveAll(updatedNotifications);
    }

    @Override
    public List<Notification> findAllRead(Integer userId) {
        return notificationRepository.findAllByReadIsTrueAndOrderClientId(userId).stream()
                .map(this::buildNotificationWithOrder)
                .collect(Collectors.toList());
    }

    private Notification buildNotificationWithOrder(NotificationEntity notificationEntity) {
        Notification notification = notificationMapper.mapEntityToDomain(notificationEntity);

        Integer orderId = notificationEntity.getOrder().getId();
        Order order = orderService.findById(orderId)
                .orElseThrow(NoSuchElementException::new);

        return new Notification(notification.getId(), order, notification.getRead());
    }

    @Override
    public long updateNotificationsReturningCount(Integer userId, LocalDateTime minOrderEndTime) {
        List<Order> lastFinishedOrders = orderService.findFinishedOrdersAfter(minOrderEndTime, userId);

        long unreadNotifications = 0;
        for (Order order : lastFinishedOrders) {
            Optional<NotificationEntity> notification = notificationRepository.findByOrderId(order.getId());

            if (notification.isPresent()) {
                if (notification.get().getRead().equals(false)) {
                    unreadNotifications++;
                }
            } else {
                Integer orderId = order.getId();
                notificationRepository.save(buildNewNotification(orderId));
                unreadNotifications++;
            }
        }
        return unreadNotifications;
    }

    private NotificationEntity buildNewNotification(Integer orderId) {
        return new NotificationEntity(
                null,
                OrderEntity.builder()
                        .id(orderId)
                        .build(),
                false);
    }
}
