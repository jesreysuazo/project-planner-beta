package com.example.project_planner_beta.dto;

import com.example.project_planner_beta.model.TaskStatus;

import java.time.LocalDate;
import java.util.List;

public class TaskDTO {
    private Long id;
    private String name;
    private String projectCode;
    private Long duration;
    private TaskStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<DependencyDTO> dependencies;

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getProjectCode() {return projectCode;}
    public void setProjectCode(String projectCode) {this.projectCode = projectCode;}

    public TaskStatus getStatus() {return status;}
    public void setStatus(TaskStatus status) {this.status = status;}

    public LocalDate getStartDate() {return startDate;}
    public void setStartDate(LocalDate startDate) {this.startDate = startDate;}

    public LocalDate getEndDate() {return endDate;}
    public void setEndDate(LocalDate endDate) {this.endDate = endDate;}

    public Long getDuration() {return duration;}
    public void setDuration(Long duration) {this.duration = duration;}

    public List<DependencyDTO> getDependencies() {return dependencies;}
    public void setDependencies(List<DependencyDTO> dependencies) {this.dependencies = dependencies;}
}
