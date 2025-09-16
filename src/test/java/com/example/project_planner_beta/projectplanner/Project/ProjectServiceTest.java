package com.example.project_planner_beta.projectplanner.Project;

import com.example.project_planner_beta.projectplanner.Task.Task;
import com.example.project_planner_beta.projectplanner.Task.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project p1;
    private Project p2;
    private Task t1;
    private Task t2;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        p1 = new Project();
        p1.setId(Long.valueOf(1));
        p1.setCode("ABCDEF");
        p1.setName("Project 1");

        p2 = new Project();
        p2.setId(Long.valueOf(2));
        p2.setCode("QWERTY");
        p2.setName("Project 2");

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
        t2.setStartDate(LocalDate.of(2025,9,18));
        t2.setEndDate(LocalDate.of(2025, 9, 19));
        t2.setStatus(TaskStatus.NOT_STARTED);
        t2.setDuration(Long.valueOf(2));
        t2.setDependencies(Set.of(t1));
        t2.setProjectCode("ABCDEF");
        t2.setProject(p1);
    }

    @Test
    void createProject_ShouldReturn_WhenValid(){
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> {
            Project saved = invocation.getArgument(0);
            saved.setId(Long.valueOf(3));
            return saved;
        });
        Project created = projectService.createProject("Project 3");

        assertThat(created.getId()).isEqualTo(Long.valueOf(3));
        assertThat(created.getName()).isEqualTo("Project 3");
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void generateProjectCode_ShouldReturnCode(){
        String code = projectService.generateProjectCode();

        assertThat(code).hasSize(6);
    }

    @Test
    void getAllProjects_ShouldReturnList(){
        when(projectRepository.findAll()).thenReturn(List.of(p1,p2));

        List<Project> list = projectService.getAllProjects();

        assertThat(list).hasSize(2);
        verify(projectRepository).findAll();
    }

    @Test
    void getProjectById_ShouldReturnOptional_WhenValid(){
        when(projectRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(p1));

        Optional<Project> project = projectService.getProjectById(Long.valueOf(1));

        assertThat(project).isPresent();
        verify(projectRepository).findById(Long.valueOf(1));
    }

    @Test
    void getProjectById_ShouldReturnOptional_WhenEmpty(){
        when(projectRepository.findById(Long.valueOf(100))).thenReturn(Optional.empty());

        Optional<Project> project = projectService.getProjectById(Long.valueOf(100));

        assertThat(project).isEmpty();
        verify(projectRepository).findById(Long.valueOf(100));
    }

    @Test
    void deleteProject_ShouldDelete_WhenValid(){
        when(projectRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(p1));
        doNothing().when(projectRepository).deleteById(Long.valueOf(1));

        projectService.deleteProject(Long.valueOf(1));

        verify(projectRepository).deleteById(Long.valueOf(1));
    }

    @Test
    void deleteProject_ShouldDelete_WhenInvalidProjectID(){
        when(projectRepository.findById(Long.valueOf(100))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.deleteProject(Long.valueOf(100)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unable to process request. Project not found.");
    }



}
