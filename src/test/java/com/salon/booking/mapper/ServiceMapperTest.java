package com.salon.booking.mapper;

import com.salon.booking.domain.Service;
import com.salon.booking.entity.ServiceEntity;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

public class ServiceMapperTest {

    private ServiceMapper serviceMapper;

    @Before
    public void before() {
        serviceMapper = new ServiceMapper();
    }

    @Test
    public void mapDomainToEntityShouldMapCorrectly() {
        Service service = Service.builder()
                .id(1)
                .name("name")
                .price(100)
                .duration(Duration.ofMinutes(30))
                .build();

        ServiceEntity expectedService = ServiceEntity.builder()
                .id(1)
                .name("name")
                .price(100)
                .durationInTimeslots(1)
                .build();

        TestCase.assertEquals(expectedService, serviceMapper.mapDomainToEntity(service));
    }

    @Test
    public void mapEntityToDomainShouldMapCorrectly() {
        ServiceEntity serviceEntity = ServiceEntity.builder()
                .id(1)
                .name("name")
                .price(100)
                .durationInTimeslots(1)
                .build();

        Service expectedService = Service.builder()
                .id(1)
                .name("name")
                .price(100)
                .duration(Duration.ofMinutes(30))
                .build();

        TestCase.assertEquals(expectedService, serviceMapper.mapEntityToDomain(serviceEntity));
    }

    @Test
    public void mapDomainToEntityShouldReturnNullOnNullParameter() {
        TestCase.assertNull(serviceMapper.mapDomainToEntity(null));
    }

    @Test
    public void mapEntityToDomainShouldReturnNullOnNullParameter() {
        TestCase.assertNull(serviceMapper.mapEntityToDomain(null));
    }
}