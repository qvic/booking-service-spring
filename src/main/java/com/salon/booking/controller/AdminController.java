package com.salon.booking.controller;

import com.salon.booking.domain.Feedback;
import com.salon.booking.domain.FeedbackStatus;
import com.salon.booking.domain.Timetable;
import com.salon.booking.domain.User;
import com.salon.booking.service.FeedbackService;
import com.salon.booking.service.TimeslotService;
import com.salon.booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final int DEFAULT_USERS_PER_PAGE = 5;
    private static final int DEFAULT_FEEDBACK_PER_PAGE = 5;
    private static final Period DEFAULT_TIMETABLE_PERIOD = Period.ofDays(14);

    private final UserService userService;
    private final FeedbackService feedbackService;
    private final TimeslotService timeslotService;

    @GetMapping
    public String homePage() {
        return "admin/index";
    }

    @PostMapping("/promote-client")
    public String promoteClient(@RequestParam(name = "client-id") Integer clientId) {
        userService.promoteToWorker(clientId);

        return "redirect:/admin/clients";
    }

    @GetMapping("/clients")
    public String showClients(@PageableDefault(size = DEFAULT_USERS_PER_PAGE) Pageable pageable,
                              Model model) {
        Page<User> users = userService.findAllClients(pageable);
        model.addAttribute("page", users);
        return "admin/clients";
    }

    @GetMapping("/workers")
    public String showWorkers(@PageableDefault(size = DEFAULT_USERS_PER_PAGE) Pageable pageable,
                              Model model) {
        Page<User> users = userService.findAllWorkers(pageable);
        model.addAttribute("page", users);
        return "admin/workers";
    }

    @GetMapping("/feedback")
    public String showUnapprovedFeedback(@PageableDefault(size = DEFAULT_FEEDBACK_PER_PAGE) Pageable pageable,
                                         Model model) {
        Page<Feedback> feedbackPage = feedbackService.findAllByStatus(FeedbackStatus.CREATED, pageable);
        model.addAttribute("page", feedbackPage);

        return "admin/feedback";
    }

    @PostMapping("/approve-feedback")
    public String approveFeedback(@RequestParam(name = "feedback-id") Integer feedbackId,
                                  Model model) {
        feedbackService.approveFeedbackById(feedbackId);

        return "redirect:/admin/feedback";
    }

    @GetMapping("/timetable")
    public String showTimetable(@RequestParam(name = "from-date", required = false)
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDateNullable,
                                @RequestParam(name = "to-date", required = false)
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDateNullable,
                                Model model) {
        LocalDate from = Optional.ofNullable(fromDateNullable).orElse(LocalDate.now());
        LocalDate to = Optional.ofNullable(toDateNullable).orElse(from.plus(DEFAULT_TIMETABLE_PERIOD));

        List<Timetable> timetables = timeslotService.findAllBetween(from, to);
        model.addAttribute("timetables", timetables);

        return "admin/timetables";
    }
}
