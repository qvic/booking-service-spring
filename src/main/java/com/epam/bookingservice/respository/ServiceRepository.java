package com.epam.bookingservice.respository;

import com.epam.bookingservice.entity.ServiceEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends CrudRepository<ServiceEntity, Integer> {

     List<ServiceEntity> findAll();
}
