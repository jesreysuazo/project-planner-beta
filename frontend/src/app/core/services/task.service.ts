import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';
import { Task } from '../models/task.model';

@Injectable({
  providedIn: 'root'
})
export class TaskService {

  constructor(private api: ApiService) { }

  getTasksByProjectCode(code: string): Observable<Task[]> {
    return this.api.get<Task[]>(`/tasks/code/${code}`)
  }

  createTask(task: Partial<Task>): Observable<Task> {
    return this.api.post<Task>('/task', task);
  }

  updateTask(id: number, task: Partial<Task>): Observable<Task> {
    return this.api.put<Task>(`/tasks/${id}`, task);
  }

  deleteTask(id: number): Observable<void> {
    return this.api.delete<void>(`/tasks/${id}`)
  }

}
