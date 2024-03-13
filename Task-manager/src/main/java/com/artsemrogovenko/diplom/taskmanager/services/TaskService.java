package com.artsemrogovenko.diplom.taskmanager.services;


import com.artsemrogovenko.diplom.taskmanager.aop.TrackUserAction;
import com.artsemrogovenko.diplom.taskmanager.dto.ComponentResponse;
import com.artsemrogovenko.diplom.taskmanager.dto.mymapper.CollectComponets;
import com.artsemrogovenko.diplom.taskmanager.model.Component;
import com.artsemrogovenko.diplom.taskmanager.model.Task;
import com.artsemrogovenko.diplom.taskmanager.model.exceptions.ExcessAmountException;
import com.artsemrogovenko.diplom.taskmanager.model.exceptions.ResourceNotFoundException;
import com.artsemrogovenko.diplom.taskmanager.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import com.artsemrogovenko.diplom.taskmanager.httprequest.MyRequest;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

import static com.artsemrogovenko.diplom.taskmanager.httprequest.MyRequest.convertResponseList;

/**
 * Сервис для работы с задачими.
 */
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class TaskService {
    /**
     * Объект репозитория.
     */
    private final TaskRepository taskRepository;
    private final WebClient.Builder builder;

    /**
     * Получение всех задач.
     *
     * @return список задач.
     */
    @TrackUserAction
    public List<Task> getAllTask() {
        return taskRepository.findAll();
    }

    /**
     * Получение данных о конкретной задаче на доске.
     *
     * @param id идентификатор задачи.
     * @return объект задачи.
     * @throws ResourceNotFoundException исключение при отсутствии задачи.
     */
    @TrackUserAction
    public Task getTaskById(Long id) throws ResourceNotFoundException {
        return taskRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("задача " + id + " не найдена!"));
    }


    /**
     * Резервирование задачи на доске.
     */
    @TrackUserAction
    @Transactional
    public void reservedTask(Long id, String ownerId) throws ExcessAmountException {
        Task work = getTaskById(id);
        if (work.isReserved()) {
            throw new ExcessAmountException("задача уже выполняется");
        }
        work.setReserved(true);
        work.setOwner(ownerId);
        work.setStatus(Task.Status.IN_PROGRESS);
        List<Component> components = CollectComponets.calculate(work);
        components.forEach(System.out::println);
//        ResponseEntity<List<ComponentResponse>> result = postRequest("http://storage-server:8081/inventory",,ownerId,work.getContractNumber());
        taskRepository.save(work);
        System.out.println("reservedTask");
    }

    /**
     * Откат резервирования
     */
    @TrackUserAction
    @Transactional
    public void rollbackReservedTask(Long id) {
        Task work = getTaskById(id);
        work.setReserved(false);
        work.setOwner("Kanban"); // сделать владельцем общее хранилище
        work.setStatus(Task.Status.TO_DO);
        taskRepository.save(work);
    }

    @TrackUserAction
    public void completedTask(Long id, String ownerId) {
        Task work = getTaskById(id);
        if (work.getOwner().equals(ownerId)) {
            work.setStatus(Task.Status.DONE);
            taskRepository.save(work);
            System.out.println("Блок complete применился");
        } else
            System.out.println("Блок complete не применился");
    }


}
