package com.artsemrogovenko.diplom.accountapp.services;


import com.artsemrogovenko.diplom.accountapp.api.TaskApi;
import com.artsemrogovenko.diplom.accountapp.dto.TaskForUser;
import com.artsemrogovenko.diplom.accountapp.dto.TaskMapper;
import com.artsemrogovenko.diplom.accountapp.httprequest.MyRequest;
import com.artsemrogovenko.diplom.accountapp.models.Component;
import com.artsemrogovenko.diplom.accountapp.models.ComponentRequest;
import com.artsemrogovenko.diplom.accountapp.models.Task;
import com.artsemrogovenko.diplom.accountapp.models.exceptions.DuplicateExeption;
import com.artsemrogovenko.diplom.accountapp.repositories.AccountRepository;
import com.artsemrogovenko.diplom.accountapp.repositories.TaskRepository;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.artsemrogovenko.diplom.accountapp.models.Account;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.ConnectException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * Сервис для работы с задачими.
 */
@Service
@AllArgsConstructor
public class TaskService {
    private final TaskApi taskApi;
    private final AccountRepository accountRepository;
    private final TaskRepository taskRepository;
    private static LocalTime requiredTime = LocalTime.now().plusMinutes(2);
    private static List<Task> tasks = new ArrayList<>();


    /**
     * сколько секунд нужно подождать для повторного обновления списка
     */
    private static int secondsDifference = 0;

    public static int getSecondsDifference() {
        return secondsDifference;
    }

    /**
     * обновлять список modules не чаще чем раз в две минуты
     *
     * @param currentTime время веб клиента
     */
    public List<Task> getTasks(LocalTime currentTime) throws FeignException.ServiceUnavailable, ConnectException {
        secondsDifference = (int) LocalTime.now().until(requiredTime, ChronoUnit.SECONDS);
        if (currentTime.isAfter(requiredTime)) {
            requiredTime = LocalTime.now().plusMinutes(2);
            tasks = pullTasks();
        }
        return tasks;
    }


    public List<Task> pullTasks() throws FeignException.ServiceUnavailable, ConnectException {
        ResponseEntity<List<Task>> taskList = taskApi.getTasks();
        if (taskList.hasBody()) {
            tasks = taskList.getBody();
        }
        secondsDifference = (int) LocalTime.now().until(requiredTime, ChronoUnit.SECONDS);
        return tasks;
    }


    public ResponseEntity<?> rollbackTask(String user, Long taskid) {
        Task rollbackTask = taskRepository.findById(taskid).get();
        List<Component> components = MyRequest.componentsFromAllModules(rollbackTask);
        List<ComponentRequest> calculated = MyRequest.totalizationComponents(components);
        return MyRequest.rollbackComponents(calculated);
    }


    public ResponseEntity<String> assign(String userId, TaskForUser assignTask) {
        try {
            Task newTask = TaskMapper.mapToTask(assignTask);
            Account recieverAccount = accountRepository.findByName(userId);
            // очень сложная проверка)
            if (recieverAccount.getTasks().stream()
                    .anyMatch(task -> task.getId().equals(newTask.getId())
                            && task.getContractNumber().equals(newTask.getContractNumber()))) {
                System.out.println("блок проверки");
                throw new DuplicateExeption("такая задача в корзине есть");
            } else {
                recieverAccount.getTasks().add(newTask);
                accountRepository.save(recieverAccount);
                // Если ошибок нет, возвращаем успешный ответ
                System.out.println("сохранил изменения " + LocalTime.now());
                return ResponseEntity.ok().build();
            }

        } catch (DuplicateExeption e) {
            System.out.println("перехват ошибки");
            // Возвращаем информацию об ошибке в теле ответа
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка: " + e.getMessage());
        }
    }

    public ResponseEntity<Void> takeTask(Long taskId, String userid) {
        return taskApi.reserveAmount(taskId, userid);
    }

    public List<Task> showMyTasks(String userId) {
        return accountRepository.getReferenceById(userId).getTasks();
    }
}
