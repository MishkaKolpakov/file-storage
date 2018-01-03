import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http'
import {HttpHeaders} from "@angular/common/http";
import {AuthenticationService} from "../authentication/authentication.service";

@Component({
  selector: 'pie-chart',
  templateUrl: './pie-chart.html'
})
export class PieChartDemoComponent implements OnInit {

  //private apiUrl = 'http://localhost:8585';
  private headers;

  constructor(private http: HttpClient, private authService: AuthenticationService){
    this.headers = new HttpHeaders(
      {'X-AUTH': this.authService.getToken()});
  }

  ngOnInit(): void {

    this.http.get('/stat/size/' + this.authService.getUserId(), { headers: this.headers }).subscribe(data => {
      sessionStorage.setItem('usedSpace', data.toString());
      let freeSpace = 1000000 - Number(data);
      // console.log("freeSpace " + freeSpace);
      sessionStorage.setItem('freeSpace', freeSpace.toString());
      // console.log("Used space " + Number(sessionStorage.getItem('usedSpace')));
      // console.log("freeSpace " + Number(sessionStorage.getItem('freeSpace')));
    });
  }

  // Pie
  public pieChartLabels:string[] = ['Used Space in CloudStore', 'Free Space'];
  public pieChartData:number[] = [Number(sessionStorage.getItem('usedSpace')), Number(sessionStorage.getItem('freeSpace'))];
  public pieChartType:string = 'pie';


  // events
  public chartClicked(e:any):void {
    console.log(e);
  }

  public chartHovered(e:any):void {
    console.log(e);
  }
}




