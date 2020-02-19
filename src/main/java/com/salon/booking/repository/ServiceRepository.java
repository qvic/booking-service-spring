package com.salon.booking.repository;

import com.salon.booking.entity.ServiceEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends CrudRepository<ServiceEntity, Integer> {

     List<ServiceEntity> findAll();
}
