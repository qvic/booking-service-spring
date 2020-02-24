package com.salon.booking.controller;

import com.salon.booking.domain.Feedback;
import com.salon.booking.domain.FeedbackForm;
import com.salon.booking.domain.Order;
import com.salon.booking.domain.SalonService;
import com.salon.booking.domain.Timetable;
import com.salon.booking.domain.User;
import com.salon.booking.service.FeedbackService;
import com.salon.booking.service.OrderService;
import com.salon.booking.service.TimeslotService;
import com.salon.booking.service.UserService;
import com.salon.booking.utility.RequestUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import static com.salon.booking.utility.RequestUtility.getIntSessionAttribute;

@Log4j2
@RequiredArgsConstructor
@Controller
@RequestMapping("/client")
public class ClientController {

    private static final Period FEEDBACK_THRESHOLD = Period.ofDays(7);
    private static final int DEFAULT_USERS_PER_PAGE = 5;
    private static final int DEFAULT_FEEDBACK_PER_PAGE = 5;
    private static final int DEFAULT_ORDERS_PER_PAGE = 5;

    private static final String SERVICE_ID_ATTRIBUTE = "serviceId";
    private static final String TIMESLOT_ID_ATTRIBUTE = "timeslotId";
    private static final String WORKER_ID_ATTRIBUTE = "workerId";

    private final TimeslotService timeslotService;
    private final OrderService orderService;
    private final UserService userService;
    private final FeedbackService feedbackService;

    @GetMapping
    public String homePage() {
        return "client/index";
    }

    @GetMapping("/order-service")
    public String selectServicePage(Model model) {
        model.addAttribute("services", orderService.findAllServices());

        return "client/services";
    }

    @PostMapping("/order-service")
    public String selectService(@RequestParam(name = "service-id") Integer serviceId,
                                HttpSession session) {
        session.setAttribute(SERVICE_ID_ATTRIBUTE, serviceId);

        return "redirect:/client/order-worker";
    }

    @GetMapping("/order-worker")
    public String selectWorkerPage(@PageableDefault(size = DEFAULT_USERS_PER_PAGE) Pageable pageable, Model model) {
        model.addAttribute("workers", userService.findAllWorkers(pageable));

        return "client/workers";
    }

    @PostMapping("/order-worker")
    public String selectWorker(@RequestParam(name = "worker-id") Integer workerId,
                               HttpSession session) {
        session.setAttribute(WORKER_ID_ATTRIBUTE, workerId);

        return "redirect:/client/order-timeslot";
    }

    @GetMapping("/order-timeslot")
    public String selectTimeslotPage(Model model, HttpSession session) {
        Optional<Integer> serviceId = RequestUtility.getIntSessionAttribute(SERVICE_ID_ATTRIBUTE, session);
        Optional<Integer> workerId = RequestUtility.getIntSessionAttribute(WORKER_ID_ATTRIBUTE, session);

        if (!serviceId.isPresent() || !workerId.isPresent()) {
            return "redirect:/app/client/order-service";
        }

        List<Timetable> timetables = timeslotService.findTimetablesForOrderWith(serviceId.get(), workerId.get(), LocalDate.now());
        model.addAttribute("timetables", timetables);

        return "client/timetables";
    }

    @PostMapping("/order-timeslot")
    public String selectTimeslot(@RequestParam(name = "timeslot-id") Integer timeslotId,
                                 HttpSession session) {
        session.setAttribute(TIMESLOT_ID_ATTRIBUTE, timeslotId);

        return "redirect:/client/create-order";
    }

    @GetMapping("/create-order")
    public String orderPage(Model model, HttpSession session) {
        setOrderAttributes(model, session);

        return "client/create-order";
    }

    private void setOrderAttributes(Model model, HttpSession session) {
        getIntSessionAttribute(TIMESLOT_ID_ATTRIBUTE, session)
                .flatMap(timeslotService::findById)
                .ifPresent(timeslot -> model.addAttribute("timeslot", timeslot));

        getIntSessionAttribute(SERVICE_ID_ATTRIBUTE, session)
                .flatMap(orderService::findServiceById)
                .ifPresent(service -> model.addAttribute("service", service));

        getIntSessionAttribute(WORKER_ID_ATTRIBUTE, session)
                .flatMap(userService::findById)
                .ifPresent(worker -> model.addAttribute("worker", worker));
    }

    @PostMapping("/create-order")
    public String order(Model model, HttpSession session, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        Optional<Integer> timeslotId = getIntSessionAttribute(TIMESLOT_ID_ATTRIBUTE, session);
        Optional<Integer> serviceId = getIntSessionAttribute(SERVICE_ID_ATTRIBUTE, session);
        Optional<Integer> workerId = getIntSessionAttribute(WORKER_ID_ATTRIBUTE, session);

        if (!timeslotId.isPresent() || !serviceId.isPresent() || !workerId.isPresent()) {
            model.addAttribute("message", "label.select_all_parameters");
            return "order";
        }

        Order order = buildNewOrder(user, workerId.get(), serviceId.get());
        orderService.saveOrderUpdatingTimeslots(timeslotId.get(), order);

        return "redirect:/client/orders";
    }

    private Order buildNewOrder(User client, Integer workerId, Integer serviceId) {
        User worker = User.builder()
                .id(workerId)
                .build();

        SalonService service = SalonService.builder()
                .id(serviceId)
                .build();

        return Order.builder()
                .date(LocalDateTime.now())
                .client(client)
                .worker(worker)
                .service(service)
                .build();
    }

    @GetMapping("/orders")
    public String ordersPage(@PageableDefault(size = DEFAULT_ORDERS_PER_PAGE) Pageable pageable,
                             Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Page<Order> orders = orderService.findAllByClientId(user.getId(), pageable);
        model.addAttribute("page", orders);

        return "client/orders";
    }

    @GetMapping("/feedback")
    public String feedbackPage(@PageableDefault(size = DEFAULT_FEEDBACK_PER_PAGE) Pageable pageable,
                               Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        Page<Feedback> approvedFeedbackPage = feedbackService.findAllByClientId(user.getId(), pageable);
        model.addAttribute("page", approvedFeedbackPage);

        return "client/feedback";
    }

    @GetMapping("/leave-feedback")
    public String leaveFeedbackPage(@RequestParam(name = "order-id", required = false) Integer orderId,
                                    Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        model.addAttribute("feedback", new FeedbackForm());
        model.addAttribute("selectedOrderId", orderId);

        List<Order> orders = orderService.findFinishedOrdersAfter(getMinimalOrderEndTimeToLeaveFeedback(), user.getId());
        model.addAttribute("orders", orders);

        return "client/leave-feedback";
    }

    @PostMapping("/leave-feedback")
    public String leaveFeedbackPage(@Valid @ModelAttribute("feedback") FeedbackForm feedbackForm,
                                    BindingResult bindingResult,
                                    Model model, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "client/leave-feedback";
        }

        User user = (User) authentication.getPrincipal();
        feedbackService.saveFeedback(user, feedbackForm, getMinimalOrderEndTimeToLeaveFeedback());

        return "redirect:/client/feedback";
    }

    private LocalDateTime getMinimalOrderEndTimeToLeaveFeedback() {
        return LocalDateTime.now().minus(FEEDBACK_THRESHOLD);
    }
}
