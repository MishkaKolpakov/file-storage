import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {UserListComponent} from './user-list/user-list.component';
import {UserUpdateComponent} from './user-update/user-update.component';
import {UserAddComponent} from './user-add/user-add.component';
import {UserUpdatePassComponent} from './user-update-pass/user-update-pass.component';
import {UserUpdateRoleComponent} from './user-update-role/user-update-role.component';

const routes: Routes = [
  {path: 'users', component: UserListComponent},
  {path: 'users/add', component: UserAddComponent},
  {path: 'users/update/:id', component: UserUpdateComponent},
  {path: 'users/update/pass/:id', component: UserUpdatePassComponent},
  {path: 'users/update/role/:id', component: UserUpdateRoleComponent},
  {path: 'users/profile/:id', component: UserUpdatePassComponent},
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UserRoutingModule { }
