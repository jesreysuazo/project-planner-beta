package com.example.project_planner_beta.projectplanner;

import com.example.project_planner_beta.common.BadRequestException;
import com.example.project_planner_beta.projectplanner.dto.CreateTaskRequestDTO;
import com.example.project_planner_beta.projectplanner.dto.TaskDTO;
import com.example.project_planner_beta.projectplanner.dto.UpdateTaskRequestDTO;
import com.example.project_planner_beta.projectplanner.tools.TaskMapper;
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

    /**
     * retrieves all tasks under a project by its project code
     *
     * @param projectCode unique code of a project
     * @return list of task under a project
     */
    @GetMapping("/code/{projectCode}")
    public List<TaskDTO> getTasksByCode(@PathVariable String projectCode){
        List<Task> tasks = taskService.getTasksByProjectCode(projectCode);
        return TaskMapper.toDTOList(tasks);
    }

    /**
     * creates a task
     *
     * @param request {
     *      name: name of task
     *      projectCode: unique code of a project where the task will be under
     *      startDate: YYYY-MM-DD
     *      endDate: YYYY-MM-DD
     *      dependencyIds: [ id of a task to be added as dependency ]
     *   }
     * @return created task
     */
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody CreateTaskRequestDTO request) {
        Task task = new Task();
        task.setName(request.getName());
        task.setProjectCode(request.getProjectCode());
        task.setStartDate(request.getStartDate());
        task.setEndDate(request.getEndDate());

        Set<Long> dependencyIds = request.getDependencyIds() !=null
                ? new HashSet<>(request.getDependencyIds())
                :Collections.emptySet();
        if(!dependencyIds.isEmpty()){
            // checks if ids are valid on adding dependency
            taskService.dependencyIdChecker(dependencyIds);
            List<Task> dependencies = taskRepository.findAllById(dependencyIds);
            task.setDependencies(new HashSet<>(dependencies));
        }


        Task createdTask = taskService.createTask(task);
        System.out.println("Task saved. Generated ID:" + createdTask.getId());

        return ResponseEntity.ok(TaskMapper.toDTO(createdTask));
    }

    /**
     * updates an existing task based on its ID
     *
     * @param id id of the task to be updated
     * @param updatedTask {
     *            name: name of task
     *            projectCode: unique code of a project where the task will be under
     *            startDate: YYYY-MM-DD
     *            endDate: YYYY-MM-DD
     *            dependencyIds: [ id of a task to be added as dependency ]
     *         }
     * @return the updated task
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id,@RequestBody UpdateTaskRequestDTO updatedTask){
        Task oldRecord = taskService.getTaskDetails(id).orElseThrow(() -> new BadRequestException("Update failed. Invalid ID provided"));

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
            // checks if ids are valid on adding dependency
            taskService.dependencyIdChecker(dependencyIds);
            List<Task> dependencies = taskRepository.findAllById(dependencyIds);
            savedtask.setDependencies(new HashSet<>(dependencies));
        }

        Task saved = taskService.updateTask(id, savedtask);

        return ResponseEntity.ok(TaskMapper.toDTO(saved));
    }

    /**
     * retrieves the details of the task by its ID
     *
     * @param id id of the task
     * @return details of the task
     */
    @GetMapping("/{id}")
    public TaskDTO getTaskById(@PathVariable Long id){
        Task task = taskService.getTaskDetails(id)
                .orElseThrow(() -> new BadRequestException("Cannot find task"));

        return TaskMapper.toDTO(task);
    }

    /**
     * deletes a task
     *
     * @param id id of task
     * @return a confirmation response for deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id){
        Optional<Task> task = taskService.getTaskDetails(id);
        if (task.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * endpoint for checking if correct payload is passed upon request
     *
     * @param rawBody any payload in JSON format
     * @return the rawBody ideally
     */
    @PostMapping("/debug")
    public String debugRequest(@RequestBody String rawBody){
        System.out.println("Payload" + rawBody);
        return rawBody;
    }
}
