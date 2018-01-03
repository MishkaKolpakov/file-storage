import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UserRoutingModule } from './user-routing.module';
import { UserListComponent } from './user-list/user-list.component';
import { UserUpdateComponent } from './user-update/user-update.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { UserAddComponent } from './user-add/user-add.component';
import { UserUpdatePassComponent } from './user-update-pass/user-update-pass.component';
import { UserUpdateRoleComponent } from './user-update-role/user-update-role.component';

@NgModule({
  imports: [
    CommonModule,
    UserRoutingModule,
    FormsModule,
    ReactiveFormsModule
  ],
  declarations: [UserListComponent, UserUpdateComponent, UserAddComponent, UserUpdatePassComponent, UserUpdateRoleComponent]
})
export class UserModule { }
