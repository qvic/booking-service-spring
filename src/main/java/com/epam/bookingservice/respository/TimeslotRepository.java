package com.epam.bookingservice.respository;

import com.epam.bookingservice.entity.TimeslotEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimeslotRepository extends CrudRepository<TimeslotEntity, Integer> {

    List<TimeslotEntity> findAllByDateBetween(LocalDate from, LocalDate to);
}
