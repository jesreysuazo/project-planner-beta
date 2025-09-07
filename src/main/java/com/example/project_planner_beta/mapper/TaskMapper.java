package com.example.project_planner_beta.mapper;

import com.example.project_planner_beta.dto.DependencyDTO;
import com.example.project_planner_beta.dto.TaskDTO;
import com.example.project_planner_beta.model.Task;

import java.util.List;
import java.util.stream.Collectors;

public class TaskMapper {

    public static TaskDTO toDTO(Task task){
        if (task == null){
            return null;
        }

        TaskDTO dto = new TaskDTO();

        dto.setId(task.getId());
        dto.setName(task.getName());
        dto.setDuration(task.getDuration());
        dto.setProjectCode(task.getProjectCode());
        dto.setStatus(task.getStatus());
        dto.setStartDate(task.getStartDate());
        dto.setEndDate(task.getEndDate());

        dto.setDependencies(
                task.getDependencies().stream()
                        .map(d -> new DependencyDTO(d.getId(), d.getName()))
                        .collect(Collectors.toList())
        );

        return dto;
    }

    public static List<TaskDTO> toDTOList(List<Task> tasks){
        for (int i = 0; i < tasks.size(); i++ ){
            Task t = tasks.get(i);
            System.out.println("Added task id: " + t.getId());
        }
        return tasks.stream()
                .map(TaskMapper::toDTO)
                .collect(Collectors.toList());
    }
}
