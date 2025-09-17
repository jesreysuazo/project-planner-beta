package com.example.project_planner_beta.projectplanner.Task;

import com.example.project_planner_beta.exception.NotFoundException;
import com.example.project_planner_beta.projectplanner.Project.Project;
import com.example.project_planner_beta.projectplanner.Task.dto.CreateTaskRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    // include this to prevent error:
    //  No qualifying bean of type 'com.example.project_planner_beta.projectplanner.Task.TaskRepository' available
    @MockitoBean
    private TaskRepository taskRepository;

    private Project p1;
    private Task t1;
    private Task t2;
    private CreateTaskRequestDTO createReq;

    @BeforeEach
    void setUp(){
        p1 = new Project();
        p1.setId(Long.valueOf(1));
        p1.setCode("ABCDEF");
        p1.setName("Project 1");

        t1 = new Task();
        t1.setId(Long.valueOf(1));
        t1.setName("Task 1");
        t1.setStartDate(LocalDate.of(2025,9,16));
        t1.setEndDate(LocalDate.of(2025, 9, 17));
        t1.setStatus(TaskStatus.NOT_STARTED);
        t1.setDuration(Long.valueOf(2));
        t1.setDependencies(new HashSet<>());
        t1.setProjectCode("ABCDEF");
        t1.setProject(p1);

        t2 = new Task();
        t2.setId(Long.valueOf(2));
        t2.setName("Task 2");
        t2.setStartDate(LocalDate.of(2025,9,21));
        t2.setEndDate(LocalDate.of(2025, 9, 22));
        t2.setStatus(TaskStatus.NOT_STARTED);
        t2.setDuration(Long.valueOf(2));
        t2.setDependencies(Set.of(t1));
        t2.setProjectCode("ABCDEF");
        t2.setProject(p1);

        createReq = new CreateTaskRequestDTO();
        createReq.setName(t1.getName());
        createReq.setProjectCode(t1.getProjectCode());
        createReq.setStartDate(t1.getStartDate());
        createReq.setEndDate(t1.getEndDate());
        createReq.setDependencyIds(t1.getDependencies()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toList())
        );

    }

    @Test
    void getTasksByCode_ShouldReturn200_WhenValid() throws Exception {
        List<Task> mockTasks = List.of(t1, t2);

        when(taskService.getTasksByProjectCode("ABCDEF")).thenReturn(mockTasks);

        mockMvc.perform(get("/api/tasks/code/ABCDEF"))
                .andExpect(status().isOk());

        verify(taskService).getTasksByProjectCode("ABCDEF");
    }


    @Test
    void createTask_ShouldReturn201_WhenValid() throws Exception{
        when(taskService.createTask(any(Task.class))).thenReturn(t1);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated());

        verify(taskService).createTask(any(Task.class));
    }

    @Test
    void updateTask_ShouldReturn200_WhenValid() throws Exception{
        Task oldRec = new Task();
        oldRec.setId(1L);
        oldRec.setProjectCode("ABCDEF");

        when(taskService.getTaskDetails(1L)).thenReturn(Optional.of(oldRec));

        Task updated = new Task();
        updated.setId(1L);
        updated.setName("Updated Task 1");
        updated.setStatus(TaskStatus.NOT_STARTED);
        updated.setStartDate(t1.getStartDate());
        updated.setEndDate(t1.getEndDate());
        updated.setDependencies(t1.getDependencies());

        when(taskService.updateTask(any(Long.class),any(Task.class))).thenReturn(updated);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk());

        verify(taskService).updateTask(any(Long.class),any(Task.class));
    }

    @Test
    void updateTask_ShouldReturn500_WhenInvalidId() throws Exception{
        Task updated = new Task();
        updated.setId(1L);
        updated.setName("Updated Task 1");
        updated.setStatus(TaskStatus.NOT_STARTED);
        updated.setStartDate(t1.getStartDate());
        updated.setEndDate(t1.getEndDate());
        updated.setDependencies(t1.getDependencies());

        when(taskService.getTaskDetails(1L)).thenThrow(new NotFoundException("Update failed. Invalid ID provided"));

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());

        verify(taskService).getTaskDetails(1L);
    }

    @Test
    void getTaskById_ShouldReturn200_WhenValid() throws Exception{
        when(taskService.getTaskDetails(1L)).thenReturn(Optional.of(t1));

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk());

        verify(taskService).getTaskDetails(1L);
    }

    @Test
    void getTaskById_ShouldReturn500_WhenInvalidId() throws Exception{

        mockMvc.perform(get("/api/tasks/100"))
                .andExpect(status().isInternalServerError());

        verify(taskService).getTaskDetails(100L);
    }

    @Test
    void deleteTask_ShouldReturn204_WhenValid() throws Exception{
        when(taskService.getTaskDetails(1L)).thenReturn(Optional.of(t1));

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTaskById(1L);
    }

    @Test
    void deleteTask_ShouldReturn404_WhenInvalidId() throws Exception{

        mockMvc.perform(delete("/api/tasks/100"))
                .andExpect(status().isNotFound());

        verify(taskService).getTaskDetails(100L);
    }
}
