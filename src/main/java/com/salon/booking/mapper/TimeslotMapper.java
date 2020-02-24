package com.salon.booking.mapper;

import com.salon.booking.domain.Order;
import com.salon.booking.domain.Timeslot;
import com.salon.booking.entity.DurationEntity;
import com.salon.booking.entity.OrderEntity;
import com.salon.booking.entity.TimeslotEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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
                .duration(mapDuration(timeslot.getDuration()))
                .date(timeslot.getDate())
                .orders(mapOrdersToEntity(timeslot.getOrders()))
                .build();
    }

    private List<OrderEntity> mapOrdersToEntity(List<Order> orders) {
        return mapList(orders, orderMapper::mapDomainToEntity);

    }

    private DurationEntity mapDuration(Duration duration) {
        return (duration == null) ? null : new DurationEntity(null, Math.toIntExact(duration.toMinutes()));
    }

    @Override
    public Timeslot mapEntityToDomain(TimeslotEntity timeslot) {
        if (timeslot == null) {
            return null;
        }

        return Timeslot.builder()
                .id(timeslot.getId())
                .fromTime(timeslot.getFromTime())
                .duration(mapDuration(timeslot.getDuration()))
                .date(timeslot.getDate())
                .orders(mapOrdersToDomain(timeslot.getOrders()))
                .build();
    }

    private List<Order> mapOrdersToDomain(List<OrderEntity> orders) {
        return mapList(orders, orderMapper::mapEntityToDomain);
    }

    private <A, B> List<B> mapList(List<A> list, Function<? super A, B> map) {
        return (list == null) ? null : list.stream().map(map).collect(Collectors.toList());
    }

    private Duration mapDuration(DurationEntity durationEntity) {
        return (durationEntity == null) ? null : Duration.ofMinutes(durationEntity.getMinutes());
    }
}
