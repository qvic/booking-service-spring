package com.salon.booking.controller;

import com.salon.booking.domain.Order;
import com.salon.booking.domain.Service;
import com.salon.booking.domain.Timeslot;
import com.salon.booking.domain.Timetable;
import com.salon.booking.domain.User;
import com.salon.booking.service.OrderService;
import com.salon.booking.service.TimeslotService;
import com.salon.booking.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Log4j2
@RequiredArgsConstructor
@Controller
@RequestMapping("/client")
public class ClientController {

    private static final Period CLIENT_TIMETABLE_PERIOD = Period.ofDays(14);

    private final TimeslotService timeslotService;
    private final OrderService orderService;
    private final UserService userService;

    @GetMapping
    public String homePage() {
        return "client/index";
    }

    @GetMapping("/select-timeslot")
    public String selectTimeslotPage(Model model) {
        LocalDate now = LocalDate.now();
        List<Timetable> timetables = timeslotService.findAllBetween(now, now.plus(CLIENT_TIMETABLE_PERIOD));
        model.addAttribute("timetables", timetables);

        return "client/select-timeslot";
    }

    @PostMapping("/select-timeslot")
    public String selectTimeslot(@RequestParam(name = "timeslot-id") Integer timeslotId,
                                 HttpSession session) {
        session.setAttribute("timeslotId", timeslotId);

        return "redirect:/client/select-service";
    }

    @GetMapping("/select-service")
    public String selectServicePage(Model model, HttpSession session) {
        model.addAttribute("services", orderService.findAllServices());

        return "client/select-service";
    }

    @PostMapping("/select-service")
    public String selectService(@RequestParam(name = "service-id") Integer serviceId,
                                HttpSession session) {
        session.setAttribute("serviceId", serviceId);

        return "redirect:/client/select-worker";
    }

    @GetMapping("/select-worker")
    public String selectWorkerPage(Model model, HttpSession session) {
        model.addAttribute("workers", userService.findAllWorkers());

        return "client/select-worker";
    }

    @PostMapping("/select-worker")
    public String selectWorker(@RequestParam(name = "worker-id") Integer workerId,
                               HttpSession session) {
        session.setAttribute("workerId", workerId);

        return "redirect:/client/order";
    }

    @GetMapping("/order")
    public String orderPage() {
        return "client/create-order";
    }

    @PostMapping("/order")
    public String order(Model model, HttpSession session, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        Integer timeslotId = (Integer) session.getAttribute("timeslotId");
        Integer serviceId = (Integer) session.getAttribute("serviceId");
        Integer workerId = (Integer) session.getAttribute("workerId");

        if (Stream.of(timeslotId, serviceId, workerId).anyMatch(Objects::isNull)) {
            model.addAttribute("message", "Please, select all parameters");
            return "order";
        }

        User worker = User.builder()
                .id(workerId)
                .build();
        Service service = Service.builder()
                .id(serviceId)
                .build();

        timeslotService.updateTimeslotWithOrder(Timeslot.builder()
                .id(timeslotId)
                .order(Order.builder()
                        .date(LocalDateTime.now())
                        .client(user)
                        .worker(worker)
                        .service(service)
                        .build())
                .build());

        return "redirect:/client/orders";
    }

    @GetMapping("/orders")
    public String ordersPage(Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Order> orders = orderService.findAllByClientId(user.getId());
        model.addAttribute("orders", orders);

        return "client/orders";
    }
}
