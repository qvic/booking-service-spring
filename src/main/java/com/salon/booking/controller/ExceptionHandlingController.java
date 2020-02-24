package com.salon.booking.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Log4j2
@ControllerAdvice
public class ExceptionHandlingController {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNotFoundStatus() {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("exception", "404");
        modelAndView.addObject("exceptionMessage", "Not found");
        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleError(HttpServletRequest request, Exception exception) {
        log.error("Request: " + request.getRequestURL() + " raised " + exception);

        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("exception", exception.toString());
        modelAndView.addObject("exceptionMessage", exception.getMessage());
        return modelAndView;
    }
}