package com.artsemrogovenko.diplom.accountapp.dto.mymapper;

import com.artsemrogovenko.diplom.accountapp.dto.TaskData;
import com.artsemrogovenko.diplom.accountapp.models.Module;
import com.artsemrogovenko.diplom.accountapp.models.Task;

public class TaskMapper {
    public static <T extends TaskData> Task mapToTask(T taskData) {
        Task task = new Task(taskData.getId());

//        task.setId(taskData.getId());
        task.setName(taskData.getName());
        task.setDescription(taskData.getDescription());
        task.setStatus(taskData.getStatus());
        task.setContractNumber(taskData.getContractNumber());
        task.setOwner(taskData.getOwner());
        task.setReserved(taskData.isReserved());

        if (taskData.getModules() != null && !taskData.getModules().isEmpty())
            for (Module module : taskData.getModules()) {
                task.getModules().add(ModuleMapper.mapToModule(module));
            }

        return task;

    }
}
