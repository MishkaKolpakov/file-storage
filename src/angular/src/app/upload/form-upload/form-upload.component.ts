import { Component, OnInit } from '@angular/core';
import {HttpResponse, HttpEventType} from '@angular/common/http';
import { UploadFileService } from '../upload-file.service';
import {AuthenticationService} from "../../authentication/authentication.service";
import * as $ from 'jquery';
import 'eonasdan-bootstrap-datetimepicker';

window["$"] = $;
let response = $("#response");

@Component({
  selector: 'form-upload',
  templateUrl: './form-upload.component.html',
  styleUrls: ['./form-upload.component.css']
})
export class FormUploadComponent implements OnInit {

  selectedFiles: FileList;
  currentFileUpload: File;
  progress: { percentage: number } = { percentage: 0 };

  expirationTime: string;

  constructor(private uploadService: UploadFileService,
              private authService: AuthenticationService) { }

  ngOnInit() {

  }

  selectFile(event) {
    this.selectedFiles = event.target.files;
  }

  upload(expirationTime) {
    this.progress.percentage = 0;

    this.expirationTime = expirationTime;
    console.log(this.expirationTime);
    this.currentFileUpload = this.selectedFiles.item(0);

    this.uploadService.pushFileToStorage(this.currentFileUpload, this.expirationTime)
      .subscribe(res => {
        console.log('File is completely uploaded!');
    });

    this.selectedFiles = undefined
  }

  isLoggedIn(): boolean {
    if (this.authService.getUsername()) {
      return true;
    } else {
      return false;
    }
  }

  downloadFile(uuid: string) {
      this.uploadService.downloadFileByUUID(uuid).subscribe(
        blob => {
          const downloadUrl= URL.createObjectURL(blob);
          window.open(downloadUrl);
        }
      );
  }
}

$(function () {
  (<any>$('#datetimepicker1') ).datetimepicker({
    icons: {
      time: 'fa fa-clock-o',
      date: "fa fa-calendar",
      up: "fa fa-arrow-up",
      down: "fa fa-arrow-down",
      previous: 'fa fa-arrow-left',
      next: 'fa fa-arrow-right'
    },
    format: 'YYYY-MM-DDTkk:mm:ss.SSSZ',
    minDate: new Date()
  })
});
