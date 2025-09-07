package com.example.project_planner_beta.controller;

import com.example.project_planner_beta.dto.ProjectDTO;
import com.example.project_planner_beta.exception.BadRequestException;
import com.example.project_planner_beta.mapper.ProjectMapper;
import com.example.project_planner_beta.model.Project;
import com.example.project_planner_beta.model.Task;
import com.example.project_planner_beta.service.ProjectService;
import com.example.project_planner_beta.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final TaskService taskService;

    public ProjectController(ProjectService projectService, TaskService taskService) {
        this.projectService = projectService;
        this.taskService = taskService;
    }

    @GetMapping("/{code}/tasks")
    public List<Task> getTasksByProjectCode(@PathVariable String code){
        return projectService.getTaskByCode(code);
    }

    @PostMapping()
    public Project createProject( @RequestBody Project project){
        return projectService.createProject(project.getName());
    }

    @GetMapping
    public List<ProjectDTO> getAllProjects(){

        List<Project> project = projectService.getAllProjects();
        return ProjectMapper.toDTOList(project);
    }

    @GetMapping("/{id}")
    public ProjectDTO getProjectDetails(@PathVariable Long id){
        Project project = projectService.getProjectById(id)
                .orElseThrow(() -> new BadRequestException("No project found"));
        return ProjectMapper.toDTO(project);
    }

    @PostMapping("/schedule/{id}")
    public Map<String,Object> scheduleProject(@PathVariable Long id){
        return taskService.generateSchedule(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id){
        Optional<Project> project = projectService.getProjectById(id);
        if (project.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

}
