package com.example.project_planner_beta.projectplanner;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectCode(String projectCode);
}

