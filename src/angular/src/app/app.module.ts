import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {UserModule} from './user/user.module';
import {HttpModule} from '@angular/http';
import {AuthenticationModule} from './authentication/authentication.module';
import {HttpClientModule} from '@angular/common/http';
import {DetailsUploadComponent} from './upload/details-upload/details-upload.component';
import {FormUploadComponent} from './upload/form-upload/form-upload.component';
import {UploadFileService} from './upload/upload-file.service';
import {FileModule} from './file/file.module';
import { ChartsModule } from 'ng2-charts';
import {PieChartDemoComponent} from './charts/pie-chart.component';
import {LineChartDemoComponent} from './charts/line-chart.component';

import {BarChartDemoComponent} from './charts/bar-chart.component';
import { StatisticsComponent } from './statistics/statistics.component';
@NgModule({
  declarations: [
    AppComponent,
	  DetailsUploadComponent,
    FormUploadComponent,
    PieChartDemoComponent,
    LineChartDemoComponent,
    BarChartDemoComponent,
    StatisticsComponent],

  imports: [
    BrowserModule,
    AppRoutingModule,
    UserModule,
    FileModule,
    AuthenticationModule,
    HttpModule,
    HttpClientModule,
    AuthenticationModule,
    ChartsModule
  ],
  providers: [UploadFileService],
  bootstrap: [AppComponent]
})
export class AppModule { }
