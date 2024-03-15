package com.artsemrogovenko.diplom.accountapp.repositories;

import com.artsemrogovenko.diplom.accountapp.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task,Long> {

}
