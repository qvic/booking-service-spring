package com.epam.bookingservice.service.impl;

import com.epam.bookingservice.domain.Timeslot;
import com.epam.bookingservice.domain.Timetable;
import com.epam.bookingservice.entity.OrderEntity;
import com.epam.bookingservice.entity.ServiceEntity;
import com.epam.bookingservice.entity.TimeslotEntity;
import com.epam.bookingservice.entity.UserEntity;
import com.epam.bookingservice.mapper.Mapper;
import com.epam.bookingservice.respository.OrderRepository;
import com.epam.bookingservice.respository.ServiceRepository;
import com.epam.bookingservice.respository.TimeslotRepository;
import com.epam.bookingservice.respository.UserRepository;
import com.epam.bookingservice.service.TimeslotService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TimeslotServiceImpl implements TimeslotService {

    private static final Logger LOGGER = LogManager.getLogger(TimeslotServiceImpl.class);

    private final TimeslotRepository timeslotRepository;
    private final OrderRepository orderRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    private final Mapper<TimeslotEntity, Timeslot> timeslotMapper;

    public TimeslotServiceImpl(TimeslotRepository timeslotRepository, OrderRepository orderRepository,
                               ServiceRepository serviceRepository, UserRepository userRepository,
                               Mapper<TimeslotEntity, Timeslot> timeslotMapper) {
        this.timeslotRepository = timeslotRepository;
        this.orderRepository = orderRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
        this.timeslotMapper = timeslotMapper;
    }

    @Override
    public List<Timetable> findAllBetween(LocalDate fromInclusive, LocalDate toExclusive) {
        List<TimeslotEntity> timeslots = timeslotRepository.findAllByDateBetween(fromInclusive, toExclusive);
        List<Timetable> timetables = new ArrayList<>();

        Map<LocalDate, List<TimeslotEntity>> groupedTimeslots = timeslots.stream()
                .collect(Collectors.groupingBy(TimeslotEntity::getDate));

        LocalDate date = fromInclusive;
        while (date.isBefore(toExclusive)) {
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

        return timeslotMapper.mapEntityToDomain(entityWithOrder);
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
        TimeslotEntity timeslotEntity = timeslotMapper.mapDomainToEntity(timeslot);

        OrderEntity savedOrder = orderRepository.save(timeslotEntity.getOrder());
        TimeslotEntity updatedTimeslot = timeslotEntity.toBuilder()
                .order(savedOrder)
                .build();
        timeslotRepository.save(updatedTimeslot);
    }
}
