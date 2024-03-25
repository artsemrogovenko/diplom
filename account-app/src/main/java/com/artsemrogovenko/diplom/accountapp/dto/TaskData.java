package com.artsemrogovenko.diplom.accountapp.dto;

import com.artsemrogovenko.diplom.accountapp.models.Module;
import com.artsemrogovenko.diplom.accountapp.dto.TaskStatus ;

import java.util.List;

public interface TaskData {
    Long getId();

    String getName();

    String getDescription();

    TaskStatus getStatus();

    String getContractNumber();

    String getOwner();

    boolean isReserved();

    List<Module> getModules();
}
