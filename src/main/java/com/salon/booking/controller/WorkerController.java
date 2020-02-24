package com.salon.booking.controller;

import com.salon.booking.domain.Feedback;
import com.salon.booking.domain.Timetable;
import com.salon.booking.domain.User;
import com.salon.booking.service.FeedbackService;
import com.salon.booking.service.TimeslotService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/worker")
public class WorkerController {

    private static final Period DEFAULT_TIMETABLE_PERIOD = Period.ofDays(14);
    private static final int DEFAULT_FEEDBACK_PER_PAGE = 5;

    private final FeedbackService feedbackService;
    private final TimeslotService timeslotService;

    @GetMapping
    public String homePage() {
        return "worker/index";
    }

    @GetMapping("/timetable")
    public String showTimetable(@RequestParam(name = "from-date", required = false)
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDateNullable,
                                @RequestParam(name = "to-date", required = false)
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDateNullable,
                                Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        LocalDate from = Optional.ofNullable(fromDateNullable).orElse(LocalDate.now());
        LocalDate to = Optional.ofNullable(toDateNullable).orElse(from.plus(DEFAULT_TIMETABLE_PERIOD));

        List<Timetable> timetables = timeslotService.findAllBetweenForWorker(user.getId(), from, to);
        model.addAttribute("timetables", timetables);

        return "worker/timetables";
    }

    @GetMapping("/feedback")
    public String showFeedback(@PageableDefault(size = DEFAULT_FEEDBACK_PER_PAGE) Pageable pageable,
                               Model model, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Page<Feedback> feedbackPage = feedbackService.findAllByWorkerId(user.getId(), pageable);
        model.addAttribute("page", feedbackPage);

        return "worker/feedback";
    }
}
