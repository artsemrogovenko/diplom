package com.artsemrogovenko.diplom.taskmanager.repository;

import com.artsemrogovenko.diplom.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task,Long> {

}
