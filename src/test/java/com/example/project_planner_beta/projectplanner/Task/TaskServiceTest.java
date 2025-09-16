package com.example.project_planner_beta.projectplanner.Task;

import com.example.project_planner_beta.projectplanner.Project.Project;
import com.example.project_planner_beta.projectplanner.Project.ProjectRepository;
import com.example.project_planner_beta.projectplanner.Project.dto.ProjectScheduleDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskService taskService;

    private Project project;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        project = new Project();
        project.setId(Long.valueOf(1));
        project.setCode("ABCDEF");
        project.setName("Project 1");
    }

    @Test
    void createTask_ShouldSave_ValidCode(){
        //arrange
        Task task = new Task();
        task.setName("Task 1");
        task.setStartDate(LocalDate.of(2025,9,16));
        task.setEndDate(LocalDate.of(2025, 9, 17));
        task.setDependencies(new HashSet<>());
        task.setProjectCode("ABCDEF");

        when(projectRepository.findByCode("ABCDEF")).thenReturn(project);
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task saved =invocation.getArgument(0);
            saved.setId(Long.valueOf(2));
            return saved;
        });

        //assert
        Task savedTask = taskService.createTask(task);

        //act
        assertThat(savedTask.getId()).isEqualTo(Long.valueOf(2));
        assertThat(savedTask.getDuration()).isEqualTo(2);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void createTask_ShouldThrow_WhenInvalidCode(){
        //arrange
        Task task = new Task();
        task.setName("Task 1");
        task.setStartDate(LocalDate.of(2025,9,16));
        task.setEndDate(LocalDate.of(2025, 9, 17));
        task.setDependencies(new HashSet<>());
        task.setProjectCode("ABCDEF");

        //assert
        when(projectRepository.findByCode("ACBDEF")).thenReturn(null);

        //act
        assertThatThrownBy(() -> taskService.createTask(task))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unable to process request. Project not found");
    }

    @Test
    void updateTask_ShouldUpdate_WhenValid(){
        Task existingtask = new Task();
        existingtask.setId(Long.valueOf(1));
        existingtask.setProjectCode("ABCDEF");
        existingtask.setDependencies(new HashSet<>());

        when(taskRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(existingtask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task updatedTask = new Task();
        updatedTask.setProjectCode("ABCDEF");
        updatedTask.setStartDate(LocalDate.of(2025, 9,28));
        updatedTask.setEndDate(LocalDate.of(2025, 9, 29));
        updatedTask.setDependencies(new HashSet<>());

        Task result = taskService.updateTask(Long.valueOf(1), updatedTask);

        assertThat(result.getDuration()).isEqualTo(2);
        verify(taskRepository).save(existingtask);

    }

    @Test
    void updateTask_ShouldThrow_WhenInvalidId(){
        when(taskRepository.findById(Long.valueOf(100))).thenReturn(Optional.empty());

        Task updatedTask = new Task();
        updatedTask.setName("Update Task 1");

        assertThatThrownBy(() -> taskService.updateTask(Long.valueOf(100), updatedTask))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unable to process request. Task not found");
    }

@Test
    void generateSchedule_ShouldCalculateDuration_WhenValid(){

        Project p1 = new Project();
        p1.setId(Long.valueOf(2));
        p1.setCode("ASDFGH");
        p1.setName("Project 2");

        Task t1 = new Task();
        t1.setId(Long.valueOf(1));
        t1.setName("Task 1");
        t1.setStartDate(LocalDate.of(2025,9,16));
        t1.setEndDate(LocalDate.of(2025, 9, 17));
        t1.setDependencies(new HashSet<>());
        t1.setProjectCode("ASDFGH");
        t1.setProject(p1);

        Task t2 = new Task();
        t2.setId(Long.valueOf(2));
        t2.setName("Task 2");
        t2.setStartDate(LocalDate.of(2025,9,18));
        t2.setEndDate(LocalDate.of(2025, 9, 19));
        t2.setDependencies(Set.of(t1));
        t2.setProjectCode("ASDFGH");
        t2.setProject(p1);

        p1.setTasks(new ArrayList<>(List.of(t1,t2)));


        when(projectRepository.findById(Long.valueOf(2))).thenReturn(Optional.of(p1));
        when(taskRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectScheduleDTO schedule = taskService.generateSchedule(Long.valueOf(2));

        assertThat(schedule.getProjectDuration()).isGreaterThan(0);
        assertThat(schedule.getTasks()).hasSize(2);
        verify(taskRepository).saveAll(anyList());
    }
}

