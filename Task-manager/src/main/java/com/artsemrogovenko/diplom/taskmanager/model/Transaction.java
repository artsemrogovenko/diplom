package com.artsemrogovenko.diplom.taskmanager.model;

import lombok.Data;

/**
 * Объект с данными для резервирования задачи.
 */
@Data
public class Transaction{
    private Long destination;
    private Long sender;
    private Task task;

    public Transaction(Long destination, Long sender, Task task) {
        this.destination = destination;
        this.sender = sender;
        this.task = task;
    }
}
