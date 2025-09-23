package com.example.project_planner_beta.projectplanner.Task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * retrieves a list of tasks under the project by its project code
     * @param projectCode unique 6 digit character string
     * @return list of tasks
     */
    List<Task> findByProjectCode(String projectCode);
}

