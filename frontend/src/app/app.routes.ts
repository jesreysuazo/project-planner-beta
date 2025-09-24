import { Routes } from '@angular/router';
import { ProjectListComponent } from './feature/project/project-list/project-list.component';
import { TaskListComponent } from './feature/task/task-list/task-list.component';

export const routes: Routes = [
    { path: '', component: ProjectListComponent },
    { path: 'project/:code', component: TaskListComponent },
    { path: '**', redirectTo: '' }
];
