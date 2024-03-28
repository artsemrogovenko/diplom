package com.artsemrogovenko.diplom.taskmanager.services;


import com.artsemrogovenko.diplom.taskmanager.aop.TrackUserAction;
import com.artsemrogovenko.diplom.taskmanager.api.AccountApi;
import com.artsemrogovenko.diplom.taskmanager.api.StorageApi;
import com.artsemrogovenko.diplom.taskmanager.calculate.Formula;
import com.artsemrogovenko.diplom.taskmanager.dto.ComponentRequest;
import com.artsemrogovenko.diplom.taskmanager.dto.ComponentResponse;
import com.artsemrogovenko.diplom.taskmanager.dto.TaskForUser;
import com.artsemrogovenko.diplom.taskmanager.dto.mymapper.TaskMapper;
import com.artsemrogovenko.diplom.taskmanager.model.Component;
import com.artsemrogovenko.diplom.taskmanager.model.Task;
import com.artsemrogovenko.diplom.taskmanager.model.TaskStatus;
import com.artsemrogovenko.diplom.taskmanager.model.exceptions.ResourceNotFoundException;
import com.artsemrogovenko.diplom.taskmanager.repository.TaskRepository;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;


/**
 * Сервис для работы с задачими.
 */
@Service
@AllArgsConstructor
public class TaskService {
    private final ProductService productService;

    /**
     * Объект репозитория.
     */
    private final TaskRepository taskRepository;
    /**
     * OpenFeign интерфейсы
     */
    private final StorageApi storageApi;
    private final AccountApi accountApi;

    public void deltask(Long id) {
        taskRepository.deleteById(id);
    }

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
    public Task getTaskById(Long id) throws NoSuchElementException {
        return taskRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("задача " + id + " не найдена!"));
//        return taskRepository.findById(id).get();
    }


    /**
     * Резервирование задачи на для пользователя.
     * при отсутствии компонентов на складе задача будет отклонена
     *
     * @return
     */
    @TrackUserAction
    @Transactional
    public ResponseEntity<String> reservedTask(Long taskId, String userId) throws FeignException.InternalServerError {
        Task work = new Task();
        try {
            work = getTaskById(taskId);
            System.out.println(work);
        } catch (NoSuchElementException ex) {
            return new ResponseEntity<>("Нет такой задачи", HttpStatus.NOT_FOUND);
        }
        if (work.isReserved()) {
//            throw new ExcessAmountException("задача уже выполняется");
            return new ResponseEntity<>("Задача уже выполняется", HttpStatus.NOT_FOUND);
        }

        work.setReserved(true);
        work.setOwner(userId);
        work.setStatus(TaskStatus.IN_PROGRESS);
        taskRepository.save(work);
        //в задаче может быть несколько модулей, получение списка компонентов вынесен в другой блок
        List<Component> components = Formula.componentsFromAllModules(work);
        components.forEach(System.out::println);
        // компоненты могут повторяться но их количество будет разное
        // поэтому нужно создать список без повторов и с подсчитаным количеством,
        // если просумировать на обьектах списках то это отразаться на базе данных
        // а мне это не нужно
        List<ComponentRequest> calculatedComponents = Formula.totalizationComponents(components);
        System.out.println("а теперь подсчитаное");
        calculatedComponents.forEach(System.out::println);
        // проверка на наличие компонентов
        ResponseEntity<List<ComponentResponse>> result = new ResponseEntity<>(HttpStatus.OK);
        try {
            result = storageApi.reserve(calculatedComponents, userId, work.getContractNumber(), taskId);

        } catch (FeignException.FeignClientException ex) {
            if (ex.getMessage().contains("[418] during [POST]")) {
//                rollbackReservedTask(taskId, userId);

                work.setReserved(false);
                work.setOwner("kanban"); // сделать владельцем общее хранилище
                work.setStatus(TaskStatus.TO_DO);
                taskRepository.save(work);
                return new ResponseEntity<>("Недостаточно материалов для выполнения", HttpStatus.NOT_FOUND);

            }
        }
        if (result.getStatusCode().isSameCodeAs(HttpStatus.OK)) {
            TaskForUser forUser = TaskMapper.convertTask(work);
            //назначаю задачу
            try {
                accountApi.assignTask(forUser, userId);

            } catch (FeignException.FeignClientException ex) {
//                rollbackReservedTask(taskId, userId);//отмена резерва задачи
                work.setReserved(false);
                work.setOwner("kanban"); // сделать владельцем общее хранилище
                work.setStatus(TaskStatus.TO_DO);
                taskRepository.save(work);
                storageApi.saveComponents(calculatedComponents);// возвращаю компоненты на склад
                return new ResponseEntity<>(ex.contentUTF8(), HttpStatus.NOT_FOUND);
            }
        }
        System.out.println("task is reserved");
        return new ResponseEntity<>("Задача принята", HttpStatus.OK);
    }

    /**
     * Откат резервирования
     */
    @TrackUserAction
    @Transactional
    public ResponseEntity<String> rollbackReservedTask(Long taskId, String ownerId) {
        Task work = new Task();
        try {
            work = getTaskById(taskId);
            System.out.println(work);
        } catch (NoSuchElementException ex) {
            return new ResponseEntity<>("Нет такой задачи", HttpStatus.NOT_FOUND);
        }
        if (work.getOwner().equals(ownerId)) {
            work.setReserved(false);
            work.setOwner("kanban"); // сделать владельцем общее хранилище
            work.setStatus(TaskStatus.TO_DO);
            taskRepository.save(work);
            return new ResponseEntity<>("Вы отказались от задачи", HttpStatus.OK);
        }
        return new ResponseEntity<>("Такой задачи нет, или вы не владелец", HttpStatus.BAD_REQUEST);
    }

    @TrackUserAction
    @Transactional
    public ResponseEntity<String> completedTask(Long taskId, String ownerId) {
        Task work = new Task();
        try {
            work = getTaskById(taskId);
        } catch (NoSuchElementException ex) {
            return new ResponseEntity<>("Нет такой задачи", HttpStatus.NOT_FOUND);
        }
        if (work.getOwner().equals(ownerId)) {
            work.setStatus(TaskStatus.DONE);
//            System.out.println(work);
            taskRepository.save(work);
            System.out.println("Блок complete применился");
            productService.changeProductStatus(work.getContractNumber());
            return new ResponseEntity<>("Поздравляю", HttpStatus.OK);
        }
        System.out.println("Блок complete не применился");
        return new ResponseEntity<>("Такой задачи нет, или вы не владелец", HttpStatus.BAD_REQUEST);
    }


}
