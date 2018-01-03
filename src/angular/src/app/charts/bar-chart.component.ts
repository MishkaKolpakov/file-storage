/**
 * Created by Miha on 28.12.2017.
 */
import { HttpClient } from '@angular/common/http'
import {HttpHeaders} from "@angular/common/http";
import {AuthenticationService} from "../authentication/authentication.service";
import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'bar-chart-demo',
  templateUrl: './bar-chart-demo.html'
})
export class BarChartDemoComponent implements OnInit{

  private headers;

  constructor(private http: HttpClient, private authService: AuthenticationService){
    this.headers = new HttpHeaders(
      {'X-AUTH': this.authService.getToken()});
  }

  ngOnInit(): void {

    this.http.get('/updownrate/' + this.authService.getUserId(), { headers: this.headers }).subscribe(data => {
      let res = data;
      sessionStorage.setItem('upload', res['upload']);
      console.log("Session upload " + sessionStorage.getItem('upload'));
      sessionStorage.setItem('download', res['download']);
      console.log("Session download " + sessionStorage.getItem('download'));

    });
  }

  public barChartOptions:any = {
    scaleShowVerticalLines: false,
    responsive: true
  };
  public barChartLabels:string[] = ['December'];
  public barChartType:string = 'bar';
  public barChartLegend:boolean = true;

  public barChartData:any[] = [
    {data: [sessionStorage.getItem('upload')], label: 'Uploads'},
    {data: [sessionStorage.getItem('download')], label: 'Downloads'}
  ];

  // events
  public chartClicked(e:any):void {
    console.log(e);
  }

  public chartHovered(e:any):void {
    console.log(e);
  }

  public randomize():void {
    // Only Change 3 values
    let data = [
      Math.round(Math.random() * 100),
      59,
      80,
      (Math.random() * 100),
      56,
      (Math.random() * 100),
      40];
    let clone = JSON.parse(JSON.stringify(this.barChartData));
    clone[0].data = data;
    this.barChartData = clone;
    /**
     * (My guess), for Angular to recognize the change in the dataset
     * it has to change the dataset variable directly,
     * so one way around it, is to clone the data, change it and then
     * assign it;
     */
  }
}

