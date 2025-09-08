package com.example.project_planner_beta.projectplanner;

import org.springframework.data.jpa.repository.JpaRepository;


public interface ProjectRepository extends JpaRepository<Project, Long> {
    Project findByCode(String code);
}
