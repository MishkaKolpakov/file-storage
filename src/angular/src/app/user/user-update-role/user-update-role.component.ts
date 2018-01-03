import {Component, OnDestroy, OnInit} from '@angular/core';
import {UserService} from '../user.service';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {User} from '../user';

@Component({
  selector: 'app-user-update-role',
  templateUrl: './user-update-role.component.html',
  styleUrls: ['./user-update-role.component.css'],
  providers: [UserService]
})
export class UserUpdateRoleComponent implements OnInit, OnDestroy {

  id: number;
  user: User;

  roleForm: FormGroup;
  private sub: any;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private userService: UserService) {
  }

  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      this.id = params['id'];
    });

    if (this.id) {
      this.userService.findById(this.id).subscribe(
        user => {
          this.id = user.id;
          this.user = user;
        }, error => {
          console.log(error);
        }
      );
    }

    this.roleForm = new FormGroup({
      role: new FormControl('', Validators.required)
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  onRoleSubmit() {
    if (this.roleForm.valid) {
      const user: User = new User(
        this.id,
        null,
        null,
        null,
        this.roleForm.controls['role'].value);
      this.userService.updateUserRole(user).subscribe();
      this.roleForm.reset();
      this.router.navigate(['/users']);
    }
  }

  redirectUserPage() {
    this.router.navigate(['/users']);
  }
}
