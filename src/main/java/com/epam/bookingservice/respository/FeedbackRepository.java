package com.epam.bookingservice.respository;

import com.epam.bookingservice.entity.FeedbackEntity;
import com.epam.bookingservice.entity.FeedbackStatusEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends PagingAndSortingRepository<FeedbackEntity, Integer> {

    List<FeedbackEntity> findAllByWorkerId(Integer id);

    List<FeedbackEntity> findAllByStatus(FeedbackStatusEntity status);
}
