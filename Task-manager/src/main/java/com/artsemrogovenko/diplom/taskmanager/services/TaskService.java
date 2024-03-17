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
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Task getTaskById(Long id) {
//        return taskRepository.findById(id).orElseThrow(
//                () -> new ResourceNotFoundException("задача " + id + " не найдена!"));
        return taskRepository.findById(id).get();
    }


    /**
     * Резервирование задачи на для пользователя.
     * при отстутствии компонентов на складе задача будет отклонена
     *
     * @return
     */
    @TrackUserAction
    @Transactional
    public ResponseEntity<String> reservedTask(Long taskId, String userId) throws ExcessAmountException,ResourceNotFoundException {
        Task work = getTaskById(taskId);
        if (work==null){
            return new ResponseEntity<>("Нет такой задачи", HttpStatus.NOT_FOUND);
        }
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
        ResponseEntity<List<ComponentResponse>> result = new ResponseEntity<>(HttpStatus.OK);
        try {
            result = storageApi.reserve(calculatedComponents, userId, work.getContractNumber(), taskId);

        } catch (ResourceNotFoundException ex) {
            rollbackReservedTask(taskId, userId);
            return new ResponseEntity<>("Недостаточно материалов для выполнения", HttpStatus.NOT_FOUND);
        }
//        ResponseEntity<List<ComponentResponse>> result = storageApi.reserve(calculatedComponents, userId, work.getContractNumber(), taskId);
//
//        if (result.getStatusCode().isSameCodeAs(HttpStatus.I_AM_A_TEAPOT)) {
//            // Отмена задачи
//            rollbackReservedTask(taskId, userId);
//        }
        if (result.getStatusCode().isSameCodeAs(HttpStatus.OK)) {
            TaskForUser forUser = TaskMapper.convertTask(work);
//            ResponseEntity<String> responseEntity = MyRequest.assignTask(forUser, userId);
            //назначаю задачу
            try {
                accountApi.assignTask(forUser, userId);

            } catch (FeignException.FeignClientException ex) {
                rollbackReservedTask(taskId, userId);//отмена резерва задачи
                MyRequest.rollbackComponents(calculatedComponents); // возвращаю компоненты на склад
            }

//            accountApi.assignTask(forUser, userId);
//            if (!result.getStatusCode().isSameCodeAs(HttpStatus.OK)) {//если на сервисе клиента ошибка
//                rollbackReservedTask(taskId, userId);//отмена резерва задачи
//                MyRequest.rollbackComponents(calculatedComponents); // возвращаю компоненты на склад
//            } else {
//
//            }
        }
        System.out.println("task is reserved");
        return new ResponseEntity<>("Задача принята", HttpStatus.OK);
    }

    /**
     * Откат резервирования
     */
    @TrackUserAction
    @Transactional
    public ResponseEntity<String> rollbackReservedTask(Long id, String ownerId) {
        Task work = getTaskById(id);
        if (work==null){
            return new ResponseEntity<>("Нет такой задачи", HttpStatus.NOT_FOUND);
        }
        if (work.getOwner().equals(ownerId)) {
            work.setReserved(false);
            work.setOwner("kanban"); // сделать владельцем общее хранилище
            work.setStatus(Task.Status.TO_DO);
            taskRepository.save(work);
            return new ResponseEntity<>("Вы отказались от задачи", HttpStatus.OK);
        }
        return new ResponseEntity<>("Такой задачи нет, или вы не владелец", HttpStatus.BAD_REQUEST);
    }

    @TrackUserAction
    @Transactional
    public ResponseEntity<String> completedTask(Long id, String ownerId) {
        Task work = getTaskById(id);
        if (work==null){
            return new ResponseEntity<>("Нет такой задачи", HttpStatus.NOT_FOUND);
        }
        if (work.getOwner().equals(ownerId)) {
            work.setStatus(Task.Status.DONE);
//            System.out.println(work);
            taskRepository.save(work);
            System.out.println("Блок complete применился");
            return new ResponseEntity<>("Поздравляю", HttpStatus.OK);
        }
        System.out.println("Блок complete не применился");
        return new ResponseEntity<>("Такой задачи нет, или вы не владелец", HttpStatus.BAD_REQUEST);
    }


}
