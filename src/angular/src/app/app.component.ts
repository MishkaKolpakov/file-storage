import { Component } from '@angular/core';
import {AuthenticationService} from './authentication/authentication.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  providers: [AuthenticationService]
})
export class AppComponent {
  title = 'app';

  constructor(private router: Router,
    private authenticationService: AuthenticationService) {
  }

  getUsername(): string {
    return this.authenticationService.getUsername();
  }

  isLoggedIn(): boolean {
    if (this.authenticationService.getUsername()) {
      return true;
    } else {
      return false;
    }
  }

  getId(): number {
    return this.authenticationService.getUserId();
  }

  isAdmin(): boolean {
    const role = this.authenticationService.getRole();
    // console.log('role is ' + role);
    if (role == 'ADMIN') {
      return true;
    }
    return false;
  }

  isUser(): boolean {
    const role = this.authenticationService.getRole();
    // console.log('role is ' + role);
    if (role == 'USER') {
      return true;
    }
    return false;
  }


  logout(): void {
    this.authenticationService.logout();
    this.router.navigate(['/']);
  }
}
