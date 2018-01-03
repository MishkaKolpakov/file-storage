import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthenticationService} from '../authentication.service';
import {UserCredentials} from '../../user/userCredentials';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
  providers: [AuthenticationService]
})
export class RegisterComponent implements OnInit {

  registerForm: FormGroup;

  constructor(private router: Router,
              private authenticationService: AuthenticationService) {
  }

  ngOnInit() {

    this.registerForm = new FormGroup({
      firstname: new FormControl('', Validators.required),
      lastname: new FormControl('', Validators.required),
      email: new FormControl('', [
        Validators.required,
        Validators.pattern('[^ @]*@[^ @]*')
      ]),
      password: new FormControl('', Validators.required)
    });

  }

  onRegisterSubmit() {
    if (this.registerForm.valid) {
      const credentials: UserCredentials = new UserCredentials(
        null,
        this.registerForm.controls['firstname'].value,
        this.registerForm.controls['lastname'].value,
        this.registerForm.controls['email'].value,
        'USER',
        this.registerForm.controls['password'].value);
      this.authenticationService.registerUser(credentials).subscribe();

      this.registerForm.reset();
      this.router.navigate(['/login']);

    }
  }

}
