package com.salon.booking.service.impl;

import com.salon.booking.domain.Order;
import com.salon.booking.domain.SalonService;
import com.salon.booking.domain.Timeslot;
import com.salon.booking.entity.OrderEntity;
import com.salon.booking.entity.ServiceEntity;
import com.salon.booking.entity.UserEntity;
import com.salon.booking.mapper.Mapper;
import com.salon.booking.repository.OrderRepository;
import com.salon.booking.repository.ServiceRepository;
import com.salon.booking.repository.UserRepository;
import com.salon.booking.service.OrderService;
import com.salon.booking.service.TimeslotService;
import com.salon.booking.service.exception.NoSuchItemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final TimeslotService timeslotService;

    private final OrderRepository orderRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    private final Mapper<OrderEntity, Order> orderMapper;
    private final Mapper<ServiceEntity, SalonService> serviceMapper;

    @Override
    public Page<Order> findAllByClientId(Integer clientId, Pageable pageable) {
        return orderRepository.findAllByClientId(clientId, pageable)
                .map(this::fillOrderEntity)
                .map(orderMapper::mapEntityToDomain);
    }

    @Override
    public Page<Order> findAllByWorkerId(Integer workerId, Pageable pageable) {
        return orderRepository.findAllByWorkerId(workerId, pageable)
                .map(orderMapper::mapEntityToDomain);
    }

    @Override
    @Transactional
    public void saveOrderUpdatingTimeslots(Integer selectedTimeslotId, Order order) {
        OrderEntity orderEntity = orderMapper.mapDomainToEntity(order);

        OrderEntity savedOrder = orderRepository.save(orderEntity);

        List<Timeslot> freeTimeslots = timeslotService.findTimeslotsForOrderWith(
                selectedTimeslotId, order.getService(), order.getWorker());

        assignOrderToTimeslots(freeTimeslots, savedOrder.getId());

    }

    @Override
    public List<Order> findFinishedOrdersAfter(LocalDateTime dateTime, Integer clientId) {
        return orderRepository.findAllFinishedAfter(dateTime.toLocalDate(), dateTime.toLocalTime(), clientId).stream()
                .map(this::fillOrderEntity)
                .map(orderMapper::mapEntityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SalonService> findAllServices() {
        return serviceRepository.findAll().stream()
                .map(serviceMapper::mapEntityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<SalonService> findServiceById(Integer id) {
        return serviceRepository.findById(id)
                .map(serviceMapper::mapEntityToDomain);
    }

    @Override
    public Optional<Order> findById(Integer id) {
        return orderRepository.findById(id)
                .map(this::fillOrderEntity)
                .map(orderMapper::mapEntityToDomain);
    }

    private OrderEntity fillOrderEntity(OrderEntity orderEntity) {
        ServiceEntity service = serviceRepository.findById(orderEntity.getService().getId())
                .orElseThrow(NoSuchItemException::new);
        UserEntity worker = userRepository.findById(orderEntity.getWorker().getId())
                .orElseThrow(NoSuchItemException::new);
        UserEntity client = userRepository.findById(orderEntity.getClient().getId())
                .orElseThrow(NoSuchItemException::new);

        return orderEntity.toBuilder()
                .service(service)
                .worker(worker)
                .client(client)
                .build();
    }

    private void assignOrderToTimeslots(List<Timeslot> timeslots, Integer orderId) {
        timeslots.forEach(timeslot -> timeslotService.saveOrderTimeslot(timeslot.getId(), orderId));
    }
}
