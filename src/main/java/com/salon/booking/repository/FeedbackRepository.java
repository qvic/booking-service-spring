package com.salon.booking.repository;

import com.salon.booking.entity.FeedbackStatusEntity;
import com.salon.booking.entity.FeedbackEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends PagingAndSortingRepository<FeedbackEntity, Integer> {

    List<FeedbackEntity> findAllByWorkerId(Integer id);

    List<FeedbackEntity> findAllByStatus(FeedbackStatusEntity status);
}
