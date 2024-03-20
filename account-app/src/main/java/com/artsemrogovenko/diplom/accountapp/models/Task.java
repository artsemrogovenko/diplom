package com.artsemrogovenko.diplom.accountapp.models;

import com.artsemrogovenko.diplom.accountapp.dto.TaskData;
import com.artsemrogovenko.diplom.accountapp.dto.TaskStatus;
import com.artsemrogovenko.diplom.accountapp.models.Module;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Task implements TaskData {
    @Id
    private Long id;
    private String name;   //имя
    private String description; // описание
    private TaskStatus status;
    private String contractNumber; // номер договора
    private String owner; // у кого сейчас задача
    private boolean reserved;
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "module_tasks",
            joinColumns = @JoinColumn(name = "module_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id"))
    private List<Module> modules = new ArrayList<>();
@ManyToOne(fetch = FetchType.LAZY)
private Account account;
    @Override
    public TaskStatus getTaskStatus() {
        return status;
    }


}
