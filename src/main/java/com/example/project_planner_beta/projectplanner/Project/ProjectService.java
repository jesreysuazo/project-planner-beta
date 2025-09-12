package com.example.project_planner_beta.projectplanner.Project;

import com.example.project_planner_beta.common.BadRequestException;
import com.example.project_planner_beta.projectplanner.Task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;

@Service
public class ProjectService {
    private static final Logger log = Logger.getLogger(ProjectService.class.getName());
    @Autowired
    private ProjectRepository projectRepository;

    /**
     * Create a project
     * @param name name of the project
     * @return created project
     */
    public Project createProject(String name){
        Project project = new Project();
        project.setName(name);
        project.setCode(generateProjectCode());
        project.setTasks(null);

        Project savedProject = projectRepository.save(project);
        log.info("Project ID=" + savedProject.getId() + " created");
        return savedProject;
    }

    /**
     * generate project code upon project creation
     *
     * @return 6 digit string for project code
     */
    public String generateProjectCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789" ;
        Random random = new Random();
        String code;


        do{
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                builder.append(characters.charAt(random.nextInt(characters.length())));
            }
            code = builder.toString();
        } while (projectRepository.findByCode(code) != null);

        log.info("Generated code: " + code);
        return code;
    }

    /**
     * Returns a list of all project in database
     *
     * @return list of all projects
     */
    public List<Project> getAllProjects() {
        List<Project> projectList = projectRepository.findAll();
        log.info("Retrieved " + projectList.size() + " projects");
        return projectList;
    }

    /**
     * get project details using its ID
     *
     * @param id id of the project
     * @return an optional if project is found
     */
    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    /**
     * deletes a project from database using its ID
     * @param id project id
     */
    public void deleteProject(Long id) {
        log.info("Deleting Project ID: "+ id);
        Project project = projectRepository.findById(id).orElseThrow(() -> new BadRequestException("Project not found."));
        projectRepository.deleteById(project.getId());
    }

    /**
     * Gets all tasks under the project
     *
     * @param code 6 digit project code
     * @return list of tasks under the project
     */
    public List<Task> getTaskByCode(String code) {
        Project project = projectRepository.findByCode(code);
        if (project == null) {
            return Collections.emptyList();
        }

        List<Task> tasks = project.getTasks();
        log.info("Retrieved " + tasks.size() + " tasks");
        return tasks != null ? tasks : Collections.emptyList();
    }
}
