package com.artsemrogovenko.diplom.accountapp.repositories;

import com.artsemrogovenko.diplom.accountapp.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByOwner(String name);

  Task findByIdAndOwner(Long taskId, String userId);

}
