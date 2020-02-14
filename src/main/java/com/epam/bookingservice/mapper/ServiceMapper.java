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
                .durationInTimeslots(mapDuration(domain.getDuration()))
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
                .duration(mapDurationInTimeslots(entity.getDurationInTimeslots()))
                .price(entity.getPrice())
                .build();
    }

    private Duration mapDurationInTimeslots(Integer durationInTimeslots) {
        return durationInTimeslots == null ?
                null : Duration.ofMinutes(durationInTimeslots * TIMESLOT_DURATION);
    }

    private Integer mapDuration(Duration duration) {
        return duration == null ?
                null : Math.toIntExact(duration.toMinutes() / TIMESLOT_DURATION);
    }
}
