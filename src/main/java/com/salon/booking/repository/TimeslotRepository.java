package com.salon.booking.repository;

import com.salon.booking.entity.TimeslotEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimeslotRepository extends CrudRepository<TimeslotEntity, Integer> {

    List<TimeslotEntity> findAllByDateBetweenOrderByDateAscFromTimeAsc(LocalDate from, LocalDate to);

    List<TimeslotEntity> findAllByDateOrderByDateAscFromTimeAsc(LocalDate date);
}
