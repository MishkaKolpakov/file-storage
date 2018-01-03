import {Injectable, OnDestroy} from '@angular/core';
import {Http, RequestOptions, Headers, Response} from '@angular/http';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/throw';
import {UserCredentials} from '../user/userCredentials';
import {User} from '../user/user';
import { JwtHelper } from 'angular2-jwt'; // npm install angular2-jwt
import jwt_decode from 'jwt-decode'; // npm install jwt-decode

@Injectable()
export class AuthenticationService implements OnDestroy {

  //private apiUrl = 'http://localhost:8585';
  private headers;

  constructor(private http: Http) {
      this.headers = new Headers(
      {'Content-Type': 'application/json; charset=utf-8',
        'Access-Control-Request-Header': 'Content-Type, Authorization, X-AUTH'});
  }

  ngOnDestroy(): void {
    /*this.logout();*/
  }

  registerUser(userCred: UserCredentials): Observable<User> {
    return this.http.post('/user/register', userCred)
      .map((res: Response) => res.json())
      .catch((error: any) => Observable.throw(error.json().error || 'Server error'));
  }

  login(credentials: UserCredentials): Observable<User> {
    return this.http.post('/user/login', credentials, {headers: this.headers})
      .map((res: Response) => {
        const status = res.status;
        // console.log('status=' + status);
        const token = res.headers.get('X-AUTH');
        // console.log('token=' + token);
        if (token) {
          // console.log(jwt_decode(token));
          let body = res.json();
          sessionStorage.setItem('currentUser', JSON.stringify({username: credentials.email, token: token, id: body.id}));
          sessionStorage.setItem('id', body.id);
          // console.log("User Id " + sessionStorage.getItem('id'));
          // console.log("User Id " + body.id);

          return body;
        } else {
          console.debug('no token');
        }
      })
      .catch((err) => {
        // Do messaging and error handling here
        const respErr  = JSON.parse(err._body, (key, value) => {
          if(key == 'message') {
            console.error('custom error message: ' + value);
          }
        });
        return Observable.throw(err);
      });
  }

  getToken(): string {
    const currentUser = JSON.parse(sessionStorage.getItem('currentUser'));
    const token = currentUser && currentUser.token;
    localStorage.setItem('token', token);
    return token ? token : '';
  }

  getUsername(): string {
    const currentUser = JSON.parse(sessionStorage.getItem('currentUser'));
    const username = currentUser && currentUser.username;
    // console.log('username ' + username);
    return username ? username : '';
  }

  getUserId(): number {
    const currentUser = JSON.parse(sessionStorage.getItem('currentUser'));
    const userId = currentUser && currentUser.id;
    console.log('user id ' + userId);
    return userId ? userId : null;
  }

  getRole(): string {
    const encodedToken = this.getToken();
    const data = jwt_decode(encodedToken);
    const role = data['role'];
    return role;
  }

  isLoggedIn(): string {
    return this.getUsername();
  }

  logout(): void {
    sessionStorage.removeItem('currentUser');
    sessionStorage.removeItem('id');
  }
}
