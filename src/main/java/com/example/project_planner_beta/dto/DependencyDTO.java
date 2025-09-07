package com.example.project_planner_beta.dto;

public class DependencyDTO {
    private Long id;
    private String name;

    public DependencyDTO(){}

    public DependencyDTO(Long id, String name){
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
