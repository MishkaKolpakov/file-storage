import {Component, OnDestroy, OnInit} from '@angular/core';
import {UserCredentials} from '../userCredentials';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {UserService} from '../user.service';
import {User} from '../user';

@Component({
  selector: 'app-user-update-pass',
  templateUrl: './user-update-pass.component.html',
  styleUrls: ['./user-update-pass.component.css'],
  providers: [UserService]
})
export class UserUpdatePassComponent implements OnInit, OnDestroy {

  id: number;
  user: User;

  passForm: FormGroup;
  private sub: any;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private userService: UserService) {
  }

  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      this.id = params['id'];
    });

    this.userService.findById(this.id).subscribe(
      user => {
        this.id = user.id;
        this.user = user;
      }, error => {
        console.log(error);
      }
    );

    this.passForm = new FormGroup({
      password: new FormControl('', Validators.required),
      confirmPassword: new FormControl('', Validators.required)
    });

  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  onSubmit() {
    if (this.passForm.valid) {
      const credentials: UserCredentials = new UserCredentials(
        this.id,
        null,
        null,
        this.user.email,
        null,
        this.passForm.controls['password'].value);
      this.userService.updateUserPassword(credentials).subscribe();
      this.passForm.reset();
      this.router.navigate(['/']);
    }
  }

  redirectUserPage() {
    this.router.navigate(['/users']);
  }
}
