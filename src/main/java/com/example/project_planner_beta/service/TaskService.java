package com.example.project_planner_beta.service;

import com.example.project_planner_beta.exception.BadRequestException;
import com.example.project_planner_beta.mapper.TaskMapper;
import com.example.project_planner_beta.model.Project;
import com.example.project_planner_beta.model.Task;
import com.example.project_planner_beta.model.TaskStatus;
import com.example.project_planner_beta.repository.ProjectRepository;
import com.example.project_planner_beta.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;


@Service
public class TaskService {

    @Autowired private TaskRepository taskRepository;

    @Autowired private ProjectRepository projectRepository;

    @Transactional
    public Task createTask(Task task) {
        Project project = projectRepository.findByCode(task.getProjectCode());
        if (project == null) {
            throw new BadRequestException("Project with code " + task.getProjectCode() + " does not exist");
        }

        validateDates(task.getStartDate(), task.getEndDate());

        task.setProjectCode(project.getCode());
        task.setDuration(durationCalculator(task.getStartDate(), task.getEndDate()));
        task.setProject(project);
        task.setStatus(TaskStatus.NOT_STARTED); // default status

        if (task.getDependencies() != null && !task.getDependencies().isEmpty()) {
            for (Task dep : task.getDependencies()) {

                if (!task.getProjectCode().equals(dep.getProjectCode())) {
                    throw new BadRequestException("All dependencies must belong to the same project");
                }
                if (willLoop(task, dep)) {
                    throw new BadRequestException("This action will result in a circular dependency");
                }
            }
        }

        Task savedTask = taskRepository.save(task);

        if(task.getDependencies() != null && !task.getDependencies().isEmpty()){
            savedTask.setDependencies(task.getDependencies());
            savedTask = taskRepository.save(savedTask);
        }

        return savedTask;
    }

    public Task updateTask(Long taskId, Task updatedTask) {
        Task existingRecord = taskRepository.findById(taskId)
                .orElseThrow(() -> new BadRequestException("Task not found"));

        //check if same project code
        if(!Objects.equals(updatedTask.getProjectCode(), existingRecord.getProjectCode())){
            throw new BadRequestException("Cannot change project code");
        }

        // check if dependency is DONE
        if(updatedTask.getStatus() == TaskStatus.IN_PROGRESS || updatedTask.getStatus() == TaskStatus.DONE){
            if(existingRecord.getDependencies()!= null && !existingRecord.getDependencies().isEmpty()) {
                for(Task dep: existingRecord.getDependencies()){
                    System.out.println("dependency id: "+ dep.getId() +",dependency status: "+dep.getStatus());

                    System.out.println("status update: "+updatedTask.getStatus());

                    if(dep.getStatus() == TaskStatus.NOT_STARTED || dep.getStatus() == TaskStatus.IN_PROGRESS){
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
            for (Task dep : updatedTask.getDependencies()) {
                if (!updatedTask.getProjectCode().equals(dep.getProjectCode())) {
                    throw new BadRequestException("All dependencies must belong to the same project");
                }
                if (willLoop(existingRecord, dep)) {
                    throw new BadRequestException("This action will result in a circular dependency");
                }
            }
        }

        // Replace dependencies safely
        existingRecord.getDependencies().clear();
        if (updatedTask.getDependencies() != null) {
            existingRecord.getDependencies().addAll(updatedTask.getDependencies());
        }

        return taskRepository.save(existingRecord);
    }

    public List<Task> getTasksByProjectCode(String code){
        return taskRepository.findByProjectCode(code);
    }

    public void deleteTaskById(Long id){
        taskRepository.deleteById(id);
    }

    public Optional<Task> getTaskDetails(Long id){
        return taskRepository.findById(id);
    }

    private boolean willLoop(Task task, Task dependency){
        Set<Long> visited = new HashSet<>();
        return isDependentOn(dependency, task.getId(), visited);
    }

    // calculate duration of task
    private long durationCalculator(LocalDate start, LocalDate end){

        // start date counts as day 1
        return ChronoUnit.DAYS.between(start, end) + 1;
    }

    //date checker
    private void validateDates(LocalDate start, LocalDate end) {
        if (end.isBefore(start)) {
            throw new BadRequestException("End date cannot be before start date");
        }
        if (start.isAfter(end)) {
            throw new BadRequestException("Start date cannot be after end date");
        }
    }

    private boolean isDependentOn(Task current, Long targetId, Set<Long> visited){
        System.out.println("LOOP CHECKER -- TASK ID: " + targetId + ",DEPENDENT ID" + current.getId() );
        if(current.getId().equals(targetId)){
            return true;
        }
        if(visited.contains(current.getId())){
            return false;
        }
        visited.add(current.getId());


        for (Task dep : current.getDependencies()){
            if(isDependentOn(dep, targetId, visited)){
                return true;
            }
        }

        return false;
    }

    @Transactional
    public Map<String, Object> generateSchedule(Long projectId){
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new BadRequestException("Cannot find project"));

        List<Task> tasks = project.getTasks();
        if(tasks == null || tasks.isEmpty()){
            throw new BadRequestException("No tasks under this Project.");
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
                .orElseThrow(() -> new BadRequestException("Error on getting earliest start date"));

        LocalDate maxEnd = sortedTasks.stream()
                .map(Task::getEndDate)
                .max(LocalDate::compareTo)
                .orElseThrow(() -> new BadRequestException("Error on getting latest end date"));

        long totalDays = ChronoUnit.DAYS.between(minStart, maxEnd) + 1;

        Map<String, Object> response = new HashMap<>();
        response.put("tasks", TaskMapper.toDTOList(sortedTasks));
        response.put("projectDuration", totalDays);

        return response;
    }


    private  List<Task> sortTask(List<Task> tasks){
        List<Task> result = new ArrayList<>();
        Set<Task> visited = new HashSet<>();

        for(Task task : tasks){
            sortDependencies(task, visited, result);
        }

        return result;
    }

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
}
