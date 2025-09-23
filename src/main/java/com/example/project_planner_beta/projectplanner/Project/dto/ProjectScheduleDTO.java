package com.example.project_planner_beta.projectplanner.Project.dto;

import com.example.project_planner_beta.projectplanner.Task.dto.TaskDTO;

import java.util.List;

public class ProjectScheduleDTO {
    private Long projectId;
    private String projectName;
    private Long projectDuration;
    private List<TaskDTO> tasks;

    public ProjectScheduleDTO(Long projectId, String projectName, Long projectDuration, List<TaskDTO> tasks){
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectDuration = projectDuration;
        this.tasks = tasks;
    }

    public Long getProjectId() {return projectId;}
    public void setProjectId(Long projectId) {this.projectId = projectId;}

    public String getProjectName() {return projectName;}
    public void setProjectName(String projectName) {this.projectName = projectName;}

    public Long getProjectDuration() {return projectDuration;}
    public void setProjectDuration(Long projectDuration) {this.projectDuration = projectDuration;}

    public List<TaskDTO> getTasks() {return tasks;}
    public void setTasks(List<TaskDTO> tasks) {this.tasks = tasks;}
}
