package com.artsemrogovenko.diplom.taskmanager.dto.mymapper;

import com.artsemrogovenko.diplom.taskmanager.dto.ModuleResponse;
import com.artsemrogovenko.diplom.taskmanager.model.Module;
import com.artsemrogovenko.diplom.taskmanager.model.*;

import java.util.ArrayList;
import java.util.List;



public class TemplateMapper {
    public static <T extends TemplateData> Template mapToTemplate(T data) {
        Template template = new Template();
        template.setName(data.getName().trim());
        template.setDescription(data.getDescription().trim());

        if (data instanceof TemplateRequest) {
            TemplateRequest templateRequest = (TemplateRequest) data;
            List<ModuleResponse> requestList = new ArrayList<>();
            if (templateRequest.getModules() != null && !templateRequest.getModules().isEmpty()) {
                requestList = templateRequest.getModules();
                template.setModules(requestList.stream().map(ModuleMapper::mapToModule).toList());
            }
        }
        return template;
    }

    public static <T extends TemplateData> Task mapToTask(T data) {
        Task task = new Task();
        task.setName(data.getName().trim());
        task.setDescription(data.getDescription().trim());

//        task.setStatus(TO_DO);

        if (data instanceof TemplateRequest) {
            TemplateRequest templateRequest = (TemplateRequest) data;
            List<ModuleResponse> requestList = new ArrayList<>();
            if (templateRequest.getModules() != null && !templateRequest.getModules().isEmpty()) {
                requestList = templateRequest.getModules();
                task.setModules(requestList.stream().map(ModuleMapper::mapToModule).toList());
            }
        }

        if (data instanceof Template) {
            Template template = (Template) data;
            if (template.getModules() != null && !template.getModules().isEmpty()) {
                task.setModules(template.getModules());
            }
        }
        return task;
    }


}
