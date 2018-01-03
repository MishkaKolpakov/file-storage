import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Http, Headers, Response, RequestOptions, ResponseContentType} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import {AuthenticationService} from '../authentication/authentication.service';
import {FileMetadata} from './fileMetadata';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';
import {ShareData} from './shareData';
import {FilesFilter} from './filesFilter';


@Injectable()
export class FileService {

  //private apiUrl = 'http://localhost:8585';
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

  upload(fileMetadata: FileMetadata, file: FormData): Observable<FileMetadata> {
    return null;
  }

  findAllByFilter(filesFilter: FilesFilter): Observable<FileMetadata[]> {
    return this.http.get('/user/files?filter=' + encodeURIComponent(JSON.stringify(filesFilter)), {headers: this.headers})
      .map((res: Response) => res.json())
      .catch((err) => {
        // Do messaging and error handling here
        return Observable.throw(err);
      });
  }

  shareFile(shareData: ShareData): Observable<number> {
    return this.http.put('/user/files/share', shareData, { headers: this.headers})
      .map(success => success.status)
      .catch((err) => {
        // Do messaging and error handling here
        return Observable.throw(err);
      });
  }

  findFileMetadataById(fileId: number): Observable<FileMetadata> {
    const uploadHttpHeaders = new HttpHeaders(
      { 'X-AUTH': this.authService.getToken()});

    return this.httpClient.get<FileMetadata>('/user/files/' + fileId + '/getFileMetadata', { headers: uploadHttpHeaders})
      .catch((err) => {
        // Do messaging and error handling here
        return Observable.throw(err);
      });
  }

  deleteFile(fileId: number): Observable<number> {
    console.log('inside of delete');
    const id = this.authService.getUserId();
    return this.http.delete('/user/' + id + '/files/' + fileId, { headers: this.headers})
      .map(success => success.status)
      .catch((err) => {
        // Do messaging and error handling here
        return Observable.throw(err);
      });
  }

  downloadFile(uuid: string, mime: string): Observable<any> {
    const downloadHttpHeaders = new Headers(
      { 'X-AUTH': this.authService.getToken()});
    const options = new RequestOptions({ headers: downloadHttpHeaders});
    options.responseType = ResponseContentType.Blob;

    return this.http.get('/files/' + uuid, options)
      .map(response => {
        if (response.status == 400) {
          console.log('status 400');
          return "FAILURE";
        } else if (response.status == 200) {
          console.log('status 200');
          var blob = new Blob([response.blob()], { type: mime });
          console.log('returning blob');
          return blob;
        }
      })
      .catch((err) => {
        // Do messaging and error handling here
        return Observable.throw(err);
      });
  }
}
