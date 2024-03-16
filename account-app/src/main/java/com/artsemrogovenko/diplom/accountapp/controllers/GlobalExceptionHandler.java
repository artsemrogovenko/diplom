package com.artsemrogovenko.diplom.accountapp.controllers;

import com.artsemrogovenko.diplom.accountapp.aspect.LogMethod;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @LogMethod
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        return "index.html"; // Перенаправление на страницу
    }

    @LogMethod
    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNoResourceFoundException(NoHandlerFoundException ex) {
        return "index.html"; // Перенаправление на страницу
    }
}
