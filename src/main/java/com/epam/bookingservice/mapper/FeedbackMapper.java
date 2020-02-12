package com.epam.bookingservice.mapper;

import com.epam.bookingservice.domain.Feedback;
import com.epam.bookingservice.domain.User;
import com.epam.bookingservice.entity.FeedbackEntity;
import com.epam.bookingservice.entity.FeedbackStatusEntity;
import com.epam.bookingservice.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class FeedbackMapper implements Mapper<FeedbackEntity, Feedback> {

    private Mapper<UserEntity, User> userMapper;

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
