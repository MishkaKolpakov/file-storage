import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {AuthenticationService} from "../authentication/authentication.service";

@Component({
  selector: 'app-statistics',
  templateUrl: './statistics.component.html',
  styleUrls: ['./statistics.component.css']
})
export class StatisticsComponent implements OnInit {

  constructor(private authService: AuthenticationService,
              private router: Router,
              private route: ActivatedRoute) {
    const userId: number = this.authService.getUserId();
  }

  ngOnInit() {
  }

}
