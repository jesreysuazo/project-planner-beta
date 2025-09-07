package com.example.project_planner_beta.dto;

import java.time.LocalDate;
import java.util.List;

public class CreateTaskRequest {
    private String name;
    private String projectCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Long> dependencyIds;

    //getters setters

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getProjectCode() { return projectCode;}
    public void setProjectCode(String projectCode) {this.projectCode = projectCode;}

    public LocalDate getStartDate() {return startDate;}
    public void setStartDate(LocalDate startDate) {this.startDate = startDate;}

    public LocalDate getEndDate() {return endDate;}
    public void setEndDate(LocalDate endDate) {this.endDate = endDate;}

    public List<Long> getDependencyIds() {return dependencyIds;}
    public void setDependencyIds(List<Long> dependencyIds) {this.dependencyIds = dependencyIds;}
}

