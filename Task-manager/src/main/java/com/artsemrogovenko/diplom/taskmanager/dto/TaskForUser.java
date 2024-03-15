package com.artsemrogovenko.diplom.taskmanager.dto;

import com.artsemrogovenko.diplom.taskmanager.model.Module;
import com.artsemrogovenko.diplom.taskmanager.model.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskForUser {

    private Long id;
    private String name;   //имя
    private String description; // описание
    private Task.Status status;
    private String contractNumber; // номер договора
    private String owner; // у кого сейчас задача
    private boolean reserved;

    private List<Module> modules = new ArrayList<>();

    public enum Status {
        TO_DO,
        DONE,
        IN_PROGRESS,
    }

}
