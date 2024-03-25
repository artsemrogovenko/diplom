package com.artsemrogovenko.diplom.taskmanager.repository;

import com.artsemrogovenko.diplom.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {

}
