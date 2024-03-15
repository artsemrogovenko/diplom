package com.artsemrogovenko.diplom.accountapp.models.exceptions;

public class DuplicateExeption extends IllegalStateException {
    public DuplicateExeption(String message) {
        super(message);
    }
}
