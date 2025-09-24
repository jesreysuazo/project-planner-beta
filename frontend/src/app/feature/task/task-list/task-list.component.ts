import { Component, OnInit, inject, model, signal } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { TaskService } from '../../../core/services/task.service';
import { Task } from '../../../core/models/task.model';
import {
  CdkDragDrop,
  moveItemInArray,
  transferArrayItem,
  CdkDrag,
  CdkDropList,
} from '@angular/cdk/drag-drop';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle,
} from '@angular/material/dialog';
import { TaskFormComponent } from '../task-form/task-form.component';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [MatIconModule, CdkDropList, CdkDrag, RouterModule],
  templateUrl: './task-list.component.html',
  styleUrl: './task-list.component.css'
})
export class TaskListComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private taskService: TaskService
  ) { }

  tasks: Task[] = [];
  todo: Task[] = [];
  inprogress: Task[] = [];
  done: Task[] = [];
  columns = [
    {
      name: 'Not Started',
      data: this.todo,
      connectedTo: ['doneList', 'inprogressList']
    },
    {
      name: 'In Progress',
      data: this.inprogress,
      connectedTo: ['doneList', 'todoList']
    },
    {
      name: 'Done',
      data: this.done,
      connectedTo: ['todoList', 'inprogressList']
    }
  ];

  readonly dialog = inject(MatDialog);

  ngOnInit(): void {
    const projectCode = this.route.snapshot.paramMap.get('code');
    this.loadTasks(projectCode ? projectCode : '');
  }

  loadTasks(projectCode: string) {
    this.taskService.getTasksByProjectCode(projectCode).subscribe((data) => {
      this.tasks = data.sort((a, b) => {
        return new Date(a.startDate).getTime() - new Date(b.startDate).getTime();
      });
      this.sortTask(this.tasks);
    });
  }

  sortTask(data: Task[]) {
    for (let index = 0; index < data.length; index++) {
      const element = data[index];

      if (element.status == 'IN_PROGRESS') {
        this.inprogress.push(element);
      }

      if (element.status == 'DONE') {
        this.done.push(element);
      }

      if (element.status == 'NOT_STARTED') {
        this.todo.push(element);
      }
    }
  }

  deleteTask(id: number) {
    this.taskService.deleteTask(id).subscribe((res) => {
      console.log(res);
    })
  }

  drop(event: CdkDragDrop<Task[]>) {
    if (event.previousContainer === event.container) {
      console.log('move inside')
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      console.log('move outside')
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex,
      );
    }
  }

  openDialog(data: Task){
    const dialogRef = this.dialog.open(TaskFormComponent, {
      data: data,
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed', result);
      if (result !== undefined) {

      }
    });
  }
}
