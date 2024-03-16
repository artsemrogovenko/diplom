package com.artsemrogovenko.diplom.taskmanager.services;


import com.artsemrogovenko.diplom.taskmanager.aop.TrackUserAction;
import com.artsemrogovenko.diplom.taskmanager.api.AccountApi;
import com.artsemrogovenko.diplom.taskmanager.api.StorageApi;
import com.artsemrogovenko.diplom.taskmanager.calculate.Formula;
import com.artsemrogovenko.diplom.taskmanager.dto.ComponentRequest;
import com.artsemrogovenko.diplom.taskmanager.dto.ComponentResponse;
import com.artsemrogovenko.diplom.taskmanager.dto.TaskForUser;
import com.artsemrogovenko.diplom.taskmanager.dto.mymapper.TaskMapper;
import com.artsemrogovenko.diplom.taskmanager.httprequest.MyRequest;
import com.artsemrogovenko.diplom.taskmanager.model.Component;
import com.artsemrogovenko.diplom.taskmanager.model.Task;
import com.artsemrogovenko.diplom.taskmanager.model.exceptions.ExcessAmountException;
import com.artsemrogovenko.diplom.taskmanager.model.exceptions.ResourceNotFoundException;
import com.artsemrogovenko.diplom.taskmanager.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**
 * Сервис для работы с задачими.
 */
@Service
@AllArgsConstructor
public class TaskService {
    /**
     * Объект репозитория.
     */
    private final TaskRepository taskRepository;
    /**
     * OpenFeign интерфейсы
     */
    private final StorageApi storageApi;
    private final AccountApi accountApi;

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
     * Резервирование задачи на для пользователя.
     * при отстутствии компонентов на складе задача будет отклонена
     */
    @TrackUserAction
    @Transactional
    public void reservedTask(Long taskId, String userId) throws ExcessAmountException {
        Task work = getTaskById(taskId);
        if (work.isReserved()) {
            throw new ExcessAmountException("задача уже выполняется");
        }
        work.setReserved(true);
        work.setOwner(userId);
        work.setStatus(Task.Status.IN_PROGRESS);
        taskRepository.save(work);
        //в задаче может быть несколько модулей, получение списка компонентов вынесен в другой блок
        List<Component> components = Formula.componentsFromAllModules(work);
        components.forEach(System.out::println);
        System.out.println("а теперь подсчитаное");
        // компоненты могут повторяться но их количество будет разное
        // поэтому нужно создать список без повторов и с подсчитаным количеством,
        // если просумировать на обьектах списках то это отразаться на базе данных
        // а мне это не нужно
        List<ComponentRequest> calculatedComponents = Formula.totalizationComponents(components);
        calculatedComponents.forEach(System.out::println);
//
//        ResponseEntity<List<ComponentResponse>> result = MyRequest.postRequest(
//                "http://storage-server:8081/component/inventory", calculatedComponents, userId, work.getContractNumber());
        // проверка на наличие компонентов

        ResponseEntity<List<ComponentResponse>> result = storageApi.reserve(calculatedComponents, userId, work.getContractNumber(), taskId);

        if (result.getStatusCode().isSameCodeAs(HttpStatus.I_AM_A_TEAPOT)) {
            // Отмена задачи
            rollbackReservedTask(taskId, userId);
        }
        if (result.getStatusCode().isSameCodeAs(HttpStatus.OK)) {
            TaskForUser forUser = TaskMapper.convertTask(work);
//            ResponseEntity<String> responseEntity = MyRequest.assignTask(forUser, userId);
            //назначаю задачу
            accountApi.assignTask(forUser, userId);

            if (!result.getStatusCode().isSameCodeAs(HttpStatus.OK)) {//если на сервисе клиента ошибка
                rollbackReservedTask(taskId, userId);//отмена резерва задачи
                MyRequest.rollbackComponents(calculatedComponents); // возвращаю компоненты на склад
            } else {

                System.out.println("task is reserved");
            }
        }
    }

    /**
     * Откат резервирования
     */
    @TrackUserAction
    @Transactional
    public void rollbackReservedTask(Long id, String ownerId) {
        Task work = getTaskById(id);
        if (work.getOwner().equals(ownerId)) {
            work.setReserved(false);
            work.setOwner("kanban"); // сделать владельцем общее хранилище
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


}
