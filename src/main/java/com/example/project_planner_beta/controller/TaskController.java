package com.example.project_planner_beta.controller;

import com.example.project_planner_beta.dto.CreateTaskRequest;
import com.example.project_planner_beta.dto.TaskDTO;
import com.example.project_planner_beta.dto.UpdateTaskRequest;
import com.example.project_planner_beta.exception.BadRequestException;
import com.example.project_planner_beta.mapper.TaskMapper;
import com.example.project_planner_beta.model.Task;
import com.example.project_planner_beta.repository.ProjectRepository;
import com.example.project_planner_beta.repository.TaskRepository;
import com.example.project_planner_beta.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/code/{projectCode}")
    public List<TaskDTO> getTasksByCode(@PathVariable String projectCode){
        List<Task> tasks = taskService.getTasksByProjectCode(projectCode);
        return TaskMapper.toDTOList(tasks);
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody CreateTaskRequest request) {
        Task task = new Task();
        task.setName(request.getName());
        task.setProjectCode(request.getProjectCode());
        task.setStartDate(request.getStartDate());
        task.setEndDate(request.getEndDate());

        Set<Long> dependencyIds = request.getDependencyIds() !=null
                ? new HashSet<>(request.getDependencyIds())
                :Collections.emptySet();
        if(!dependencyIds.isEmpty()){
            List<Task> dependencies = taskRepository.findAllById(dependencyIds);
            task.setDependencies(new HashSet<>(dependencies));
        }


        Task createdTask = taskService.createTask(task);
        System.out.println("Task saved. Generated ID:" + createdTask.getId());

        return ResponseEntity.ok(TaskMapper.toDTO(createdTask));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id,@RequestBody UpdateTaskRequest updatedTask){
        Task oldRecord = taskRepository.findById(id).orElseThrow(() -> new BadRequestException("Cannot find ID"));

        Task savedtask = new Task();

        savedtask.setName(updatedTask.getName());
        savedtask.setProjectCode(oldRecord.getProjectCode());
        savedtask.setStartDate(updatedTask.getStartDate());
        savedtask.setEndDate(updatedTask.getEndDate());
        savedtask.setStatus(updatedTask.getStatus());

        Set<Long> dependencyIds = updatedTask.getDependencyIds() !=null
                ? new HashSet<>(updatedTask.getDependencyIds())
                :Collections.emptySet();
        if(!dependencyIds.isEmpty()){
            List<Task> dependencies = taskRepository.findAllById(dependencyIds);
            savedtask.setDependencies(new HashSet<>(dependencies));
        }

        Task saved = taskService.updateTask(id, savedtask);

        return ResponseEntity.ok(TaskMapper.toDTO(saved));
    }

    @GetMapping("/{id}")
    public TaskDTO getTaskById(@PathVariable Long id){
        Task task = taskService.getTaskDetails(id)
                .orElseThrow(() -> new BadRequestException("Cannot find task"));

        return TaskMapper.toDTO(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id){
        Optional<Task> task = taskService.getTaskDetails(id);
        if (task.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }


    // debugging payloads
    @PostMapping("/debug")
    public String debugRequest(@RequestBody String rawBody){
        System.out.println("Payload" + rawBody);
        return rawBody;
    }
}
