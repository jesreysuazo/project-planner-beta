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

    /**
     * creates new project
     *
     * @param project The project data (only name is required for the payload)
     * @return the created project
     */
    @PostMapping()
    public Project createProject( @RequestBody Project project){
        return projectService.createProject(project.getName());
    }

    /**
     * retrieves all project
     *
     * @return a list of projects
     */
    @GetMapping
    public List<ProjectDTO> getAllProjects(){

        List<Project> project = projectService.getAllProjects();
        return ProjectMapper.toDTOList(project);
    }

    /**
     * retrieves details of a single project by its ID
     *
     * @param id id of project
     * @return details of project
     */
    @GetMapping("/{id}")
    public ProjectDTO getProjectDetails(@PathVariable Long id){
        Project project = projectService.getProjectById(id)
                .orElseThrow(() -> new BadRequestException("No project found"));
        return ProjectMapper.toDTO(project);
    }

    /**
     * generates a schedule for a single project by its ID
     *
     * @param id id of project
     * @return a map containing a list of task in order according to its start dates and end dates
     *              and the duration of project in days
     */
    @PostMapping("/schedule/{id}")
    public Map<String,Object> scheduleProject(@PathVariable Long id){
        return taskService.generateSchedule(id);
    }

    /**
     * generates a schedule for all projects
     * @return a list of map containing a  list of tasks in order according to its start dates and end dates
     *              and the duration of project in days
     */
    @PostMapping("/schedule/all")
    public List<Map<String, Object>> scheduleAllProjects(){
        return taskService.generateAllSchedule();
    }

    /**
     * deletes a project and all tasks under it
     *
     * @param id of project to be deleted
     * @return a confirmation response for deletion of project
     */
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

