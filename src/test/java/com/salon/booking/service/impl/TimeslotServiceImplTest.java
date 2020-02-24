package com.salon.booking.service.impl;

import com.salon.booking.domain.Order;
import com.salon.booking.domain.SalonService;
import com.salon.booking.domain.Timeslot;
import com.salon.booking.domain.Timetable;
import com.salon.booking.domain.User;
import com.salon.booking.entity.DurationEntity;
import com.salon.booking.entity.OrderEntity;
import com.salon.booking.entity.ServiceEntity;
import com.salon.booking.entity.TimeslotEntity;
import com.salon.booking.entity.UserEntity;
import com.salon.booking.mapper.Mapper;
import com.salon.booking.repository.OrderRepository;
import com.salon.booking.repository.ServiceRepository;
import com.salon.booking.repository.TimeslotRepository;
import com.salon.booking.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TimeslotServiceImplTest {

    private static final LocalDate FROM_DATE = LocalDate.of(2020, 2, 4);
    private static final LocalDate TO_DATE = LocalDate.of(2020, 2, 9);

    private static final List<OrderEntity> ORDER_ENTITIES = initOrderEntities();

    private static final List<TimeslotEntity> TIMESLOT_ENTITIES = initTimeslotEntities();
    private static final List<Timetable> TIMETABLES = initTimetables();
    private static final List<TimeslotEntity> TIMESLOT_ENTITIES_BY_DAY = initTimeslotEntitiesByDay();


    @Mock
    private TimeslotRepository timeslotRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Mapper<TimeslotEntity, Timeslot> timeslotMapper;

    private TimeslotServiceImpl timeslotService;

    @Before
    public void injectMocks() {
        timeslotService = new TimeslotServiceImpl(timeslotRepository, orderRepository, serviceRepository, userRepository, timeslotMapper);
    }

    @After
    public void resetMocks() {
        reset(timeslotRepository, orderRepository, serviceRepository, userRepository, timeslotMapper);
    }

    @Test
    public void findTimetablesForOrderWithShouldReturnCorrectListServiceIsUnavailable() {
        when(timeslotRepository.findAllByDateBetweenOrderByDateAscFromTimeAsc(any(), any())).thenReturn(TIMESLOT_ENTITIES);
        when(serviceRepository.findById(anyInt())).thenAnswer(this::mapService);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(UserEntity.builder().build()));
        when(orderRepository.findById(anyInt())).thenAnswer(invocation -> Optional.of(ORDER_ENTITIES.get(invocation.getArgument(0))));
        when(timeslotMapper.mapEntityToDomain(any())).thenAnswer(this::mapTimeslot);

        List<Timetable> timetables = timeslotService.findTimetablesForOrderWith(1, 1, LocalDate.of(2020, 2, 4));

        List<Integer> unpackedIds = timetables.stream()
                .flatMap(timetable -> timetable.getRows().stream())
                .map(Timeslot::getId)
                .collect(Collectors.toList());

        assertThat(unpackedIds, equalTo(Arrays.asList(123, 125, 126)));
    }

    @Test
    public void findTimetablesForOrderWithShouldReturnCorrectListWhenWorkerIsUnavailable() {
        when(timeslotRepository.findAllByDateBetweenOrderByDateAscFromTimeAsc(any(), any())).thenReturn(TIMESLOT_ENTITIES);
        when(serviceRepository.findById(anyInt())).thenAnswer(this::mapService);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(UserEntity.builder().build()));
        when(orderRepository.findById(anyInt())).thenAnswer(invocation -> Optional.of(ORDER_ENTITIES.get(invocation.getArgument(0))));
        when(timeslotMapper.mapEntityToDomain(any())).thenAnswer(this::mapTimeslot);

        List<Timetable> timetables = timeslotService.findTimetablesForOrderWith(1, 2, LocalDate.of(2020, 2, 4));

        List<Integer> unpackedIds = timetables.stream()
                .flatMap(timetable -> timetable.getRows().stream())
                .map(Timeslot::getId)
                .collect(Collectors.toList());

        assertThat(unpackedIds, equalTo(Collections.singletonList(123)));
    }

    private Object mapTimeslot(InvocationOnMock invocation) {
        TimeslotEntity t = invocation.getArgument(0);
        return Timeslot.builder()
                .id(t.getId())
                .date(t.getDate())
                .orders(mapTestOrderEntities(t.getOrders()))
                .duration(Duration.ofMinutes(t.getDuration().getMinutes()))
                .build();
    }

    private Optional<ServiceEntity> mapService(InvocationOnMock invocation) {
        Integer id = invocation.getArgument(0);
        return Optional.of(ServiceEntity.builder()
                .id(id)
                .durationMinutes(id < 3 ? 30 : 60)
                .build());
    }

    @Test
    public void findAllBetweenShouldReturnCorrectList() {
        when(serviceRepository.findById(anyInt())).thenAnswer(
                invocation -> Optional.of(ServiceEntity.builder().id(invocation.getArgument(0)).build()));
        when(userRepository.findById(anyInt())).thenAnswer(
                invocation -> Optional.of(UserEntity.builder().id(invocation.getArgument(0)).build()));
        when(orderRepository.findById(anyInt())).thenAnswer(
                invocation -> Optional.of(ORDER_ENTITIES.get(0)));
        when(timeslotMapper.mapEntityToDomain(any())).thenAnswer(this::mapTimeslot);
        when(timeslotRepository.findAllByDateBetweenOrderByDateAscFromTimeAsc(eq(FROM_DATE), eq(TO_DATE))).thenReturn(TIMESLOT_ENTITIES);

        List<Timetable> timetables = timeslotService.findAllBetween(FROM_DATE, TO_DATE);

        assertThat(timetables, equalTo(TIMETABLES));
    }

    @Test
    public void findTimeslotsForServiceWithWorkerShouldReturnEmptyListWhenWorkerIsBusy() {
        when(timeslotRepository.findAllByDateOrderByDateAscFromTimeAsc(any())).thenReturn(TIMESLOT_ENTITIES_BY_DAY);
        when(serviceRepository.findById(anyInt())).thenReturn(Optional.of(ServiceEntity.builder()
                .durationMinutes(30)
                .build()));
        when(orderRepository.findById(anyInt())).thenAnswer(
                invocation -> Optional.of(ORDER_ENTITIES.get(invocation.getArgument(0))));
        when(timeslotMapper.mapEntityToDomain(any())).thenAnswer(this::mapTimeslot);

        SalonService service = SalonService.builder()
                .id(2)
                .build();
        User worker = User.builder()
                .id(1)
                .build();

        List<Timeslot> consecutiveFreeTimeslots = timeslotService.findTimeslotsForOrderWith(
                122, service, worker);

        assertThat(consecutiveFreeTimeslots, equalTo(Collections.emptyList()));
    }

    @Test
    public void findTimeslotsForServiceWithWorkerShouldReturnEmptyListWhenServiceIsUnavailable() {
        when(timeslotRepository.findAllByDateOrderByDateAscFromTimeAsc(any())).thenReturn(TIMESLOT_ENTITIES_BY_DAY);
        when(serviceRepository.findById(anyInt())).thenReturn(Optional.of(ServiceEntity.builder()
                .durationMinutes(30)
                .build()));
        when(orderRepository.findById(anyInt())).thenAnswer(
                invocation -> Optional.of(ORDER_ENTITIES.get(invocation.getArgument(0))));
        when(timeslotMapper.mapEntityToDomain(any())).thenAnswer(this::mapTimeslot);

        SalonService service = SalonService.builder()
                .id(1)
                .build();
        User worker = User.builder()
                .id(2)
                .build();

        List<Timeslot> consecutiveFreeTimeslots = timeslotService.findTimeslotsForOrderWith(
                122, service, worker);

        assertThat(consecutiveFreeTimeslots, equalTo(Collections.emptyList()));
    }

    @Test
    public void findTimeslotsForServiceWithWorkerShouldReturnCorrectListIfServiceAndWorkerAreAvailable() {
        when(timeslotRepository.findAllByDateOrderByDateAscFromTimeAsc(any())).thenReturn(TIMESLOT_ENTITIES_BY_DAY);
        when(serviceRepository.findById(anyInt())).thenReturn(Optional.of(ServiceEntity.builder()
                .durationMinutes(30)
                .build()));
        when(orderRepository.findById(anyInt())).thenAnswer(
                invocation -> Optional.of(ORDER_ENTITIES.get(invocation.getArgument(0))));
        when(timeslotMapper.mapEntityToDomain(any())).thenAnswer(
                invocation -> {
                    TimeslotEntity t = invocation.getArgument(0);
                    return Timeslot.builder()
                            .id(t.getId())
                            .orders(mapTestOrderEntities(t.getOrders()))
                            .fromTime(t.getFromTime())
                            .duration(Duration.ofMinutes(t.getDuration().getMinutes()))
                            .build();
                });

        SalonService service = SalonService.builder()
                .id(3)
                .duration(Duration.ofMinutes(30))
                .build();
        User worker = User.builder()
                .id(3)
                .build();

        List<Timeslot> consecutiveFreeTimeslots = timeslotService.findTimeslotsForOrderWith(
                122, service, worker);

        List<Integer> timeslotIds = consecutiveFreeTimeslots.stream()
                .map(Timeslot::getId)
                .collect(Collectors.toList());

        assertThat(timeslotIds, equalTo(Collections.singletonList(122)));
    }

    @Test
    public void findTimeslotsForServiceWithWorkerShouldReturnEmptyListIfServiceIntersectsOther() {
        when(timeslotRepository.findAllByDateOrderByDateAscFromTimeAsc(any())).thenReturn(TIMESLOT_ENTITIES_BY_DAY);
        when(serviceRepository.findById(anyInt())).thenReturn(Optional.of(ServiceEntity.builder()
                .durationMinutes(90)
                .build()));
        when(orderRepository.findById(anyInt())).thenAnswer(
                invocation -> Optional.of(ORDER_ENTITIES.get(invocation.getArgument(0))));
        when(timeslotMapper.mapEntityToDomain(any())).thenAnswer(
                invocation -> {
                    TimeslotEntity t = invocation.getArgument(0);
                    return Timeslot.builder()
                            .id(t.getId())
                            .orders(mapTestOrderEntities(t.getOrders()))
                            .fromTime(t.getFromTime())
                            .duration(Duration.ofMinutes(t.getDuration().getMinutes()))
                            .build();
                });

        SalonService service = SalonService.builder()
                .id(3)
                .build();
        User worker = User.builder()
                .id(3)
                .build();

        List<Timeslot> consecutiveFreeTimeslots = timeslotService.findTimeslotsForOrderWith(
                122, service, worker);

        List<Integer> timeslotIds = consecutiveFreeTimeslots.stream()
                .map(Timeslot::getId)
                .collect(Collectors.toList());

        assertThat(timeslotIds, equalTo(Collections.emptyList()));
    }

    @Test
    public void findTimeslotsForServiceWithWorkerShouldReturnCorrectListIfServiceTakesSeveralTimeslots() {
        when(timeslotRepository.findAllByDateOrderByDateAscFromTimeAsc(any())).thenReturn(TIMESLOT_ENTITIES_BY_DAY);
        when(serviceRepository.findById(anyInt())).thenReturn(Optional.of(ServiceEntity.builder()
                .durationMinutes(50)
                .build()));
        when(orderRepository.findById(anyInt())).thenAnswer(
                invocation -> Optional.of(ORDER_ENTITIES.get(invocation.getArgument(0))));
        when(timeslotMapper.mapEntityToDomain(any())).thenAnswer(
                invocation -> {
                    TimeslotEntity t = invocation.getArgument(0);
                    return Timeslot.builder()
                            .id(t.getId())
                            .fromTime(t.getFromTime())
                            .orders(mapTestOrderEntities(t.getOrders()))
                            .duration(Duration.ofMinutes(t.getDuration().getMinutes()))
                            .build();
                });

        SalonService service = SalonService.builder()
                .id(3)
                .build();
        User worker = User.builder()
                .id(3)
                .build();

        List<Timeslot> consecutiveFreeTimeslots = timeslotService.findTimeslotsForOrderWith(
                122, service, worker);

        List<Integer> timeslotIds = consecutiveFreeTimeslots.stream()
                .map(Timeslot::getId)
                .collect(Collectors.toList());

        assertThat(timeslotIds, equalTo(Arrays.asList(122, 123)));
    }

    @Test
    public void findTimeslotsForServiceWithWorkerShouldReturnEmptyListIfThereIsPauseBetweenTimeslots() {
        when(timeslotRepository.findAllByDateOrderByDateAscFromTimeAsc(any())).thenReturn(TIMESLOT_ENTITIES_BY_DAY);
        when(serviceRepository.findById(anyInt())).thenReturn(Optional.of(ServiceEntity.builder()
                .durationMinutes(60)
                .build()));
        when(orderRepository.findById(anyInt())).thenAnswer(
                invocation -> Optional.of(ORDER_ENTITIES.get(invocation.getArgument(0))));
        when(timeslotMapper.mapEntityToDomain(any())).thenAnswer(
                invocation -> {
                    TimeslotEntity t = invocation.getArgument(0);
                    return Timeslot.builder()
                            .id(t.getId())
                            .fromTime(t.getFromTime())
                            .orders(mapTestOrderEntities(t.getOrders()))
                            .duration(Duration.ofMinutes(t.getDuration().getMinutes()))
                            .build();
                });

        SalonService service = SalonService.builder()
                .id(1)
                .build();
        User user = User.builder()
                .id(1)
                .build();

        List<Timeslot> consecutiveFreeTimeslots = timeslotService.findTimeslotsForOrderWith(
                125, service, user);

        assertThat(consecutiveFreeTimeslots, equalTo(Collections.emptyList()));
    }

    private static List<Order> mapTestOrderEntities(List<OrderEntity> orderEntities) {
        return orderEntities.stream()
                .map(orderEntity -> Order.builder().id(orderEntity.getId()).build())
                .collect(Collectors.toList());
    }

    private static List<OrderEntity> initOrderEntities() {
        return Arrays.asList(
                OrderEntity.builder()
                        .id(0)
                        .client(UserEntity.builder()
                                .id(1)
                                .build())
                        .service(ServiceEntity.builder()
                                .id(1)
                                .build())
                        .worker(UserEntity.builder()
                                .id(1)
                                .build())
                        .build(),
                OrderEntity.builder()
                        .id(1)
                        .client(UserEntity.builder()
                                .id(1)
                                .build())
                        .service(ServiceEntity.builder()
                                .id(1)
                                .build())
                        .worker(UserEntity.builder()
                                .id(2)
                                .build())
                        .build(),
                OrderEntity.builder()
                        .id(2)
                        .client(UserEntity.builder()
                                .id(1)
                                .build())
                        .service(ServiceEntity.builder()
                                .id(2)
                                .build())
                        .worker(UserEntity.builder()
                                .id(1)
                                .build())
                        .build(),
                OrderEntity.builder()
                        .id(3)
                        .client(UserEntity.builder()
                                .id(1)
                                .build())
                        .service(ServiceEntity.builder()
                                .id(3)
                                .build())
                        .worker(UserEntity.builder()
                                .id(2)
                                .build())
                        .build()
        );
    }

    private static List<Order> initOrders() {
        return Arrays.asList(
                Order.builder()
                        .service(SalonService.builder()
                                .id(1)
                                .build())
                        .worker(User.builder()
                                .id(1)
                                .build())
                        .build(),
                Order.builder()
                        .service(SalonService.builder()
                                .id(1)
                                .build())
                        .worker(User.builder()
                                .id(2)
                                .build())
                        .build(),
                Order.builder()
                        .service(SalonService.builder()
                                .id(1)
                                .build())
                        .worker(User.builder()
                                .id(1)
                                .build())
                        .build(),
                Order.builder()
                        .service(SalonService.builder()
                                .id(3)
                                .build())
                        .worker(User.builder()
                                .id(2)
                                .build())
                        .build()
        );
    }

    private static List<TimeslotEntity> initTimeslotEntitiesByDay() {
        DurationEntity duration = new DurationEntity(1, 30);

        return Arrays.asList(
                TimeslotEntity.builder()
                        .id(122)
                        .date(LocalDate.of(2020, 2, 2))
                        .fromTime(LocalTime.of(8, 0))
                        .duration(duration)
                        .orders(Collections.singletonList(ORDER_ENTITIES.get(0)))
                        .build(),
                TimeslotEntity.builder()
                        .id(123)
                        .date(LocalDate.of(2020, 2, 2))
                        .fromTime(LocalTime.of(8, 30))
                        .duration(duration)
                        .orders(Arrays.asList(
                                ORDER_ENTITIES.get(1), ORDER_ENTITIES.get(2)
                        ))
                        .build(),
                TimeslotEntity.builder()
                        .id(124)
                        .date(LocalDate.of(2020, 2, 2))
                        .fromTime(LocalTime.of(9, 0))
                        .duration(duration)
                        .orders(Collections.singletonList(
                                ORDER_ENTITIES.get(3)
                        ))
                        .build(),
                TimeslotEntity.builder()
                        .id(125)
                        .date(LocalDate.of(2020, 2, 2))
                        .fromTime(LocalTime.of(9, 30))
                        .duration(duration)
                        .orders(Collections.singletonList(
                                ORDER_ENTITIES.get(3)
                        ))
                        .build(),
                TimeslotEntity.builder()
                        .id(126)
                        .date(LocalDate.of(2020, 2, 2))
                        .fromTime(LocalTime.of(10, 30))
                        .duration(duration)
                        .build()
        );
    }

    private static List<TimeslotEntity> initTimeslotEntities() {
        DurationEntity duration = new DurationEntity(1, 30);

        return Arrays.asList(
                TimeslotEntity.builder()
                        .id(122)
                        .date(LocalDate.of(2020, 2, 5))
                        .orders(Collections.singletonList(ORDER_ENTITIES.get(0)))
                        .fromTime(LocalTime.of(8, 0))
                        .duration(duration)
                        .build(),
                TimeslotEntity.builder()
                        .id(123)
                        .date(LocalDate.of(2020, 2, 5))
                        .fromTime(LocalTime.of(8, 30))
                        .duration(duration)
                        .build(),
                TimeslotEntity.builder()
                        .id(124)
                        .date(LocalDate.of(2020, 2, 7))
                        .orders(Arrays.asList(ORDER_ENTITIES.get(1), ORDER_ENTITIES.get(2)))
                        .duration(duration)
                        .build(),
                TimeslotEntity.builder()
                        .id(125)
                        .date(LocalDate.of(2020, 2, 8))
                        .orders(Collections.singletonList(ORDER_ENTITIES.get(3)))
                        .duration(duration)
                        .build(),
                TimeslotEntity.builder()
                        .id(126)
                        .date(LocalDate.of(2020, 2, 8))
                        .orders(Collections.singletonList(ORDER_ENTITIES.get(3)))
                        .duration(duration)
                        .build()
        );
    }

    private static List<Timetable> initTimetables() {
        Order order = Order.builder().build();
        Duration duration = Duration.ofMinutes(30);

        return Arrays.asList(
                new Timetable(LocalDate.of(2020, 2, 4), Collections.emptyList()),
                new Timetable(LocalDate.of(2020, 2, 5),
                        Arrays.asList(
                                Timeslot.builder()
                                        .id(122)
                                        .duration(duration)
                                        .date(LocalDate.of(2020, 2, 5))
                                        .orders(Collections.singletonList(order))
                                        .build(),
                                Timeslot.builder()
                                        .id(123)
                                        .duration(duration)
                                        .date(LocalDate.of(2020, 2, 5))
                                        .build())
                ),
                new Timetable(LocalDate.of(2020, 2, 6), Collections.emptyList()),
                new Timetable(LocalDate.of(2020, 2, 7),
                        Collections.singletonList(Timeslot.builder()
                                .id(124)
                                .duration(duration)
                                .date(LocalDate.of(2020, 2, 7))
                                .orders(Arrays.asList(order, order))
                                .build())
                ),
                new Timetable(LocalDate.of(2020, 2, 8),
                        Arrays.asList(
                                Timeslot.builder()
                                        .id(125)
                                        .duration(duration)
                                        .date(LocalDate.of(2020, 2, 8))
                                        .orders(Collections.singletonList(order))
                                        .build(),
                                Timeslot.builder()
                                        .id(126)
                                        .duration(duration)
                                        .date(LocalDate.of(2020, 2, 8))
                                        .orders(Collections.singletonList(order))
                                        .build())
                ));
    }
}