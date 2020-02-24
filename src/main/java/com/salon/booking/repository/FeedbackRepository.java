package com.salon.booking.repository;

import com.salon.booking.entity.FeedbackEntity;
import com.salon.booking.entity.FeedbackStatusEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends PagingAndSortingRepository<FeedbackEntity, Integer> {

    Page<FeedbackEntity> findAllByOrderWorkerIdAndStatus(Integer workerId, FeedbackStatusEntity status, Pageable properties);

    Page<FeedbackEntity> findAllByOrderClientId(Integer clientId, Pageable properties);

    Page<FeedbackEntity> findAllByStatus(FeedbackStatusEntity status, Pageable properties);
}
