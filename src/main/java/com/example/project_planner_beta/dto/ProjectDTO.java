package com.example.project_planner_beta.dto;

import java.util.List;

public class ProjectDTO {
    private Long id;
    private String name;
    private String code;
    private List<TaskDTO> tasks;


    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getCode() {return code;}
    public void setCode(String code) {this.code = code;}

    public List<TaskDTO> getTasks() {return tasks;}
    public void setTasks(List<TaskDTO> tasks) {this.tasks = tasks;}

}
