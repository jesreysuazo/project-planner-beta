package com.example.project_planner_beta.projectplanner;

import org.springframework.data.jpa.repository.JpaRepository;


public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * retrieves project containing the code
     *
     * @param code unique 6 character string
     * @return a project with the code
     */
    Project findByCode(String code);
}
