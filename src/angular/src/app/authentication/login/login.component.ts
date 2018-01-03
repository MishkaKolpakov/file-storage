import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {UserCredentials} from '../../user/userCredentials';
import {AuthenticationService} from '../authentication.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  providers: [AuthenticationService]
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup;

  constructor(private router: Router,
              private authenticationService: AuthenticationService) {
  }

  ngOnInit() {

    this.loginForm = new FormGroup({
      email: new FormControl('', [
        Validators.required,
        Validators.pattern('[^ @]*@[^ @]*')
      ]),
      password: new FormControl('', Validators.required)
    });

  }

  onLoginSubmit() {
    if (this.loginForm.valid) {
      const credentials: UserCredentials = new UserCredentials(
        null,
        null,
        null,
        this.loginForm.controls['email'].value,
        null,
        this.loginForm.controls['password'].value);
      this.authenticationService.login(credentials).subscribe();

      this.loginForm.reset();

      this.router.navigate(['/']);

    }
  }

  redirectUserPage() {
    this.router.navigate(['/']);
  }

}
