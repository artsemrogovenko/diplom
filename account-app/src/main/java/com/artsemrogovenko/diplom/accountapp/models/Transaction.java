package com.artsemrogovenko.diplom.accountapp.models;

import lombok.Data;

/**
 * Объект для проведения обмена.
 */
@Data
public class Transaction {
    private Long destination;
    private Long sender;
    private Task task;

}
