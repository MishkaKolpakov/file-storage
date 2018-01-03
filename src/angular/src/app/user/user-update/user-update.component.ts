import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {UserService} from '../user.service';
import {User} from '../user';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-user-update',
  templateUrl: './user-update.component.html',
  styleUrls: ['./user-update.component.css'],
  providers: [UserService]
})
export class UserUpdateComponent implements OnInit, OnDestroy {

  id: number;
  user: User;

  userForm: FormGroup;
  private sub: any;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private userService: UserService) {
  }

  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      this.id = params['id'];
    });

    this.userForm = new FormGroup({
      firstname: new FormControl('', Validators.required),
      lastname: new FormControl('', Validators.required),
      email: new FormControl('', [
        Validators.required,
        Validators.pattern('[^ @]*@[^ @]*')
      ]),
      role: new FormControl('', Validators.required)
    });

    this.userService.findById(this.id).subscribe(
      user => {
        this.userForm.patchValue({
          firstname: user.firstname,
          lastname: user.lastname,
          email: user.email,
          role: user.role,
        });
      }, error => {
        console.log(error);
      }
    );

  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  onSubmit() {
    if (this.userForm.valid) {
      const user: User = new User(
        this.id,
        this.userForm.controls['firstname'].value,
        this.userForm.controls['lastname'].value,
        this.userForm.controls['email'].value,
        null);
      this.userService.updateUser(user).subscribe();

      this.userForm.reset();
      this.router.navigate(['/users']);

    }
  }

  redirectUserPage() {
    this.router.navigate(['/users']);
  }
}
