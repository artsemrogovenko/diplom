package com.artsemrogovenko.diplom.accountapp.models.exceptions;


public class ExcessAmountException extends RuntimeException {
    public ExcessAmountException(String message) {
        super(message);
    }
}
