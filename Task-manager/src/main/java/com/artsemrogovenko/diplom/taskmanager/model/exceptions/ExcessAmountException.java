package com.artsemrogovenko.diplom.taskmanager.model.exceptions;


public class ExcessAmountException extends RuntimeException {
    public ExcessAmountException(String message) {
        super(message);
    }
}
