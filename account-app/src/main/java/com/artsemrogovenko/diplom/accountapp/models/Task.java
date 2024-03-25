package com.artsemrogovenko.diplom.accountapp.models;

import com.artsemrogovenko.diplom.accountapp.dto.TaskData;
import com.artsemrogovenko.diplom.accountapp.dto.TaskStatus;
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

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "module_tasks",
            joinColumns = @JoinColumn(name = "module_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id"))
    private List<Module> modules = new ArrayList<>();
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "account_tasks", joinColumns = @JoinColumn(name = "account_name"),
            inverseJoinColumns = @JoinColumn(name = "task_id", columnDefinition = "VARCHAR(50)"))
    private Account account;

    public Task(Long id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", contractNumber='" + contractNumber + '\'' +
                ", owner='" + owner + '\'' +
                ", reserved=" + reserved +
                ", modules=" + modules +
                '}';
    }


}
