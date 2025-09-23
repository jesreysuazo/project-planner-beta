export interface Task {
    id?: number;
    name: string;
    projectCode: string;
    duration?: number;
    status: 'NOT_STARTED' | 'IN_PROGRESS' | 'DONE';
    startDate: string;
    endDate: string;
    dependencies? : Task[];
}