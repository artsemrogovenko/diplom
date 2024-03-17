package com.artsemrogovenko.diplom.accountapp.dto;

import com.artsemrogovenko.diplom.accountapp.models.Task;
public class TaskMapper {
    public static Task mapToTask(TaskForUser taskForUser) {
        Task task = new Task();

        task.setId(taskForUser.getId());
        task.setName(taskForUser.getName());
        task.setDescription(taskForUser.getDescription());
        task.setStatus(taskForUser.getTaskStatus());
        task.setContractNumber(taskForUser.getContractNumber());
        task.setOwner(taskForUser.getOwner());
        task.setReserved(taskForUser.isReserved());
        task.setModules(taskForUser.getModules());

        return task;

    }
}