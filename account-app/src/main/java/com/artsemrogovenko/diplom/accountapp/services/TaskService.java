package com.artsemrogovenko.diplom.accountapp.services;


import com.artsemrogovenko.diplom.accountapp.api.TaskApi;
import com.artsemrogovenko.diplom.accountapp.aspect.LogMethod;
import com.artsemrogovenko.diplom.accountapp.dto.TaskForUser;
import com.artsemrogovenko.diplom.accountapp.dto.mymapper.TaskMapper;
import com.artsemrogovenko.diplom.accountapp.dto.TaskStatus;
import com.artsemrogovenko.diplom.accountapp.httprequest.MyRequest;
import com.artsemrogovenko.diplom.accountapp.models.Module;
import com.artsemrogovenko.diplom.accountapp.models.*;
import com.artsemrogovenko.diplom.accountapp.models.exceptions.DuplicateExeption;
import com.artsemrogovenko.diplom.accountapp.repositories.AccountRepository;
import com.artsemrogovenko.diplom.accountapp.repositories.TaskRepository;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Сервис для работы с задачими.
 */
@Service
@AllArgsConstructor
public class TaskService {
    private final TaskApi taskApi;
    private final AccountRepository accountRepository;
    private final ModuleService moduleService;
    private final TaskRepository taskRepository;
    private static LocalTime requiredTime = LocalTime.now().plusSeconds(30);
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
    public List<Task> getTasks(LocalTime currentTime) throws FeignException.ServiceUnavailable, feign.RetryableException {
        secondsDifference = (int) LocalTime.now().until(requiredTime, ChronoUnit.SECONDS);
        if (currentTime.isAfter(requiredTime)) {
            requiredTime = LocalTime.now().plusSeconds(30);
            tasks = pullTasks();
        }
        return tasks;
    }

    @LogMethod
    public List<Task> pullTasks() throws FeignException.ServiceUnavailable, feign.RetryableException {
        ResponseEntity<List<Task>> taskList = taskApi.getTasks();
        if (taskList.hasBody()) {
            return taskList.getBody();
        }
        secondsDifference = (int) LocalTime.now().until(requiredTime, ChronoUnit.SECONDS);
        return new ArrayList<>();
    }

    @LogMethod
    public ResponseEntity<?> rollbackTask(String user, Long taskid) {
        Task rollbackTask = taskRepository.findById(taskid).get();
        List<Component> components = MyRequest.componentsFromAllModules(rollbackTask);
        List<ComponentRequest> calculated = MyRequest.totalizationComponents(components);
        return MyRequest.rollbackComponents(calculated);
    }

    @LogMethod
    public String completeTask(String user, Long taskid) {
        Task complete = taskRepository.findById(taskid).get();
        if (complete.getOwner().equals(user)) {
            complete.setStatus(TaskStatus.DONE);
            return taskApi.completeTask(taskid, user).getBody();
        }
        return "Что-то пошло не так";
    }

    private Task saveTask(Task task) {
        Task result = TaskMapper.mapToTask(task);
        result.setModules(new ArrayList<>());

        List<Module> modules = new ArrayList<>();
        if (!modules.isEmpty() || modules != null) {
            if (!task.getModules().isEmpty() && task.getModules() != null) {
                for (Module module : task.getModules()) {
                    if (module != null) {
                        modules.add(moduleService.createModule(module).getBody());
                    }
                }
            }
        }
        result.setModules(modules);
        return taskRepository.save(result);
    }

    @LogMethod
    public ResponseEntity<String> assign(String userId, TaskForUser assignTask) {
        try {
            Task newTask = TaskMapper.mapToTask(assignTask);
            Account recieverAccount = accountRepository.findByName(userId);
            if (recieverAccount == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("пользователь не найден");
            }
            // проверка на дубликат такой задачи
//            if (recieverAccount.getTasks().stream()
//                    .anyMatch(task -> task.getId().equals(assignTask.getId())
//                            && task.getContractNumber().equals(assignTask.getContractNumber()))) {
            if(false){
                System.out.println("блок проверки");
                throw new DuplicateExeption("такая задача в корзине есть");
            } else {
                recieverAccount.addTask(saveTask(newTask));
                accountRepository.save(recieverAccount);
                System.out.println(accountRepository.getReferenceById(userId));
                // Если ошибок нет, возвращаем успешный ответ
                System.out.println("сохранил изменения " + LocalTime.now());
                return ResponseEntity.status(HttpStatus.OK).body("Задача присвоена");
            }

        } catch (DuplicateExeption e) {
            System.out.println("перехват ошибки");
            // Возвращаем информацию об ошибке в теле ответа
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка: " + e.getMessage());
        }
    }

    @LogMethod
    public ResponseEntity<String> takeTask(Long taskId, String userid) {
        return taskApi.reserveAmount(taskId, userid);
    }

    @LogMethod
    public List<Task> showMyTasks(String userId) {
        Optional<List<Task>> result=  taskRepository.findAllByOwner(userId);
      if (result.isPresent()){
          return result.get();
      }
        return new ArrayList<>();
//        System.out.println(accountRepository.getReferenceById(userId));
//        return accountRepository.getReferenceById(userId).getTasks();
//        if (myAccount.isPresent()) {
//            return myAccount.get().getTasks();
//        }
//        return new ArrayList<>();
    }
}
