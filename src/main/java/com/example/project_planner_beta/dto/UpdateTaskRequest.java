package com.example.project_planner_beta.dto;

import com.example.project_planner_beta.model.TaskStatus;

import java.time.LocalDate;
import java.util.List;

public class UpdateTaskRequest {
    private String name;
    private TaskStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Long> dependencyIds;

    //getters setters
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public TaskStatus getStatus() {return status;}
    public void setStatus(TaskStatus status) {this.status = status;}

    public LocalDate getStartDate() {return startDate;}
    public void setStartDate(LocalDate startDate) {this.startDate = startDate;}

    public LocalDate getEndDate() {return endDate;}
    public void setEndDate(LocalDate endDate) {this.endDate = endDate;}

    public List<Long> getDependencyIds() {return dependencyIds;}
    public void setDependencyIds(List<Long> dependencyIds) {this.dependencyIds = dependencyIds;}
}


