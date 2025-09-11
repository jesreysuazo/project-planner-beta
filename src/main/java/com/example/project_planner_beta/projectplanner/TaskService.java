package com.example.project_planner_beta.projectplanner;

import com.example.project_planner_beta.common.BadRequestException;
import com.example.project_planner_beta.projectplanner.tools.TaskMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Logger;

@Service
public class TaskService {
    private static final Logger log = Logger.getLogger(TaskService.class.getName());
    @Autowired private TaskRepository taskRepository;
    @Autowired private ProjectRepository projectRepository;

    /**
     * Creates and saves a new task
     *
     * @param task The task to be created
     * @return The saved task with generated ID
     * @throws BadRequestException if the project does not exist, dates are invalid,
     *                              or dependencies belong to a different project or create circular dependency.
     */
    @Transactional
    public Task createTask(Task task) {

        log.info("Creating new task: name:" + task.getName() + ", projectCode: " + task.getProjectCode());

        Project project = projectRepository.findByCode(task.getProjectCode());
        if (project == null) {
            log.info("Failed to create task. Project with code "+ task.getProjectCode() +" does not exist");
            throw new BadRequestException("Project with code " + task.getProjectCode() + " does not exist");
        }

        validateDates(task.getStartDate(), task.getEndDate());

        task.setProjectCode(project.getCode());
        task.setDuration(durationCalculator(task.getStartDate(), task.getEndDate()));
        task.setProject(project);
        task.setStatus(TaskStatus.NOT_STARTED); // default status

        Task savedTask = taskRepository.save(task);

        if(savedTask.getDependencies() != null && !savedTask.getDependencies().isEmpty()){

            // check if start date of task is after the end date of task dependencies
            validateDependencyDates(task);

            log.info("Validating " + savedTask.getDependencies().size() + " task dependencies for task ID= " + savedTask.getId());

            Set<Task> dependencies = savedTask.getDependencies();
            for (Task dep: dependencies){
                if (!task.getProjectCode().equals(dep.getProjectCode())) {
                    log.info("Failed to add task dependencies. Project code does not match " + dep.getProjectCode() +" : " + task.getProjectCode() );
                    throw new BadRequestException("All dependencies must belong to the same project");
                }
                if (willLoop(task, dep)) {
                    log.info("This action will result in a circular dependency");
                    throw new BadRequestException("This action will result in a circular dependency");
                }
            }
        }

        log.info("Task saved with ID= " + savedTask.getId());
        return savedTask;
    }

    /**
     * Updates an existing task by its ID.
     *
     * @param taskId The id of the task to be updated
     * @param updatedTask New task data
     * @return The updated and saved task
     * @throws BadRequestException if the task does not exist
     *                              invalid dates
     *                              circular dependencies are detected
     */
    @Transactional
    public Task updateTask(Long taskId, Task updatedTask) {

        log.info("Updating task ID= " + taskId);
        Task existingRecord = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.info("Task with ID= "+ taskId +" not found");
                    return new BadRequestException("Task not found");
                });

        //check if same project code
        if(!Objects.equals(updatedTask.getProjectCode(), existingRecord.getProjectCode())){
            log.info("Cannot change project code");
            throw new BadRequestException("Cannot change project code");
        }

        // check if dependency is DONE
        if(updatedTask.getStatus() == TaskStatus.IN_PROGRESS || updatedTask.getStatus() == TaskStatus.DONE){
            if(updatedTask.getDependencies()!= null && !updatedTask.getDependencies().isEmpty()) {
                for(Task dep: existingRecord.getDependencies()){

                    if(dep.getStatus() == TaskStatus.NOT_STARTED || dep.getStatus() == TaskStatus.IN_PROGRESS){
                        log.info("Cannot start task until dependencies are DONE");
                        throw new BadRequestException("Cannot start task until dependencies are DONE");
                    }

                }
            }
        }

        validateDates(updatedTask.getStartDate(), updatedTask.getEndDate());

        existingRecord.setName(updatedTask.getName());
        existingRecord.setStartDate(updatedTask.getStartDate());
        existingRecord.setEndDate(updatedTask.getEndDate());
        existingRecord.setDuration(durationCalculator(updatedTask.getStartDate(), updatedTask.getEndDate()));
        existingRecord.setStatus(updatedTask.getStatus());

        if (updatedTask.getDependencies() != null && !updatedTask.getDependencies().isEmpty()) {

            //check if start date of task is after the end date of task dependencies
            validateDependencyDates(updatedTask);

            for (Task dep : updatedTask.getDependencies()) {

                if (!updatedTask.getProjectCode().equals(dep.getProjectCode())) {
                    log.info("All dependencies must belong to the same project");
                    throw new BadRequestException("All dependencies must belong to the same project");
                }
                if (willLoop(existingRecord, dep)) {
                    log.info("This action will result in a circular dependency");
                    throw new BadRequestException("This action will result in a circular dependency");
                }
            }

            existingRecord.setDependencies(updatedTask.getDependencies());
        } else {
            existingRecord.setDependencies(updatedTask.getDependencies());
        }

        Task savedTask = taskRepository.save(existingRecord);

        log.info("Task updated with ID= " + savedTask.getId());
        return savedTask;
    }

    /**
     * Gets the list of task under the project
     *
     * @param code code from project code
     * @return A list of all tasks under the project
     */
    public List<Task> getTasksByProjectCode(String code){
        List<Task> tasks = taskRepository.findByProjectCode(code);
        log.info("Retrieved " + tasks.size() + "tasks using project code: " + code);
        return tasks;
    }

    /**
     * Deletes a task by its ID
     *
     * @param id the ID of the task to be deleted
     */
    public void deleteTaskById(Long id){
        taskRepository.deleteById(id);
        log.info("Task with ID=" + id + " is deleted");
    }

    /**
     * Displays the task details by ID
     *
     * @param id the task id
     * @return An optional containing the task if found
     */
    public Optional<Task> getTaskDetails(Long id){
        return taskRepository.findById(id);
    }

    /**
     * Checker for circular dependency
     *
     * @param task The task being checked
     * @param dependency The dependency to be checked
     * @return true if circular dependency is detected, false if not
     */
    private boolean willLoop(Task task, Task dependency){
        Set<Long> visited = new HashSet<>();
        return isDependentOn(dependency, task.getId(), visited);
    }

    /**
     * Recursive checker for circular dependency
     * @param current dependency that is being checked
     * @param targetId ID of parent task where the dependency task being added
     * @param visited list of IDs that are checked
     * @return true if circular dependency is detected, false if none is detected
     */
    private boolean isDependentOn(Task current, Long targetId, Set<Long> visited){

        log.info("Checking task dependency ID=" + current.getId() + "for potential dependency looping. IDS visited: " + visited);
        if(current.getId().equals(targetId)){
            return true;
        }
        if(visited.contains(current.getId())){
            log.info("no circular dependency detected");
            return false;
        }
        visited.add(current.getId());


        for (Task dep : current.getDependencies()){
            if(isDependentOn(dep, targetId, visited)){
                log.info("circular dependency detected");
                return true;
            }
        }

        log.info("no circular dependency detected");
        return false;
    }

    /**
     * generates a schedule for the project using its ID
     *
     * @param projectId id of the project
     * @return a map containing array of task in order, duration of the project in days
     */
    @Transactional
    public Map<String, Object> generateSchedule(Long projectId){
        Project project = projectRepository.findById(projectId).orElseThrow(() -> {
            log.info("Cannot find project with ID: " + projectId);
            return new BadRequestException("Cannot find project with ID: " + projectId);
        });

        List<Task> tasks = project.getTasks();
        if(tasks == null || tasks.isEmpty()){
            log.info("Project with ID: " + projectId + " has no tasks. Cannot generate a schedule");
            throw new BadRequestException("Project with ID: " + projectId + " has no tasks. Cannot generate a schedule");
        }

        List<Task> sortedTasks = sortTask(tasks);

        //adjust dates of task depends on the sorting
        for(Task task: sortedTasks){
            if(task.getDependencies() != null && !task.getDependencies().isEmpty()){
                LocalDate latestEnd = task.getDependencies()
                        .stream()
                        .map(Task::getEndDate)
                        .max(LocalDate::compareTo)
                        .orElse(task.getStartDate());

                LocalDate newStart = latestEnd.plusDays(1);
                LocalDate newEnd = newStart.plusDays(task.getDuration() - 1);

                task.setStartDate(newStart);
                task.setEndDate(newEnd);
            }
        }

        taskRepository.saveAll(sortedTasks);
        LocalDate minStart = sortedTasks.stream()
                .map(Task::getStartDate)
                .min(LocalDate::compareTo)
                .orElseThrow(() -> {
                    log.info("Error on getting earliest start date");
                    return new BadRequestException("Error on getting earliest start date");
                });

        LocalDate maxEnd = sortedTasks.stream()
                .map(Task::getEndDate)
                .max(LocalDate::compareTo)
                .orElseThrow(() -> {
                    log.info("Error on getting latest end date");
                    return new BadRequestException("Error on getting latest end date");
                });

        long totalDays = ChronoUnit.DAYS.between(minStart, maxEnd) + 1;

        Map<String, Object> response = new HashMap<>();
        response.put("tasks", TaskMapper.toDTOList(sortedTasks));
        response.put("projectDuration", totalDays);

        return response;
    }

    /**
     * Generates a schedule for all projects in the database
     *
     * @return A list of map containing id of project, name of project, tasks under it, and project duration
     */
    @Transactional
    public List<Map<String, Object>> generateAllSchedule(){
        List<Project> projects = projectRepository.findAll();
        if(projects == null || projects.isEmpty()){
            throw new BadRequestException("No projects found");
        }

        List<Map<String, Object>> allSchedules = new ArrayList<>();

        for(Project project: projects){
            Map<String, Object> schedule = generateSchedule(project.getId());

            schedule.put("projectId", project.getId());
            schedule.put("projectName", project.getName());

            allSchedules.add(schedule);
        }

        return allSchedules;
    }

    /**
     * sorts the task by its date
     * 
     * @param tasks list of tasks under the project
     * @return list of tasks in order
     */
    private  List<Task> sortTask(List<Task> tasks){
        List<Task> result = new ArrayList<>();
        Set<Task> visited = new HashSet<>();

        for(Task task : tasks){
            sortDependencies(task, visited, result);
        }

        return result;
    }

    /**
     * a recursive function for sorting the task based on its dependencies
     *
     * @param task task being checked
     * @param visited list of IDs checked
     * @param result sorted task
     */
    private void sortDependencies(Task task, Set<Task> visited, List<Task> result){

        //avoid infinite recursion
        if(visited.contains(task)){
            return;
        }
        visited.add(task);

        if(task.getDependencies() != null){
            for(Task dep:task.getDependencies()){
                sortDependencies(dep, visited, result);
            }
        }

        //adds the deepest dependency task first
        result.add(task);
    }

    /**
     * Calculates the duration of the task
     *
     * @param start start date (YYYY-MM-DD)
     * @param end end date (YYYY-MM-DD)
     * @return long duration of task
     */
    private long durationCalculator(LocalDate start, LocalDate end){
        // start date counts as day 1
        return ChronoUnit.DAYS.between(start, end) + 1;
    }

    /**
     * Checker if the dates are valid
     *
     * @param start start date (YYYY-MM-DD)
     * @param end end date (YYYY-MM-DD)
     */
    private void validateDates(LocalDate start, LocalDate end) {
        log.info("Validating dates: start=" + start + ", end=" + end );
        if (end.isBefore(start)) {
            log.info("End date cannot be before start date");
            throw new BadRequestException("End date cannot be before start date");
        }
        if (start.isAfter(end)) {
            log.info("Start date cannot be after end date");
            throw new BadRequestException("Start date cannot be after end date");
        }
    }

    /**
     * Check if the start date of parent task is after the end date of dependency task
     * @param task task to be checked for dependency dates
     */
    private void validateDependencyDates(Task task){
        LocalDate parentStart = task.getStartDate().minusDays(1);
        LocalDate dependencyEnd = task.getDependencies().stream()
                .map(Task::getEndDate)
                .max(LocalDate::compareTo)
                .orElseThrow(() -> {
                    log.info("Error on getting latest end date");
                    return new BadRequestException("Error on getting latest end date");
                });

        if(parentStart.isBefore(dependencyEnd)){
            log.info("Cannot add dependency. Cannot set start date before the dependency task's end date.");
            throw new BadRequestException("Cannot add dependency. Cannot set start date before the dependency task's end date.");
        }
    }

    /**
     * Check if IDs added on dependency are invalid
     * @param dependency dependency to be checked
     */
    public void dependencyIdChecker (Set<Long> dependency){
        for(Long id: dependency){
            getTaskDetails(id).orElseThrow(() -> new BadRequestException("Cannot add dependency. ID=" + id + " is invalid"));
        }
    }

}
