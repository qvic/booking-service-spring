package com.salon.booking.mapper;

import com.salon.booking.domain.Feedback;
import com.salon.booking.entity.FeedbackStatusEntity;
import com.salon.booking.domain.User;
import com.salon.booking.entity.FeedbackEntity;
import com.salon.booking.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FeedbackMapper implements Mapper<FeedbackEntity, Feedback> {

    private final Mapper<UserEntity, User> userMapper;
    
    @Override
    public FeedbackEntity mapDomainToEntity(Feedback domain) {
        if (domain == null) {
            return null;
        }

        return FeedbackEntity.builder()
                .text(domain.getText())
                .status(FeedbackStatusEntity.CREATED)
                .worker(userMapper.mapDomainToEntity(domain.getWorker()))
                .build();
    }

    @Override
    public Feedback mapEntityToDomain(FeedbackEntity entity) {
        if (entity == null) {
            return null;
        }

        return Feedback.builder()
                .text(entity.getText())
                .worker(userMapper.mapEntityToDomain(entity.getWorker()))
                .build();
    }
}
