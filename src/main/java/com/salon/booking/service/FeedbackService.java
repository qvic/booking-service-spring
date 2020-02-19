package com.salon.booking.service;

import com.salon.booking.domain.Feedback;
import com.salon.booking.domain.FeedbackStatus;

import java.util.List;

public interface FeedbackService {

    List<Feedback> findAllByWorkerId(Integer workerId);

    List<Feedback> findAllByStatus(FeedbackStatus status);
}
