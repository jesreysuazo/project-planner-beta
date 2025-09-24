import { inject } from '@angular/core';

import {
    HttpErrorResponse,
    HttpHandlerFn,
    HttpRequest,
} from "@angular/common/http";
import { catchError, throwError } from "rxjs";
import { Router } from '@angular/router';

export function errorInterceptor(req:HttpRequest<unknown>, next:HttpHandlerFn){
    const router = inject(Router);
    const caller = req.headers.get('X-caller');
    
    return next(req).pipe(
        catchError((error:HttpErrorResponse)=>{
            alert('Error: ' + error.error.message);
            // router.navigate(['/']);

 
            return throwError(() => error)
        })
    )
}