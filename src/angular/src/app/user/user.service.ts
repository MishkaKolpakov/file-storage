import { Injectable } from '@angular/core';
import { User } from './user';
import {Http, Headers, RequestOptions, Response, RequestMethod} from '@angular/http';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/throw';
import {UserCredentials} from './userCredentials';
import {AuthenticationService} from '../authentication/authentication.service';
import {UsersFilter} from './usersFilter';
import {HttpClient, HttpEventType, HttpHeaders, HttpParams, HttpRequest, HttpResponse} from '@angular/common/http';
import {Subject} from 'rxjs/Subject';

@Injectable()
export class UserService {

  //private apiUrl = 'http://localhost:8585/users';
  private headers = new Headers(
    { 'Content-Type': 'application/json; charset=utf-8',
      'X-AUTH': this.authService.getToken()});
  private httpHeaders = new HttpHeaders(
    { 'Content-Type': 'application/json; charset=utf-8',
      'X-AUTH': this.authService.getToken()});

  constructor(private http: Http,
              private httpClient: HttpClient,
              private authService: AuthenticationService) {
  }

  findById(id: number): Observable<User> {
    return this.http.get('/users/' + id, { headers: this.headers})
      .map((res: Response) => res.json())
      .catch((err) => {
        // Do messaging and error handling here
        return Observable.throw(err);
      });
  }

  addUserCred(userCred: UserCredentials): Observable<User> {
    return this.http.post('/users', userCred, { headers: this.headers})
      .map((res: Response) => res.json())
      .catch((err) => {
        // Do messaging and error handling here
        return Observable.throw(err);
      });
  }

  deleteUser(id: number): Observable<number> {
    return this.http.delete('/users/' + id, { headers: this.headers})
      .map(success => success.status)
      .catch((err) => {
        // Do messaging and error handling here
        return Observable.throw(err);
      });
  }

  activateUser(id: number): Observable<number> {
    console.log('activate user ');
    return this.http.patch('/users/' + id + '/activate', null, { headers: this.headers})
      .map(success => success.status)
      .catch((err) => {
        // Do messaging and error handling here
        return Observable.throw(err);
      });
  }

  updateUser(user: User): Observable<User> {
    return this.http.put('/users/' + user.id, user, { headers: this.headers})
      .map((res: Response) => res.json())
      .catch((err) => {
        // Do messaging and error handling here
        return Observable.throw(err);
      });
  }

  updateUserPassword(user: UserCredentials): Observable<User> {
    return this.http.put('/users/' + user.id + '/pass', user, { headers: this.headers})
      .map((res: Response) => res.json())
      .catch((err) => {
        // Do messaging and error handling here
        return Observable.throw(err);
      });
  }

  updateUserRole(user: User): Observable<User> {
    return this.http.put('/users/' + user.id + '/role', user, { headers: this.headers})
      .map((res: Response) => res.json())
      .catch((err) => {
        // Do messaging and error handling here
        return Observable.throw(err);
      });
  }

  findAllByFilter(usersFilter: UsersFilter): Observable<User[]> {
     /*const requestOptions = new RequestOptions({headers: this.headers});*/
     return this.http.get('/users?filter=' + encodeURIComponent(JSON.stringify(usersFilter)), {headers: this.headers})
     .map((res: Response) => res.json())
       .catch((err) => {
         // Do messaging and error handling here
         return Observable.throw(err);
       });

    /*console.log('inside findAllByFilter');
    console.log(this.headers);
    const subject = new Subject<User[]>();
    const params = new HttpParams()
      .set('filter', JSON.stringify(usersFilter));

    this.httpClient.get(this.apiUrl + '/list', {headers: this.httpHeaders, params: params}).subscribe(event => {
      if (event instanceof HttpResponse) {
        subject.complete();
      }
    });
    return subject.asObservable();*/
  }
}
