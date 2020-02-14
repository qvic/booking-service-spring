package com.epam.bookingservice.mapper;

import com.epam.bookingservice.domain.Service;
import com.epam.bookingservice.entity.ServiceEntity;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ServiceMapper implements Mapper<ServiceEntity, Service> {

    private static final long TIMESLOT_DURATION = 30L;

    @Override
    public ServiceEntity mapDomainToEntity(Service domain) {
        if (domain == null) {
            return null;
        }

        return ServiceEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .durationInTimeslots(Math.toIntExact(domain.getDuration().toMinutes() / TIMESLOT_DURATION))
                .price(domain.getPrice())
                .build();
    }

    @Override
    public Service mapEntityToDomain(ServiceEntity entity) {
        if (entity == null) {
            return null;
        }

        return Service.builder()
                .id(entity.getId())
                .name(entity.getName())
                .duration(Duration.ofMinutes(entity.getDurationInTimeslots() * TIMESLOT_DURATION))
                .price(entity.getPrice())
                .build();
    }
}
