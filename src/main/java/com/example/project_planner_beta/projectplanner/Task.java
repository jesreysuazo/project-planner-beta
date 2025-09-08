package com.example.project_planner_beta.projectplanner;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String projectCode;

    private Long duration;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "task_dependencies",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "dependency_id")
    )
    private Set<Task> dependencies = new HashSet<>();

    public Task() {}

    // Getters Setters
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getProjectCode() {return projectCode;}
    public void setProjectCode(String projectCode) {this.projectCode = projectCode;}

    public Long getDuration() {return duration;}
    public void setDuration(Long duration) {this.duration = duration;}

    public LocalDate getStartDate() {return startDate;}
    public void setStartDate(LocalDate startDate) {this.startDate = startDate;}

    public LocalDate getEndDate() {return endDate;}
    public void setEndDate(LocalDate endDate) {this.endDate = endDate;}

    public Set<Task> getDependencies() {return dependencies;}
    public void setDependencies(Set<Task> dependencies) {this.dependencies = dependencies;}

    public TaskStatus getStatus() {return status;}
    public void setStatus(TaskStatus status) {this.status = status;}

    public Project getProject() {return project;}
    public void setProject(Project project) {this.project = project;}
}
