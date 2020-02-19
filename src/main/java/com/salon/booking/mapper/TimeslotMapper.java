package com.salon.booking.mapper;

import com.salon.booking.domain.Order;
import com.salon.booking.domain.Timeslot;
import com.salon.booking.entity.OrderEntity;
import com.salon.booking.entity.TimeslotEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TimeslotMapper implements Mapper<TimeslotEntity, Timeslot> {

    private final Mapper<OrderEntity, Order> orderMapper;

    @Override
    public TimeslotEntity mapDomainToEntity(Timeslot timeslot) {
        if (timeslot == null) {
            return null;
        }

        return TimeslotEntity.builder()
                .id(timeslot.getId())
                .fromTime(timeslot.getFromTime())
                .toTime(timeslot.getToTime())
                .date(timeslot.getDate())
                .order(orderMapper.mapDomainToEntity(timeslot.getOrder()))
                .build();
    }

    @Override
    public Timeslot mapEntityToDomain(TimeslotEntity timeslot) {
        if (timeslot == null) {
            return null;
        }

        return Timeslot.builder()
                .id(timeslot.getId())
                .fromTime(timeslot.getFromTime())
                .toTime(timeslot.getToTime())
                .date(timeslot.getDate())
                .order(orderMapper.mapEntityToDomain(timeslot.getOrder()))
                .build();
    }
}
