import { Task } from "./task.model";

export interface Project {
    id?: number;
    name: string;
    code?: string;
    tasks?: Task[];
}