package com.example.project_planner_beta.projectplanner;

import com.example.project_planner_beta.common.BadRequestException;
import com.example.project_planner_beta.projectplanner.dto.ProjectDTO;
import com.example.project_planner_beta.projectplanner.tools.ProjectMapper;
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

    @PostMapping("/schedule/all")
    public List<Map<String, Object>> scheduleAllProjects(){
        return taskService.generateAllSchedule();
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
