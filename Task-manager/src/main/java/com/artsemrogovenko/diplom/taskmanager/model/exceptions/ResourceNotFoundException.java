package com.artsemrogovenko.diplom.taskmanager.model.exceptions;

/**
 * не найден.
 */
public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
