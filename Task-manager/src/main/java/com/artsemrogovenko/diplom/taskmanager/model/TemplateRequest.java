package com.artsemrogovenko.diplom.taskmanager.model;

import com.artsemrogovenko.diplom.taskmanager.dto.ModuleResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TemplateRequest implements TemplateData{
    private String name;   //имя
    private String description; // описание

   private MyCollection<ModuleResponse> modules=new MyCollection();

    public void addModule(ModuleResponse moduleResponse){
       modules.add(moduleResponse);
   }

}
