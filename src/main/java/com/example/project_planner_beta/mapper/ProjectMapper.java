package com.example.project_planner_beta.mapper;

import com.example.project_planner_beta.dto.ProjectDTO;
import com.example.project_planner_beta.model.Project;

import java.util.List;
import java.util.stream.Collectors;

public class ProjectMapper {

    public static ProjectDTO toDTO(Project project){

        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setCode(project.getCode());

        if(project.getTasks() != null){
            System.out.println("This project contains task(s)");
            dto.setTasks(
                    TaskMapper.toDTOList(project.getTasks())

            );
        }
        return dto;
    }

    public static List<ProjectDTO> toDTOList(List<Project> project){
        return project.stream()
                .map(ProjectMapper::toDTO)
                .collect(Collectors.toList());
    }
}
