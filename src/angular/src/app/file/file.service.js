"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require('@angular/core');
var http_1 = require('@angular/common/http');
var http_2 = require('@angular/http');
var Observable_1 = require('rxjs/Observable');
require('rxjs/add/operator/map');
require('rxjs/add/operator/catch');
require('rxjs/add/observable/throw');
var FileService = (function () {
    function FileService(http, httpClient, authService) {
        this.http = http;
        this.httpClient = httpClient;
        this.authService = authService;
        this.apiUrl = 'http://localhost:8585';
        this.headers = new http_2.Headers({ 'Content-Type': 'application/json; charset=utf-8',
            'X-AUTH': this.authService.getToken() });
        this.httpHeaders = new http_1.HttpHeaders({ 'Content-Type': 'application/json; charset=utf-8',
            'X-AUTH': this.authService.getToken() });
    }
    FileService.prototype.upload = function (fileMetadata, file) {
        return null;
    };
    FileService.prototype.findAllPerPage = function (page, limit) {
        var uploadHttpHeaders = new http_1.HttpHeaders({ 'X-AUTH': this.authService.getToken() });
        var id = localStorage.getItem('id');
        return this.httpClient.get(this.apiUrl + '/user/' + id + '/files', { headers: uploadHttpHeaders })
            .catch(function (err) {
            // Do messaging and error handling here
            return Observable_1.Observable.throw(err);
        });
    };
    FileService.prototype.shareFile = function (shareData) {
        return this.http.put(this.apiUrl + '/user/files/share', shareData, { headers: this.headers })
            .map(function (success) { return success.status; })
            .catch(function (err) {
            // Do messaging and error handling here
            return Observable_1.Observable.throw(err);
        });
    };
    FileService.prototype.findFileMetadataById = function (fileId) {
        var uploadHttpHeaders = new http_1.HttpHeaders({ 'X-AUTH': this.authService.getToken() });
        var id = localStorage.getItem('id');
        return this.httpClient.get(this.apiUrl + '/user/files/' + fileId + '/getFileMetadata', { headers: uploadHttpHeaders })
            .catch(function (err) {
            // Do messaging and error handling here
            return Observable_1.Observable.throw(err);
        });
    };
    FileService.prototype.deleteFile = function (fileId) {
        console.log('inside of delete');
        var id = localStorage.getItem('id');
        return this.http.delete(this.apiUrl + '/user/' + id + '/files/' + fileId, { headers: this.headers })
            .map(function (success) { return success.status; })
            .catch(function (err) {
            // Do messaging and error handling here
            return Observable_1.Observable.throw(err);
        });
    };
    FileService.prototype.downloadFile = function (uuid) {
        var downloadHttpHeaders = new http_1.HttpHeaders({ 'X-AUTH': this.authService.getToken() });
        var id = localStorage.getItem('id');
        return this.httpClient.get(this.apiUrl + '/files/' + uuid, { headers: downloadHttpHeaders })
            .catch(function (err) {
            // Do messaging and error handling here
            return Observable_1.Observable.throw(err);
        });
    };
    FileService = __decorate([
        core_1.Injectable()
    ], FileService);
    return FileService;
}());
exports.FileService = FileService;
