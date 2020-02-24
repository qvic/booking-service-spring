package com.salon.booking.service.impl;

import com.salon.booking.domain.SalonService;
import com.salon.booking.domain.Timeslot;
import com.salon.booking.domain.Timetable;
import com.salon.booking.domain.User;
import com.salon.booking.entity.OrderEntity;
import com.salon.booking.entity.ServiceEntity;
import com.salon.booking.entity.TimeslotEntity;
import com.salon.booking.entity.UserEntity;
import com.salon.booking.mapper.Mapper;
import com.salon.booking.repository.OrderRepository;
import com.salon.booking.repository.ServiceRepository;
import com.salon.booking.repository.TimeslotRepository;
import com.salon.booking.repository.UserRepository;
import com.salon.booking.service.TimeslotService;
import com.salon.booking.service.exception.NoSuchItemException;
import com.salon.booking.utility.StreamUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import static com.salon.booking.utility.ListUtility.getIndexOfFirstMatch;

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

    /**
     * @param serviceId selected service id
     * @param workerId  selected worker id
     * @return timetables for defined as DEFAULT_TIMETABLE_PERIOD period of days
     * with timeslots where order with such service id and worker id can be placed
     */
    @Override
    public List<Timetable> findTimetablesForOrderWith(Integer serviceId, Integer workerId, LocalDate currentDate) {
        LocalDate to = currentDate.plus(DEFAULT_TIMETABLE_PERIOD);
        List<Timetable> timetablesBetween = findAllBetween(currentDate, to);

        SalonService service = SalonService.builder()
                .id(serviceId)
                .build();

        User worker = User.builder()
                .id(workerId)
                .build();

        List<Timetable> viewTimetables = new ArrayList<>();
        for (Timetable timetable : timetablesBetween) {

            List<Timeslot> viewTimeslots = new ArrayList<>();
            for (Timeslot timeslot : timetable.getRows()) {

                List<Timeslot> freeTimeslots = findFirstAcceptableTimeslotsForOrder(
                        timeslot.getId(), timetable.getRows(), service, worker);

                if (!freeTimeslots.isEmpty()) {
                    Timeslot viewTimeslot = timeslot.toBuilder()
                            .duration(getTotalDuration(freeTimeslots))
                            .build();

                    viewTimeslots.add(viewTimeslot);
                }
            }

            viewTimetables.add(new Timetable(timetable.getDate(), viewTimeslots));
        }

        return viewTimetables;
    }

    private Duration getTotalDuration(List<Timeslot> timeslots) {
        return Duration.ofMinutes(timeslots.stream()
                .mapToLong(t -> t.getDuration().toMinutes())
                .sum());
    }

    /**
     * @param fromInclusive first day in timetable
     * @param toExclusive   day after the last day in timetable
     * @return timetables for each day from fromInclusive to toExclusive
     */
    @Override
    public List<Timetable> findAllBetween(LocalDate fromInclusive, LocalDate toExclusive) {
        List<TimeslotEntity> timeslots = timeslotRepository.findAllByDateBetweenOrderByDateAscFromTimeAsc(fromInclusive, toExclusive);
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

    @Override
    public List<Timetable> findAllBetweenForWorker(Integer workerId, LocalDate fromInclusive, LocalDate toExclusive) {
        return findAllBetween(fromInclusive, toExclusive).stream()
                .map(timetable -> getTimetableFilteredByWorker(workerId, timetable))
                .collect(Collectors.toList());
    }

    private Timetable getTimetableFilteredByWorker(Integer workerId, Timetable timetable) {
        return new Timetable(timetable.getDate(), timetable.getRows().stream()
                .map(timeslot -> getTimeslotFilteredByWorker(workerId, timeslot))
                .collect(Collectors.toList()));
    }

    private Timeslot getTimeslotFilteredByWorker(Integer workerId, Timeslot timeslot) {
        return timeslot.toBuilder()
                .orders(timeslot.getOrders().stream()
                        .filter(order -> workerId.equals(order.getWorker().getId()))
                        .collect(Collectors.toList()))
                .build();
    }

    private Timeslot buildTimeslot(TimeslotEntity timeslotEntity) {
        List<OrderEntity> orders = timeslotEntity.getOrders().stream()
                .map(orderEntity -> orderRepository.findById(orderEntity.getId()))
                .flatMap(StreamUtility::toStream)
                .map(this::buildOrderEntity)
                .collect(Collectors.toList());

        return timeslotMapper.mapEntityToDomain(
                timeslotEntity.toBuilder()
                        .orders(orders)
                        .build());
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
                .orElseThrow(() -> new IllegalArgumentException("Can't find ServiceEntity with such id"));
    }

    private UserEntity getClient(OrderEntity orderEntity) {
        return userRepository.findById(orderEntity.getClient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Can't find UserEntity with such id"));
    }

    private UserEntity getWorker(OrderEntity orderEntity) {
        return userRepository.findById(orderEntity.getWorker().getId())
                .orElseThrow(() -> new IllegalArgumentException("Can't find UserEntity with such id"));
    }

    /**
     * Service can have duration of more than 1 timeslot.
     * With passed selected timeslot id, service and worker searches for consecutive timeslots
     * where order with such service can be placed. If it can't, returns empty list.
     *
     * @param selectedTimeslotId timeslot id, selected on order creation
     * @param service            selected service
     * @param worker             selected worker
     * @return list of timeslots where order can be placed
     */
    @Override
    public List<Timeslot> findTimeslotsForOrderWith(Integer selectedTimeslotId,
                                                    SalonService service, User worker) {

        List<Timeslot> timeslots = getSameDayTimeslots(selectedTimeslotId);
        return findFirstAcceptableTimeslotsForOrder(selectedTimeslotId, timeslots, service, worker);
    }

    private List<Timeslot> findFirstAcceptableTimeslotsForOrder(Integer timeslotId, List<Timeslot> timeslots, SalonService service, User worker) {
        OptionalInt startingTimeslotIndex = getIndexOfFirstMatch(timeslots, t -> t.getId().equals(timeslotId));
        if (!startingTimeslotIndex.isPresent()) {
            return Collections.emptyList();
        }
        List<Timeslot> timeslotsAfterSelectedId = timeslots.subList(startingTimeslotIndex.getAsInt(), timeslots.size());

        Duration serviceDuration = getServiceDuration(service);
        Duration currentFreeDuration = Duration.ZERO;

        List<Timeslot> freeTimeslots = new ArrayList<>();
        Timeslot previousTimeslot = null;

        for (Timeslot timeslot : timeslotsAfterSelectedId) {

            if (currentFreeDuration.compareTo(serviceDuration) < 0) {
                boolean isValidTimeslot = isConsecutiveTimeslot(previousTimeslot, timeslot) &&
                        areServiceAndWorkerAvailable(timeslot, service.getId(), worker.getId());

                if (isValidTimeslot) {
                    freeTimeslots.add(timeslot);
                    currentFreeDuration = currentFreeDuration.plus(timeslot.getDuration());
                } else {
                    return Collections.emptyList();
                }
            } else {
                break;
            }

            previousTimeslot = timeslot;
        }

        if (currentFreeDuration.compareTo(serviceDuration) < 0) {
            return Collections.emptyList();
        }

        return freeTimeslots;
    }

    private List<Timeslot> getSameDayTimeslots(Integer timeslotId) {
        TimeslotEntity timeslot = timeslotRepository.findById(timeslotId)
                .orElseThrow(NoSuchItemException::new);

        return timeslotRepository.findAllByDateOrderByDateAscFromTimeAsc(timeslot.getDate()).stream()
                .map(timeslotMapper::mapEntityToDomain)
                .collect(Collectors.toList());
    }

    private Duration getServiceDuration(SalonService service) {
        return Duration.ofMinutes(serviceRepository.findById(service.getId())
                .orElseThrow(() -> new IllegalArgumentException("Can't find service with such id"))
                .getDurationMinutes());
    }

    private boolean isConsecutiveTimeslot(Timeslot previous, Timeslot current) {
        if (previous == null) {
            return true;
        }

        LocalTime previousTimeslotEndTime = previous.getFromTime().plus(previous.getDuration());
        LocalTime currentTimeslotStartTime = current.getFromTime();

        return previousTimeslotEndTime.equals(currentTimeslotStartTime);
    }

    private boolean areServiceAndWorkerAvailable(Timeslot timeslot, Integer serviceId, Integer workerId) {
        return timeslot.getOrders().stream()
                .map(o -> orderRepository.findById(o.getId()))
                .flatMap(StreamUtility::toStream)
                .allMatch(o -> !o.getService().getId().equals(serviceId) && !o.getWorker().getId().equals(workerId));
    }

    @Override
    public void saveOrderTimeslot(Integer timeslotId, Integer orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(NoSuchItemException::new);
        TimeslotEntity timeslotEntity = timeslotRepository.findById(timeslotId)
                .orElseThrow(NoSuchItemException::new);

        timeslotEntity.getOrders().add(orderEntity);
        timeslotRepository.save(timeslotEntity);
    }

    @Override
    public Optional<Timeslot> findById(Integer id) {
        return timeslotRepository.findById(id)
                .map(timeslotMapper::mapEntityToDomain);
    }
}
