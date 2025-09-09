package com.example.project_planner_beta.projectplanner;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;

    //Getters Setters
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getCode() {return code;}
    public void setCode(String code) {this.code = code;}

    public List<Task> getTasks() {return tasks;}
    public void setTasks(List<Task> task) {this.tasks = tasks;}

}
