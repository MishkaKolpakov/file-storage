import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FileRoutingModule } from './file-routing.module';
import { FileShareComponent } from './file-share/file-share.component';
import { FileListComponent } from './file-list/file-list.component';
import { FileUploadComponent } from './file-upload/file-upload.component';
import { FileDownloadComponent } from './file-download/file-download.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

@NgModule({
  imports: [
    CommonModule,
    FileRoutingModule,
    FormsModule,
    ReactiveFormsModule
  ],
  declarations: [FileShareComponent, FileListComponent, FileUploadComponent, FileDownloadComponent]
})
export class FileModule { }
