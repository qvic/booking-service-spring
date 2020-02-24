package com.salon.booking.service;

import com.salon.booking.domain.Feedback;
import com.salon.booking.domain.FeedbackForm;
import com.salon.booking.domain.FeedbackStatus;
import com.salon.booking.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface FeedbackService {

    Page<Feedback> findAllByWorkerId(Integer workerId, Pageable properties);

    Page<Feedback> findAllByClientId(Integer clientId, Pageable properties);

    Page<Feedback> findAllByStatus(FeedbackStatus status, Pageable properties);

    void approveFeedbackById(Integer feedbackId);

    void saveFeedback(User user, FeedbackForm feedbackForm, LocalDateTime minOrderEndTime);
}
