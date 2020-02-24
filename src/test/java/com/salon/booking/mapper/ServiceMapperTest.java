package com.salon.booking.mapper;

import com.salon.booking.domain.SalonService;
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
        SalonService service = SalonService.builder()
                .id(1)
                .name("name")
                .price(100)
                .duration(Duration.ofMinutes(30))
                .build();

        ServiceEntity expectedService = ServiceEntity.builder()
                .id(1)
                .name("name")
                .price(100)
                .durationMinutes(30)
                .build();

        assertEquals(expectedService, serviceMapper.mapDomainToEntity(service));
    }

    @Test
    public void mapEntityToDomainShouldMapCorrectly() {
        ServiceEntity serviceEntity = ServiceEntity.builder()
                .id(1)
                .name("name")
                .price(100)
                .durationMinutes(30)
                .build();

        SalonService expectedService = SalonService.builder()
                .id(1)
                .name("name")
                .price(100)
                .duration(Duration.ofMinutes(30))
                .build();

        TestCase.assertEquals(expectedService, serviceMapper.mapEntityToDomain(serviceEntity));
    }

    @Test
    public void mapDomainToEntityShouldReturnNullOnNullParameter() {
        assertNull(serviceMapper.mapDomainToEntity(null));
    }

    @Test
    public void mapEntityToDomainShouldReturnNullOnNullParameter() {
        TestCase.assertNull(serviceMapper.mapEntityToDomain(null));
    }
}