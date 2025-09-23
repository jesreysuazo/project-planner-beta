import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';
import { Project } from '../models/project.model';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {

  constructor(private api: ApiService) { }

  getProjects(): Observable<Project[]>{
    return this.api.get<Project[]>('/projects')
  }

  createProject(project: Partial<Project>): Observable<Project>{
    return this.api.post<Project>('/projects', project);
  }

  deleteProject(id:number): Observable<void>{
    return this.api.delete<void>(`/projects/${id}`)
  }
}
