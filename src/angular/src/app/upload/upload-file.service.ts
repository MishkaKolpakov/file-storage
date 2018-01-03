import {Injectable} from '@angular/core';
import {HttpClient, HttpEvent} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {Headers, Http, RequestOptions, Response, ResponseContentType} from "@angular/http";
import {AuthenticationService} from "../authentication/authentication.service";
import {HttpHeaders} from "@angular/common/http";
import { saveAs } from 'file-saver';
import * as $ from 'jquery';

window["$"] = $;
let response = $("#response");

@Injectable()
export class UploadFileService {

  private apiUrl = "/user/files/uploadFile";
  private headers = new HttpHeaders(
    { 'Web': 'web',
      'X-AUTH': this.authService.getToken()});

  constructor(private http: HttpClient,
              private authService: AuthenticationService,
              private oldHttp: Http) {}

  pushFileToStorage(file: File, expirationTime: string): Observable<HttpEvent<{}>> {
    let data: FormData = new FormData();

    console.log("User ID IS " + localStorage.getItem('id'));
    data.append('file', file);
    data.append("userId", String(this.authService.getUserId()));
    data.append("ownerId", String(this.authService.getUserId()));
    data.append("fileSize", file.size.toString());
    data.append("mime", file.type);
    data.append("fileName", file.name);
    // data.append("permission",  "ALL_USERS");
    data.append("expirationTime",  expirationTime);

    // const req = new HttpRequest('POST', this.apiUrl, data, {
    //   reportProgress: true,
    //   responseType: 'json'
    // });
    //
    // return this.http.request(req);

    console.log("Token " + this.authService.getToken());
    return this.oldHttp.post(this.apiUrl, data, { headers: new Headers({ 'Web': 'web', 'X-AUTH': this.authService.getToken()})})
      .map((res: Response) => {
        let body = res.json();

        this.http.get('/stat/size/' + String(this.authService.getUserId()), { headers: new HttpHeaders(
          { 'Content-Type': 'application/json; charset=utf-8',
            'X-AUTH': this.authService.getToken()}) }).subscribe(data => {
          console.log("Data space " + Number(data));
          sessionStorage.setItem('usedSpace', data.toString());
          console.log("From SS " + sessionStorage.getItem('usedSpace'));
          let freeSpace = 1000000 - Number(sessionStorage.getItem('usedSpace'));
          console.log("freeSpace " + freeSpace);
          sessionStorage.setItem('freeSpace', freeSpace.toString());
          console.log("Used space " + Number(sessionStorage.getItem('usedSpace')));
          console.log("freeSpace " + Number(sessionStorage.getItem('freeSpace')));

          localStorage.setItem("uuid", body.fileUUID);
          localStorage.setItem("res", res.statusText);

          console.log("uuid " + localStorage.getItem("uuid"));
          console.log("res " + localStorage.getItem("res"));

          // location.reload();
          showResponse(localStorage.getItem("res"), localStorage.getItem("uuid"));
        });
      })
      .catch((err) => {
        // Do messaging and error handling here
        return Observable.throw(err);
      });
  }


  downloadFileByUUID(uuid: string): Observable<any> {
    const downloadHttpHeaders = new Headers(
      { 'X-AUTH': this.authService.getToken()});
    const options = new RequestOptions({ headers: downloadHttpHeaders});
    options.responseType = ResponseContentType.Blob;

    return this.oldHttp.get('/files/' + uuid, options)
      .map(response => {
        if (response.status == 400) {
          console.log('status 400');
          return "FAILURE";
        } else if (response.status == 200) {
          console.log('status 200');
          const contentDispositionHeader: string = response.headers.get('Content-Disposition');
          const contentTypeHeader: string = response.headers.get('Content-Type');
          const parts: string[] = contentDispositionHeader.split(';');
          const filename = parts[1].split('=')[1];
          const blob = new Blob([response.blob()], { type: contentTypeHeader });
          saveAs(blob, filename);
          console.log('returning blob');
          return blob;
        }
      })
      .catch((err) => {
        return Observable.throw(err);
      });
  }
}

function showResponse(statusCode, message) {
  console.log("status code " + statusCode);
  console.log("message " + message);

  // document.getElementById("response").innerHTML = "Status code: " + statusCode + "\n-------------------------\n" + message;

  $("#response")
    .empty()
    .text("status code: " + statusCode + "\n-------------------------\n" + message);
}
