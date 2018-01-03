import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {FormUploadComponent} from "./upload/form-upload/form-upload.component";

const routes: Routes = [
  {path: '', component: FormUploadComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
