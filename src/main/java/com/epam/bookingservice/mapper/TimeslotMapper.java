package com.epam.bookingservice.mapper;

import com.epam.bookingservice.domain.Order;
import com.epam.bookingservice.domain.Timeslot;
import com.epam.bookingservice.entity.OrderEntity;
import com.epam.bookingservice.entity.TimeslotEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TimeslotMapper implements Mapper<TimeslotEntity, Timeslot> {

    private final Mapper<OrderEntity, Order> orderMapper;

    @Override
    public TimeslotEntity mapDomainToEntity(Timeslot domain) {
        if (domain == null) {
            return null;
        }

        return TimeslotEntity.builder()
                .id(domain.getId())
                .fromTime(domain.getFromTime())
                .toTime(domain.getToTime())
                .date(domain.getDate())
                .order(orderMapper.mapDomainToEntity(domain.getOrder()))
                .build();
    }

    @Override
    public Timeslot mapEntityToDomain(TimeslotEntity entity) {
        if (entity == null) {
            return null;
        }

        return Timeslot.builder()
                .id(entity.getId())
                .fromTime(entity.getFromTime())
                .toTime(entity.getToTime())
                .date(entity.getDate())
                .order(orderMapper.mapEntityToDomain(entity.getOrder()))
                .build();
    }
}
