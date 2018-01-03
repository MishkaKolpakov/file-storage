import { Component, OnInit } from '@angular/core';
import {FileMetadata} from '../fileMetadata';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {FileService} from '../file.service';
import {FilesFilter, ORDER_ENUM, METADATA_FIELD_ENUM, FILE_TYPE} from "../filesFilter";
import {AuthenticationService} from '../../authentication/authentication.service';

@Component({
  selector: 'app-file-list',
  templateUrl: './file-list.component.html',
  styleUrls: ['./file-list.component.css'],
  providers: [FileService, AuthenticationService]
})
export class FileListComponent implements OnInit {

  fileMetadataList: FileMetadata[];
  filterForm: FormGroup;
  isOwn: boolean;
  filter: FilesFilter;

  constructor(private authService: AuthenticationService,
              private router: Router,
              private route: ActivatedRoute,
              private fileService: FileService) {
    const userId: number = this.authService.getUserId();
    this.filter = new FilesFilter(0, 10,
      ORDER_ENUM.DESC,
      METADATA_FIELD_ENUM.resourceId,
      FILE_TYPE.OWN,
      userId,
      null);

    if(router.url.endsWith('shared')) {
      console.log('shared files');
      this.isOwn = false;
      this.filter.fileType = FILE_TYPE.SHARED;
      this.getByFilter(this.filter);
    } else {
      console.log('my files');
      this.isOwn = true;
      this.getByFilter(this.filter);
    }
  }

  ngOnInit() {
    this.filterForm = new FormGroup({
      page: new FormControl(''),
      limit: new FormControl(''),
      order: new FormControl(''),
      sortField: new FormControl(''),
      permission: new FormControl('')
    });
  }

  getByFilter(filter: FilesFilter) {
    this.fileService.findAllByFilter(filter).subscribe(
      fileMetadatas => {
        this.fileMetadataList = fileMetadatas;
      },
      err => {
        console.log(err);
      }
    );
  }

  onFilter() {
    const page = this.filterForm.controls['page'].value;
    const limit = this.filterForm.controls['limit'].value;
    const offset = limit * (page - 1);
    const userId: number = this.authService.getUserId();

    const filter = new FilesFilter(offset, limit,
      this.filterForm.controls['order'].value,
      this.filterForm.controls['sortField'].value,
      this.filter.fileType,
      userId,
      this.filterForm.controls['permission'].value);

    this.fileService.findAllByFilter(filter).subscribe(
      fileMetadatas => {
        this.fileMetadataList = fileMetadatas;
      },
      err => {
        console.log(err);
      }
    );
  }

  redirectShareFilePage(file: FileMetadata) {
    if (file) {
      this.router.navigate(['/files/share', file.resourceId]);
    }
  }

  deleteFile(file: FileMetadata) {
    if (file) {
      this.fileService.deleteFile(file.resourceId).subscribe(
        res => {
          this.getByFilter(this.filter);
          this.router.navigate(['/files']);
          console.log('done');
        }
      );
    }
  }

  downloadFile(file: FileMetadata) {
    if (file) {
      this.fileService.downloadFile(file.fileUUID, file.mime).subscribe(
        blob => {
          var downloadUrl= URL.createObjectURL(blob);
          window.open(downloadUrl);
        }
      );
    }
  }
}
