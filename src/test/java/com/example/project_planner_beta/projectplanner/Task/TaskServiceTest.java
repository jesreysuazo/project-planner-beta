package com.example.project_planner_beta.projectplanner.Task;

import com.example.project_planner_beta.exception.BadRequestException;
import com.example.project_planner_beta.exception.NotFoundException;
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

    private Project p1;
    private Project p2;
    private Project p3;
    private Task t1;
    private Task t2;
    private Task t3;

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

        p3 = new Project();
        p3.setId(Long.valueOf(3));
        p3.setCode("ZXCVBN");
        p3.setName("Project 3");

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

        t3 = new Task();
        t3.setId(Long.valueOf(3));
        t3.setName("Task 3");
        t3.setStartDate(LocalDate.of(2025,9,18));
        t3.setEndDate(LocalDate.of(2025, 9, 19));
        t3.setStatus(TaskStatus.NOT_STARTED);
        t3.setDuration(Long.valueOf(2));
        t3.setDependencies(new HashSet<>());
        t3.setProjectCode("QWERTY");
        t3.setProject(p2);

    }

    @Test
    void createTask_ShouldSave_ValidCode(){
        //arrange
        when(projectRepository.findByCode("ABCDEF")).thenReturn(p1);
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task saved =invocation.getArgument(0);
            saved.setId(Long.valueOf(2));
            return saved;
        });

        //assert
        Task savedTask = taskService.createTask(t2);

        //act
        assertThat(savedTask.getId()).isEqualTo(Long.valueOf(2));
        assertThat(savedTask.getDuration()).isEqualTo(2);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void createTask_ShouldThrow_WhenInvalidDate(){
        Task task = new Task();
        task.setName("Baddates");
        task.setStartDate(LocalDate.of(2025,9,19));
        task.setEndDate(LocalDate.of(2025, 9, 17));
        task.setProjectCode("ABCDEF");
        task.setDependencies(new HashSet<>());

        when(projectRepository.findByCode("ABCDEF")).thenReturn(p1);

        assertThatThrownBy(() -> taskService.createTask(task))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unable to process request. Invalid dates.");
    }

    @Test
    void createTask_ShouldThrow_WhenDependencyCodeMismatch(){
        Task task = new Task();
        task.setId(Long.valueOf(3));
        task.setName("Task 3");
        task.setStartDate(LocalDate.of(2025,9,9));
        task.setEndDate(LocalDate.of(2025, 9, 10));
        task.setDuration(Long.valueOf(2));
        task.setProjectCode("ASDFGH");
        task.setDependencies(new HashSet<>());

        t1.setDependencies(Set.of(task));

        when(projectRepository.findByCode("ABCDEF")).thenReturn(p1);
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));


        assertThatThrownBy(() -> taskService.createTask(t1))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Unable to process request. All dependencies must belong to the same project.");
    }

    @Test
    void createTask_ShouldThrow_WhenCircularDependency(){
        Task task = new Task();
        task.setId(Long.valueOf(3));
        task.setName("Task 3");
        task.setStartDate(LocalDate.of(2025,9,9));
        task.setEndDate(LocalDate.of(2025, 9, 10));
        task.setDuration(Long.valueOf(2));
        task.setProjectCode("ABCDEF");
        task.setDependencies(Set.of(t2));
        t1.setDependencies(Set.of(task));

        when(projectRepository.findByCode("ABCDEF")).thenReturn(p1);
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThatThrownBy(() -> taskService.createTask(t1))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Unable to process request. Circular dependency detected.");
    }

    @Test
    void createTask_ShouldThrow_WhenInvalidCode(){
        //assert
        when(projectRepository.findByCode("ABCDEF")).thenReturn(null);

        //act
        assertThatThrownBy(() -> taskService.createTask(t1))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Project not found with code: " + "ABCDEF");
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
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Unable to process request. Task not found with ID= " + 100);
    }

    @Test
    void updateTask_ShouldThrow_WhenDependencyNotDONE(){

        when(taskRepository.findById(Long.valueOf(2))).thenReturn(Optional.of(t2));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task updatedTask = new Task();
        updatedTask.setProjectCode("ABCDEF");
        updatedTask.setStartDate(LocalDate.of(2025, 9,18));
        updatedTask.setEndDate(LocalDate.of(2025, 9, 19));
        updatedTask.setStatus(TaskStatus.IN_PROGRESS);
        updatedTask.setDependencies(Set.of(t1));

        assertThatThrownBy(() -> taskService.updateTask(Long.valueOf(2), updatedTask))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Unable to process request. Dependencies must be completed first");

    }

    @Test
    void updateTask_ShouldThrow_WhenCodeMismatch(){

        when(taskRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(t1));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task updatedTask = new Task();
        updatedTask.setProjectCode("ASDFGH");
        updatedTask.setStartDate(LocalDate.of(2025, 9,16));
        updatedTask.setEndDate(LocalDate.of(2025, 9, 17));

        assertThatThrownBy(() -> taskService.updateTask(Long.valueOf(1), updatedTask))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Unable to process request. Cannot change project code");
    }

    @Test
    void updateTask_ShouldThrow_WhenDependencyCodeMismatch(){
        Task task = new Task();
        task.setId(Long.valueOf(3));
        task.setName("Task 3");
        task.setStartDate(LocalDate.of(2025,9,9));
        task.setEndDate(LocalDate.of(2025, 9, 10));
        task.setDuration(Long.valueOf(2));
        task.setProjectCode("ASDFGH");
        task.setDependencies(new HashSet<>());

        when(taskRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(t1));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task updatedTask = new Task();
        updatedTask.setProjectCode("ABCDEF");
        updatedTask.setStartDate(LocalDate.of(2025, 9,16));
        updatedTask.setEndDate(LocalDate.of(2025, 9, 17));
        updatedTask.setDependencies(Set.of(task));

        assertThatThrownBy(() -> taskService.updateTask(Long.valueOf(1), updatedTask))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Unable to process request. All dependencies must belong to the same project.");
    }

    @Test
    void updateTask_ShouldThrow_WhenCircularDependency(){
        Task task = new Task();
        task.setId(Long.valueOf(3));
        task.setName("Task 3");
        task.setStartDate(LocalDate.of(2025,9,9));
        task.setEndDate(LocalDate.of(2025, 9, 10));
        task.setDuration(Long.valueOf(2));
        task.setProjectCode("ABCDEF");
        task.setDependencies(Set.of(t2));

        when(taskRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(t1));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task updatedTask = new Task();
        updatedTask.setProjectCode("ABCDEF");
        updatedTask.setStartDate(LocalDate.of(2025, 9,16));
        updatedTask.setEndDate(LocalDate.of(2025, 9, 17));
        updatedTask.setDependencies(Set.of(task));

        assertThatThrownBy(() -> taskService.updateTask(Long.valueOf(1), updatedTask))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Unable to process request. Circular dependency detected.");
    }

    @Test
    void getTaskByProjectCode_ShouldReturnList_WhenValid(){
        when(taskRepository.findByProjectCode("ABCDEF")).thenReturn(List.of(t1,t2));

        List<Task> result = taskService.getTasksByProjectCode("ABCDEF");

        assertThat(result).hasSize(2);
        verify(taskRepository).findByProjectCode("ABCDEF");
    }

    @Test
    void deleteTaskById_ShouldDelete_WhenValid(){
        doNothing().when(taskRepository).deleteById(Long.valueOf(1));

        taskService.deleteTaskById(Long.valueOf(1));

        verify(taskRepository).deleteById(Long.valueOf(1));
    }

    @Test
    void getTaskDetails_ShouldReturnOptional(){
        when(taskRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(t1));
        Optional<Task> optional = taskService.getTaskDetails(Long.valueOf(1));

        assertThat(optional).isPresent();
        assertThat(optional.get().getName()).isEqualTo("Task 1");
    }

    @Test
    void generateSchedule_ShouldCalculateDuration_WhenValid(){
        p1.setTasks(List.of(t1,t2));

        when(projectRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(p1));
        when(taskRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectScheduleDTO schedule = taskService.generateSchedule(Long.valueOf(1));

        assertThat(schedule.getProjectDuration()).isGreaterThan(0);
        assertThat(schedule.getTasks()).hasSize(2);
        verify(taskRepository).saveAll(anyList());
    }

    @Test
    void generateSchedule_ShouldThrow_WhenProjectHasNoTask(){
        when(projectRepository.findById(Long.valueOf(3))).thenReturn(Optional.of(p3));

        assertThatThrownBy(() -> taskService.generateSchedule(Long.valueOf(3)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Unable to process request. Project has no tasks.");
    }

    @Test
    void generateAllSchedule_ShouldReturn_WhenValid(){
        p1.setTasks(List.of(t1,t2));
        p2.setTasks(List.of(t3));

        when(projectRepository.findAll()).thenReturn(List.of(p1,p2));
        when(projectRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(p1));
        when(projectRepository.findById(Long.valueOf(2))).thenReturn(Optional.of(p2));

        List<ProjectScheduleDTO> list = taskService.generateAllSchedule();

        assertThat(list).hasSize(2);
    }

    @Test
    void generateAllSchedule_ShouldReturn_WhenAProjectHasNoTask(){
        p1.setTasks(List.of(t1,t2));
        p2.setTasks(List.of(t3));
        p3.setTasks(new ArrayList<>());

        when(projectRepository.findAll()).thenReturn(List.of(p1,p2,p3));
        when(projectRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(p1));
        when(projectRepository.findById(Long.valueOf(2))).thenReturn(Optional.of(p2));
        when(projectRepository.findById(Long.valueOf(3))).thenReturn(Optional.of(p3));


        assertThatThrownBy(() -> taskService.generateAllSchedule())
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Unable to process request. Project has no tasks.");
    }

    @Test
    void generateAllSchedule_ShouldReturn_WhenNoProject(){
        when(projectRepository.findAll()).thenReturn(new ArrayList<>());

        assertThatThrownBy(() -> taskService.generateAllSchedule())
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Unable to process request. No projects found");
    }



}
