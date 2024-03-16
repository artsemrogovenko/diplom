package com.artsemrogovenko.diplom.accountapp.models;
import com.artsemrogovenko.diplom.accountapp.dto.TaskStatus;
import com.artsemrogovenko.diplom.accountapp.models.Module;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Task {
    @Id
    private Long id;
    private String name;   //имя
    private String description; // описание
    private TaskStatus status;
    private String contractNumber; // номер договора
    private String owner; // у кого сейчас задача
    private boolean reserved;
    @OneToMany // список модулей
    private List<Module> modules = new ArrayList<>();
    @ManyToOne
    private Account account;




}
