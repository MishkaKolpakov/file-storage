import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {FileListComponent} from './file-list/file-list.component';
import {FileUploadComponent} from './file-upload/file-upload.component';
import {FileShareComponent} from './file-share/file-share.component';
import { StatisticsComponent } from '../statistics/statistics.component';


const routes: Routes = [
  {path: 'files', component: FileListComponent},
  {path: 'files/list', component: FileListComponent},
  {path: 'files/shared', component: FileListComponent},
  {path: 'files/upload', component: FileUploadComponent},
  {path: 'files/share/:fileId', component: FileShareComponent},
  {path: 'statistics' , component: StatisticsComponent}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class FileRoutingModule { }
