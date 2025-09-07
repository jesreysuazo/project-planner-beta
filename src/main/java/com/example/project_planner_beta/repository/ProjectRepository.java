package com.example.project_planner_beta.repository;

import com.example.project_planner_beta.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProjectRepository extends JpaRepository<Project, Long> {
    Project findByCode(String code);
}
