import {Component, OnDestroy, OnInit} from '@angular/core';
import {UserService} from '../user.service';
import {UserCredentials} from '../userCredentials';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-user-add',
  templateUrl: './user-add.component.html',
  styleUrls: ['./user-add.component.css'],
  providers: [UserService]
})
export class UserAddComponent implements OnInit {

  newUserForm: FormGroup;

  constructor(private router: Router,
              private userService: UserService) {
  }

  ngOnInit() {

    this.newUserForm = new FormGroup({
      firstname: new FormControl('', Validators.required),
      lastname: new FormControl('', Validators.required),
      email: new FormControl('', [
        Validators.required,
        Validators.pattern('[^ @]*@[^ @]*')
      ]),
      role: new FormControl('', Validators.required),
      password: new FormControl('', Validators.required)
    });

  }

  onSubmit() {
    if (this.newUserForm.valid) {
      const userCredentials: UserCredentials = new UserCredentials(null,
        this.newUserForm.controls['firstname'].value,
        this.newUserForm.controls['lastname'].value,
        this.newUserForm.controls['email'].value,
        this.newUserForm.controls['role'].value,
        this.newUserForm.controls['password'].value);
      this.userService.addUserCred(userCredentials).subscribe();

      this.newUserForm.reset();
      this.router.navigate(['/users']);

    }
  }

  redirectUserPage() {
    this.router.navigate(['/users']);
  }

}
