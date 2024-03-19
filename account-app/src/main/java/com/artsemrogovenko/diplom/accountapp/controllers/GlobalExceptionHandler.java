package com.artsemrogovenko.diplom.accountapp.controllers;

import com.artsemrogovenko.diplom.accountapp.aspect.LogMethod;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @LogMethod
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ModelAndView handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        ModelAndView modelAndView = new ModelAndView("errorPage"); // Перенаправление на главную страницу
        modelAndView.addObject("errorInfo", ex.getMessage()+ ex.getBody());
        return modelAndView;
    }

    @LogMethod
    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNoResourceFoundException(NoHandlerFoundException ex) {
        ModelAndView modelAndView = new ModelAndView("errorPage"); // Перенаправление на главную страницу
        modelAndView.addObject("errorInfo", ex.getMessage()+ ex.getBody());
        return modelAndView;
    }

//    @LogMethod
//    @ExceptionHandler(value = FeignException.InternalServerError.class)
//    public ModelAndView errorPage(FeignException ex) {
//        ModelAndView modelAndView = new ModelAndView("/errorPage"); // Перенаправление на главную страницу
//        modelAndView.addObject("errorInfo", ex.getMessage()+ ex.getCause());
//        return modelAndView;
//    }

    @LogMethod
    @ExceptionHandler(value = EntityNotFoundException.class)
    public ModelAndView errorPage(EntityNotFoundException ex) {
        ModelAndView modelAndView = new ModelAndView("errorPage"); // Перенаправление на главную страницу
        modelAndView.addObject("errorInfo", ex.getMessage()+ ex.getCause());
        return modelAndView;
    }

    @LogMethod
    @ExceptionHandler(value = NoResourceFoundException.class)
    public ModelAndView errorPage(NoResourceFoundException ex) {
        ModelAndView modelAndView = new ModelAndView("errorPage"); // Перенаправление на главную страницу
        modelAndView.addObject("message", "404 Not Found");
        modelAndView.addObject("errorInfo", ex.getMessage()+ ex.getBody());
        return modelAndView;
    }

    @LogMethod
    @ExceptionHandler(value = FeignException.MethodNotAllowed.class)
    public ModelAndView notsupported(FeignException ex) {
        ModelAndView modelAndView = new ModelAndView("errorPage"); // Перенаправление на главную страницу
        modelAndView.addObject("errorInfo", ex.getMessage()+ ex.contentUTF8());
        return modelAndView;
    }

}
