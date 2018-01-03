import {Component, OnDestroy, OnInit} from '@angular/core';
import {FileService} from '../file.service';
import {FileMetadata} from '../fileMetadata';
import {FormArray, FormControl, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {UserService} from '../../user/user.service';
import {User} from '../../user/user';
import {ORDER_ENUM, USER_FIELD_ENUM, USER_STATUS_ENUM, UsersFilter} from '../../user/usersFilter';
import {ShareData} from '../shareData';

@Component({
  selector: 'app-file-share',
  templateUrl: './file-share.component.html',
  styleUrls: ['./file-share.component.css'],
  providers: [FileService, UserService]
})
export class FileShareComponent implements OnInit, OnDestroy {

  file: FileMetadata;
  resourceId: number;
  users: User[];

  shareForm: FormGroup;
  private sub: any;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private userService: UserService,
              private fileService: FileService) {
  }

  ngOnInit() {
    this.sub = this.route.params.subscribe(
      params => {
        this.resourceId = params['fileId'];
      },
    );

    this.shareForm = new FormGroup({
      permission: new FormControl('', Validators.required),
      userIDs: new FormArray([]),
    });

    this.fileService.findFileMetadataById(this.resourceId).subscribe(
      fileMetadata => {
        this.file = fileMetadata;
      },
      err => {
        console.log(err);
      }
    );

    this.getAllUsersToShareFile();

  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  getAllUsersToShareFile() {
    const filter: UsersFilter = new UsersFilter(
      null,
      null,
      ORDER_ENUM.ASC,
      USER_FIELD_ENUM.id,
      null,
      USER_STATUS_ENUM.ACTIVE,
      null);
    this.userService.findAllByFilter(filter).subscribe(
      users => {
        this.users = users;
      },
      err => {
        console.log(err);
      }
    );
  }

  onSubmit() {
    if (this.shareForm.valid) {
      this.file.permission = this.shareForm.controls['permission'].value;
      const userIDs: number[] = this.shareForm.controls['userIDs'].value;

      const shareData: ShareData = new ShareData(this.file, userIDs);
      console.log(JSON.stringify(shareData));
      this.fileService.shareFile(shareData).subscribe();

      this.shareForm.reset();
      this.router.navigate(['/files']);

    }
  }

  onCheckChange(event) {
    const formArray: FormArray = this.shareForm.get('userIDs') as FormArray;

    /* Selected */
    if (event.target.checked) {
      // Add a new control in the arrayForm
      formArray.push(new FormControl(event.target.value));
    } else {      /* unselected */
      // find the unselected element
      let i: number = 0;

      formArray.controls.forEach((ctrl: FormControl) => {
        if(ctrl.value == event.target.value) {
          // Remove the unselected element from the arrayForm
          formArray.removeAt(i);
          return;
        }

        i++;
      });
    }
  }

  redirectUserPage() {
    this.router.navigate(['/files']);
  }
}
