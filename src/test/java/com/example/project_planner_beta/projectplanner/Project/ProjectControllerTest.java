package com.example.project_planner_beta.projectplanner.Project;

import com.example.project_planner_beta.projectplanner.Task.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private ProjectRepository projectRepository;

    @MockitoBean
    private TaskService taskService;

    private Project p1;

    @BeforeEach
    void setUp() {
        p1 = new Project();
        p1.setId(Long.valueOf(1));
        p1.setCode("ABCDEF");
        p1.setName("Project 1");
    }

    @Test
    void createTask_ShouldReturn201_WhenValid() throws Exception{
        Project req = new Project();
        req.setName("Project 1");
        when(projectService.createProject(any(String.class))).thenReturn(p1);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        verify(projectService).createProject(any(String.class));
    }

    @Test
    void getAllProjects_ShouldReturn200_WhenValid() throws Exception{
        List<Project> list = List.of(p1);

        when(projectService.getAllProjects()).thenReturn(list);

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk());

        verify(projectService).getAllProjects();
    }

    @Test
    void getProjectDetails_ShouldReturn200_WhenValid() throws Exception{
        when(projectService.getProjectById(1L)).thenReturn(Optional.of(p1));

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk());

        verify(projectService).getProjectById(1L);
    }

    @Test
    void getProjectDetails_ShouldReturn404_WhenInvalid() throws Exception{
        mockMvc.perform(get("/api/projects/100"))
                .andExpect(status().isNotFound());

        verify(projectService).getProjectById(100L);
    }

    @Test
    void scheduleProject_ShouldReturn200_WhenValid() throws Exception{
        when(projectService.getProjectById(1L)).thenReturn(Optional.of(p1));

        mockMvc.perform(post("/api/projects/schedule/1"))
                .andExpect(status().isOk());

        verify(taskService).generateSchedule(1L);
    }

    @Test
    void scheduleAllProjects_ShouldReturn200_WhenValid() throws Exception{
        mockMvc.perform(post("/api/projects/schedule/all"))
                .andExpect(status().isOk());

        verify(taskService).generateAllSchedule();
    }

    @Test
    void deleteProject_ShouldReturn204_WhenValid() throws Exception{
        when(projectService.getProjectById(1L)).thenReturn(Optional.of(p1));

        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isNoContent());

        verify(projectService).deleteProject(1L);
    }

    @Test
    void deleteProject_ShouldReturn404_WhenInvalidId() throws Exception{
        mockMvc.perform(delete("/api/projects/100"))
                .andExpect(status().isNotFound());

        verify(projectService).getProjectById(100L);
    }

}
