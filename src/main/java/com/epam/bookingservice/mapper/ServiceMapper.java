package com.epam.bookingservice.mapper;

import com.epam.bookingservice.domain.Service;
import com.epam.bookingservice.entity.ServiceEntity;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper implements Mapper<ServiceEntity, Service> {

    @Override
    public ServiceEntity mapDomainToEntity(Service domain) {
        if (domain == null) {
            return null;
        }

        return ServiceEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .durationInTimeslots(domain.getDurationInTimeslots())
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
                .durationInTimeslots(entity.getDurationInTimeslots())
                .price(entity.getPrice())
                .build();
    }
}
