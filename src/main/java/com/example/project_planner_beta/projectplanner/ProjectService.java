package com.example.project_planner_beta.projectplanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public Project createProject(String name){
        Project project = new Project();
        project.setName(name);
        project.setCode(generateProjectCode());
        project.setTasks(null);

        return projectRepository.save(project);
    }

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

        return code;
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    public List<Task> getTaskByCode(String code) {
        Project project = projectRepository.findByCode(code);
        if (project == null) {
            return Collections.emptyList();
        }

        List<Task> tasks = project.getTasks();
        return tasks != null ? tasks : Collections.emptyList();
    }
}
