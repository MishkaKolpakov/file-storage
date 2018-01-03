import { Component, OnInit } from '@angular/core';
import {User} from '../user';
import {UserService} from '../user.service';
import {Router} from '@angular/router';
import {UsersFilter, ORDER_ENUM, USER_STATUS_ENUM, USER_FIELD_ENUM, PASSWORD_STATUS_ENUM, ROLE_ENUM} from '../usersFilter';
import {FormControl, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css'],
  providers: [UserService]
})
export class UserListComponent implements OnInit {

  users: User[];
  filterForm: FormGroup;
  deleted: boolean;

  constructor(private router: Router,
              private userService: UserService) {
    this.deleted = false;
    this.getAllUsersByFilter();
  }

  ngOnInit() {
    this.filterForm = new FormGroup({
      order: new FormControl(''),
      sortField: new FormControl(''),
      page: new FormControl(''),
      limit: new FormControl(''),
      role: new FormControl(''),
      userStatus: new FormControl('', Validators.required),
      passwordStatus: new FormControl('')
    });
    this.deleted = false;

  }

  getAllUsersByFilter() {
    const filter: UsersFilter = new UsersFilter(
      0,
      10,
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

   onFilter() {
    if (this.filterForm.controls['userStatus'].value == 'DELETED') {
      console.log('deleted is true');
      this.deleted = true;
    } else {
      console.log('deleted is false');
      this.deleted = false;
    }

   const page = this.filterForm.controls['page'].value;
   const limit = this.filterForm.controls['limit'].value;
   const offset = limit * (page - 1);

   const filter: UsersFilter = new UsersFilter(
   offset,
   this.filterForm.controls['limit'].value,
   this.filterForm.controls['order'].value,
   this.filterForm.controls['sortField'].value,
   this.filterForm.controls['role'].value,
   this.filterForm.controls['userStatus'].value,
   this.filterForm.controls['passwordStatus'].value);
   this.userService.findAllByFilter(filter).subscribe(
   users => {
   this.users = users;
   },
   err => {
   console.log(err);
   }
   );
   }

  redirectEditUserPage(user: User) {
    if (user) {
      this.router.navigate(['/users/update', user.id]);
    }
  }

  redirectEditRolePage(user: User) {
    if (user) {
      this.router.navigate(['/users/update/role', user.id]);
    }
  }

  redirectEditPassPage(user: User) {
    if (user) {
      this.router.navigate(['/users/update/pass', user.id]);
    }
  }

  deleteUser(user: User) {
    if (user) {
      this.userService.deleteUser(user.id).subscribe(
        res => {
          this.getAllUsersByFilter();
          this.router.navigate(['/users']);
          console.log('done');
        }
      );
    }
  }

  activateUser(user: User) {
    if (user) {
      this.userService.activateUser(user.id).subscribe(
        res => {
          this.getAllUsersByFilter();
          this.router.navigate(['/users']);
          console.log('done');
        }
      );
    }
  }

}
