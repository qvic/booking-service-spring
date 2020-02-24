package com.salon.booking.service.impl;

import com.salon.booking.domain.Feedback;
import com.salon.booking.domain.FeedbackForm;
import com.salon.booking.domain.FeedbackStatus;
import com.salon.booking.domain.Order;
import com.salon.booking.domain.User;
import com.salon.booking.entity.FeedbackEntity;
import com.salon.booking.entity.FeedbackStatusEntity;
import com.salon.booking.entity.OrderEntity;
import com.salon.booking.mapper.Mapper;
import com.salon.booking.repository.FeedbackRepository;
import com.salon.booking.service.FeedbackService;
import com.salon.booking.service.OrderService;
import com.salon.booking.service.exception.NoSuchItemException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final OrderService orderService;
    private final Mapper<FeedbackEntity, Feedback> feedbackMapper;
    private final Mapper<FeedbackStatusEntity, FeedbackStatus> feedbackStatusMapper;

    @Override
    public Page<Feedback> findAllByWorkerId(Integer workerId, Pageable properties) {
        return feedbackRepository.findAllByOrderWorkerIdAndStatus(workerId, FeedbackStatusEntity.APPROVED, properties)
                .map(this::buildFeedbackWithOrder);
    }

    @Override
    public Page<Feedback> findAllByClientId(Integer clientId, Pageable properties) {
        return feedbackRepository.findAllByOrderClientId(clientId, properties)
                .map(this::buildFeedbackWithOrder);
    }

    private Feedback buildFeedbackWithOrder(FeedbackEntity feedbackEntity) {
        Feedback feedback = feedbackMapper.mapEntityToDomain(feedbackEntity);

        Integer orderId = feedbackEntity.getOrder().getId();
        Order order = orderService.findById(orderId)
                .orElseThrow(NoSuchElementException::new);

        return feedback.toBuilder()
                .order(order)
                .build();
    }

    @Override
    public Page<Feedback> findAllByStatus(FeedbackStatus status, Pageable properties) {
        return feedbackRepository.findAllByStatus(feedbackStatusMapper.mapDomainToEntity(status), properties)
                .map(feedbackMapper::mapEntityToDomain);
    }

    @Override
    public void approveFeedbackById(Integer feedbackId) {
        Optional<FeedbackEntity> feedback = feedbackRepository.findById(feedbackId)
                .map(feedbackEntity -> feedbackEntity.toBuilder()
                        .status(FeedbackStatusEntity.APPROVED)
                        .build());

        if (!feedback.isPresent()) {
            throw new NoSuchItemException();
        }

        feedbackRepository.save(feedback.get());
    }

    @Override
    public void saveFeedback(User user, FeedbackForm feedbackForm, LocalDateTime minOrderEndTime) {
        boolean canLeaveFeedback = canLeaveFeedbackAbout(feedbackForm.getOrderId(), user.getId(), minOrderEndTime);

        if (canLeaveFeedback) {
            OrderEntity order = OrderEntity.builder()
                    .id(feedbackForm.getOrderId())
                    .build();

            FeedbackEntity feedback = FeedbackEntity.builder()
                    .order(order)
                    .text(feedbackForm.getText())
                    .status(FeedbackStatusEntity.CREATED)
                    .build();

            feedbackRepository.save(feedback);
        }
    }

    private boolean canLeaveFeedbackAbout(Integer orderId, Integer clientId, LocalDateTime minTime) {
        List<Order> finishedOrders = orderService.findFinishedOrdersAfter(minTime, clientId);
        return finishedOrders.stream()
                .anyMatch(o -> orderId.equals(o.getId()));
    }

}
