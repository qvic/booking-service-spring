package com.salon.booking.mapper;

import com.salon.booking.domain.SalonService;
import com.salon.booking.entity.ServiceEntity;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ServiceMapper implements Mapper<ServiceEntity, SalonService> {

    @Override
    public ServiceEntity mapDomainToEntity(SalonService service) {
        if (service == null) {
            return null;
        }

        return ServiceEntity.builder()
                .id(service.getId())
                .name(service.getName())
                .durationMinutes(mapDurationMinutes(service.getDuration()))
                .price(service.getPrice())
                .build();
    }

    private Integer mapDurationMinutes(Duration duration) {
        return (duration == null) ? null :
                Math.toIntExact(duration.toMinutes());
    }

    @Override
    public SalonService mapEntityToDomain(ServiceEntity service) {
        if (service == null) {
            return null;
        }

        return SalonService.builder()
                .id(service.getId())
                .name(service.getName())
                .duration(mapDuration(service.getDurationMinutes()))
                .price(service.getPrice())
                .build();
    }

    private Duration mapDuration(Integer durationMinutes) {
        return (durationMinutes == null) ? null :
                Duration.ofMinutes(durationMinutes);
    }
}
