package com.salon.booking.mapper;

import com.salon.booking.domain.Feedback;
import com.salon.booking.domain.FeedbackStatus;
import com.salon.booking.domain.Order;
import com.salon.booking.entity.FeedbackEntity;
import com.salon.booking.entity.FeedbackStatusEntity;
import com.salon.booking.entity.OrderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FeedbackMapper implements Mapper<FeedbackEntity, Feedback> {

    private final Mapper<OrderEntity, Order> orderMapper;
    private final Mapper<FeedbackStatusEntity, FeedbackStatus> feedbackStatusMapper;

    @Override
    public FeedbackEntity mapDomainToEntity(Feedback feedback) {
        if (feedback == null) {
            return null;
        }

        return FeedbackEntity.builder()
                .id(feedback.getId())
                .text(feedback.getText())
                .order(orderMapper.mapDomainToEntity(feedback.getOrder()))
                .status(FeedbackStatusEntity.CREATED)
                .build();
    }

    @Override
    public Feedback mapEntityToDomain(FeedbackEntity feedback) {
        if (feedback == null) {
            return null;
        }

        return Feedback.builder()
                .id(feedback.getId())
                .text(feedback.getText())
                .order(orderMapper.mapEntityToDomain(feedback.getOrder()))
                .status(feedbackStatusMapper.mapEntityToDomain(feedback.getStatus()))
                .build();
    }
}
