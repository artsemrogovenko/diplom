package com.artsemrogovenko.diplom.accountapp.models.exceptions;

/**
 * Отсутствие переданного id.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
