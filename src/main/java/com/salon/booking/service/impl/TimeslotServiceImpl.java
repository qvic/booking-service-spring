package com.salon.booking.service.impl;

import com.salon.booking.domain.Timeslot;
import com.salon.booking.domain.Timetable;
import com.salon.booking.entity.OrderEntity;
import com.salon.booking.entity.ServiceEntity;
import com.salon.booking.entity.TimeslotEntity;
import com.salon.booking.entity.UserEntity;
import com.salon.booking.mapper.Mapper;
import com.salon.booking.repository.ServiceRepository;
import com.salon.booking.repository.TimeslotRepository;
import com.salon.booking.repository.UserRepository;
import com.salon.booking.service.TimeslotService;
import com.salon.booking.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class TimeslotServiceImpl implements TimeslotService {

    private static final Period DEFAULT_TIMETABLE_PERIOD = Period.ofDays(14);

    private final TimeslotRepository timeslotRepository;
    private final OrderRepository orderRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final Mapper<TimeslotEntity, Timeslot> timeslotMapper;

    @Override
    public List<Timetable> findAllBetween(LocalDate fromOrNull, LocalDate toOrNull) {
        LocalDate from = fromOrNull == null ? LocalDate.now() : fromOrNull;
        LocalDate to = toOrNull == null ? from.plus(DEFAULT_TIMETABLE_PERIOD) : toOrNull;

        List<TimeslotEntity> timeslots = timeslotRepository.findAllByDateBetween(from, to);
        List<Timetable> timetables = new ArrayList<>();

        Map<LocalDate, List<TimeslotEntity>> groupedTimeslots = timeslots.stream()
                .collect(Collectors.groupingBy(TimeslotEntity::getDate));

        LocalDate date = from;
        while (date.isBefore(to)) {
            List<TimeslotEntity> timeslotEntities = groupedTimeslots.getOrDefault(date, Collections.emptyList());

            List<Timeslot> rows = timeslotEntities.stream()
                    .map(this::buildTimeslot)
                    .collect(Collectors.toList());

            timetables.add(new Timetable(date, rows));
            date = date.plusDays(1);
        }

        return timetables;
    }

    private Timeslot buildTimeslot(TimeslotEntity timeslotEntity) {
        Optional<OrderEntity> order = Optional.ofNullable(timeslotEntity.getOrder())
                .flatMap(orderEntity -> orderRepository.findById(orderEntity.getId()))
                .map(this::buildOrderEntity);

        TimeslotEntity entityWithOrder = timeslotEntity.toBuilder()
                .order(order.orElse(null))
                .build();

        log.debug(entityWithOrder);
        Timeslot timeslot = timeslotMapper.mapEntityToDomain(entityWithOrder);
        log.debug(timeslot);
        return timeslot;
    }

    private OrderEntity buildOrderEntity(OrderEntity orderEntity) {
        return orderEntity.toBuilder()
                .worker(getWorker(orderEntity))
                .client(getClient(orderEntity))
                .service(getService(orderEntity))
                .build();
    }

    private ServiceEntity getService(OrderEntity orderEntity) {
        return serviceRepository.findById(orderEntity.getService().getId())
                .orElse(null);
    }

    private UserEntity getClient(OrderEntity orderEntity) {
        return userRepository.findById(orderEntity.getClient().getId())
                .orElse(null);
    }

    private UserEntity getWorker(OrderEntity orderEntity) {
        return userRepository.findById(orderEntity.getWorker().getId())
                .orElse(null);
    }

    @Override
    @Transactional
    public void updateTimeslotWithOrder(Timeslot timeslot) {
        TimeslotEntity timeslotEntityWithOrder = timeslotMapper.mapDomainToEntity(timeslot);

        TimeslotEntity fetchedTimeslotEntity = timeslotRepository.findById(timeslotEntityWithOrder.getId())
                .orElseThrow(() -> new IllegalArgumentException("Not found timeslot with id [" + timeslot.getId() + "]"));

        OrderEntity savedOrder = orderRepository.save(timeslotEntityWithOrder.getOrder());
        TimeslotEntity updatedTimeslotEntity = fetchedTimeslotEntity.toBuilder()
                .order(savedOrder)
                .build();

        timeslotRepository.save(updatedTimeslotEntity);
    }
}
