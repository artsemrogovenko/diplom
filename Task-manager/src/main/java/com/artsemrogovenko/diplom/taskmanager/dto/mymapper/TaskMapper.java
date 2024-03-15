package com.artsemrogovenko.diplom.taskmanager.dto.mymapper;

import com.artsemrogovenko.diplom.taskmanager.dto.TaskForUser;
import com.artsemrogovenko.diplom.taskmanager.model.Task;


public class TaskMapper {
    public static TaskForUser convertTask(Task task){
        return new TaskForUser().builder()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .status(task.getStatus())
                .contractNumber(task.getContractNumber())
                .owner(task.getOwner())
                .reserved(task.isReserved())
                .modules(task.getModules())
                .build();
    }
}
