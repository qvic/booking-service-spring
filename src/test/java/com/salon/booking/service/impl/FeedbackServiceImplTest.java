package com.salon.booking.service.impl;

import com.salon.booking.domain.Feedback;
import com.salon.booking.domain.FeedbackStatus;
import com.salon.booking.entity.FeedbackEntity;
import com.salon.booking.entity.FeedbackStatusEntity;
import com.salon.booking.entity.OrderEntity;
import com.salon.booking.mapper.Mapper;
import com.salon.booking.repository.FeedbackRepository;
import com.salon.booking.service.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FeedbackServiceImplTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private Mapper<FeedbackEntity, Feedback> feedbackMapper;

    @Mock
    private Mapper<FeedbackStatusEntity, FeedbackStatus> feedbackStatusMapper;

    private FeedbackServiceImpl feedbackService;

    @Before
    public void injectMocks() {
        feedbackService = new FeedbackServiceImpl(feedbackRepository, orderService, feedbackMapper, feedbackStatusMapper);
    }

    @Test
    public void findAllByWorkerId() {
        FeedbackEntity feedbackEntity = FeedbackEntity.builder()
                .id(1)
                .text("test")
                .order(OrderEntity.builder()
                        .id(23)
                        .build())
                .build();

        Feedback feedback = Feedback.builder()
                .id(1)
                .text("test")
                .build();

        Pageable properties = PageRequest.of(0, 1);
        Page<FeedbackEntity> feedbackEntityPage = new PageImpl<>(Collections.singletonList(feedbackEntity),
                properties, 1);

        when(feedbackRepository.findAllByOrderWorkerIdAndStatus(eq(23), eq(FeedbackStatusEntity.APPROVED), eq(properties))).thenReturn(feedbackEntityPage);
        when(feedbackMapper.mapEntityToDomain(eq(feedbackEntity))).thenReturn(feedback);

        Page<Feedback> allByWorkerId = feedbackService.findAllByWorkerId(23, properties);

        assertThat(allByWorkerId.getContent(), equalTo(Collections.singletonList(feedback)));
    }

    @Test
    public void findAllByStatus() {
        FeedbackEntity feedbackEntity = FeedbackEntity.builder()
                .id(1)
                .text("test")
                .status(FeedbackStatusEntity.CREATED)
                .build();

        Feedback feedback = Feedback.builder()
                .id(1)
                .text("test")
                .build();

        Pageable properties = PageRequest.of(0, 1);
        Page<FeedbackEntity> feedbackEntityPage = new PageImpl<>(Collections.singletonList(feedbackEntity),
                properties, 1);

        when(feedbackRepository.findAllByStatus(eq(FeedbackStatusEntity.CREATED), eq(properties))).thenReturn(feedbackEntityPage);
        when(feedbackMapper.mapEntityToDomain(eq(feedbackEntity))).thenReturn(feedback);
        when(feedbackStatusMapper.mapDomainToEntity(eq(FeedbackStatus.CREATED))).thenReturn(FeedbackStatusEntity.CREATED);

        Page<Feedback> allByStatus = feedbackService.findAllByStatus(FeedbackStatus.CREATED, properties);

        assertThat(allByStatus.getContent(), equalTo(Collections.singletonList(feedback)));
    }

    @Test
    public void approveFeedbackById() {
        feedbackService.approveFeedbackById(123);

        verify(feedbackRepository).findById(eq(123));
        verify(feedbackRepository).save(any());
    }
}