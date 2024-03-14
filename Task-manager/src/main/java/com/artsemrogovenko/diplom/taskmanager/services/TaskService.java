package com.artsemrogovenko.diplom.taskmanager.services;


import com.artsemrogovenko.diplom.taskmanager.aop.TrackUserAction;
import com.artsemrogovenko.diplom.taskmanager.dto.ComponentRequest;
import com.artsemrogovenko.diplom.taskmanager.dto.mymapper.CollectComponets;
import com.artsemrogovenko.diplom.taskmanager.dto.mymapper.ComponentMapper;
import com.artsemrogovenko.diplom.taskmanager.model.Component;
import com.artsemrogovenko.diplom.taskmanager.model.Task;
import com.artsemrogovenko.diplom.taskmanager.model.exceptions.ExcessAmountException;
import com.artsemrogovenko.diplom.taskmanager.model.exceptions.ResourceNotFoundException;
import com.artsemrogovenko.diplom.taskmanager.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        //в задаче может быть несколько модулей, и в самих модулях компоненты
        //то создание списка компонентов вынесен в другой блок
        List<Component> components = CollectComponets.calculate(work);
        components.forEach(System.out::println);
        System.out.println("а теперь подсчитаное");
        // компоненты могут повторяться но их количество будет разное
        // поэтому нужно создать список без повторов и с подсчитаным количеством,
        // если просумировать на обьектах списках то это отразаться на базе данных
        // а мне это не нужно
        List<ComponentRequest> calculatedComponents = totalizationComponents(components);

        calculatedComponents.forEach(System.out::println);
//        ResponseEntity<List<ComponentResponse>> result = postRequest("http://storage-server:8081/inventory",ownerId,work.getContractNumber());

        taskRepository.save(work);
        System.out.println("task is reserved");
    }

    /**
     * Откат резервирования
     */
    @TrackUserAction
    @Transactional
    public void rollbackReservedTask(Long id,String ownerId) {
        Task work = getTaskById(id);
        if (work.getOwner().equals(ownerId)) {
            work.setReserved(false);
            work.setOwner("Kanban"); // сделать владельцем общее хранилище
            work.setStatus(Task.Status.TO_DO);
            taskRepository.save(work);
        }
    }

    @TrackUserAction
    @Transactional
    public void completedTask(Long id, String ownerId) {
        Task work = getTaskById(id);
        if (work.getOwner().equals(ownerId)) {
            work.setStatus(Task.Status.DONE);
//            System.out.println(work);
            taskRepository.save(work);
            System.out.println("Блок complete применился");
        } else
            System.out.println("Блок complete не применился");
    }

    private List<ComponentRequest> totalizationComponents(List<Component> components) {

        Map<Long, ComponentRequest> uniqueRequest = new HashMap<>();
        for (Component component : components) {
            if (uniqueRequest.containsKey(component.getId())) {
                // Если уже есть такой компонент, обновляем значение quantity
                ComponentRequest tempComponent = uniqueRequest.get(component.getId());
                tempComponent.setQuantity(tempComponent.getQuantity() + component.getQuantity());

            } else {
                // Если такого компонента ещё нет, добавляем его в Map
                ComponentRequest tempComponent = ComponentMapper.mapToComponentRequest(component);
                uniqueRequest.put(component.getId(), tempComponent);
            }
        }
        return new ArrayList<>(uniqueRequest.values());
    }
}
