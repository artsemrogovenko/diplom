package com.artsemrogovenko.diplom.accountapp.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.artsemrogovenko.diplom.accountapp.models.Module;
import com.artsemrogovenko.diplom.accountapp.dto.TaskStatus ;


import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskForUser implements TaskData{
    private Long id;
    private String name;   //имя
    private String description; // описание
    private TaskStatus taskStatus;
    private String contractNumber; // номер договора
    private String owner; // у кого сейчас задача
    private boolean reserved;

    private List<Module> modules = new ArrayList<>();


}
