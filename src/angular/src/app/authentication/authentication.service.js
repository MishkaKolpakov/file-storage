"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require('@angular/core');
var http_1 = require('@angular/http');
require('rxjs/add/operator/map');
require('rxjs/add/operator/catch');
var Observable_1 = require('rxjs/Observable');
require('rxjs/add/observable/throw');
var angular2_jwt_1 = require('angular2-jwt'); // npm install angular2-jwt
var jwt_decode_1 = require('jwt-decode'); // npm install jwt-decode
var AuthenticationService = (function () {
    function AuthenticationService(http) {
        this.http = http;
        this.apiUrl = 'http://localhost:8585';
        this.jwtHelper = new angular2_jwt_1.JwtHelper();
        this.isUser = false;
        this.isLogged = false;
        this.isTechSupp = false;
        this.isAdmin = false;
        this.headers = new http_1.Headers({ 'Content-Type': 'application/json; charset=utf-8',
            'Access-Control-Request-Header': 'Content-Type, Authorization, X-AUTH' });
    }
    AuthenticationService.prototype.ngOnDestroy = function () {
        /*this.logout();*/
    };
    AuthenticationService.prototype.registerUser = function (userCred) {
        return this.http.post(this.apiUrl + '/user/register', userCred)
            .map(function (res) { return res.json(); })
            .catch(function (error) { return Observable_1.Observable.throw(error.json().error || 'Server error'); });
    };
    AuthenticationService.prototype.login = function (credentials) {
        var _this = this;
        return this.http.post(this.apiUrl + '/user/login', credentials, { headers: this.headers })
            .map(function (res) {
            var status = res.status;
            console.log('status=' + status);
            var token = res.headers.get('X-AUTH');
            console.log('token=' + token);
            if (token) {
                localStorage.setItem('currentUser', JSON.stringify({ username: credentials.email, token: token }));
                console.log(jwt_decode_1.default(token));
                _this.setRole();
                _this.isLogged = true;
                var body = res.json();
                sessionStorage.setItem('id', body.id);
                localStorage.setItem('id', body.id);
                console.log("User Id " + sessionStorage.getItem('id'));
                console.log("User Id " + body.id);
                return body;
            }
            else {
                console.debug('no token');
            }
        })
            .catch(function (err) {
            // Do messaging and error handling here
            var respErr = JSON.parse(err._body, function (key, value) {
                if (key == 'message') {
                    console.error('custom error message: ' + value);
                }
            });
            return Observable_1.Observable.throw(err);
        });
    };
    AuthenticationService.prototype.getToken = function () {
        var currentUser = JSON.parse(localStorage.getItem('currentUser'));
        var token = currentUser && currentUser.token;
        return token ? token : '';
    };
    AuthenticationService.prototype.getUsername = function () {
        var currentUser = JSON.parse(localStorage.getItem('currentUser'));
        var username = currentUser && currentUser.username;
        console.log('username ' + username);
        return username ? username : '';
    };
    AuthenticationService.prototype.getRole = function () {
        var encodedToken = this.getToken();
        var data = jwt_decode_1.default(encodedToken);
        var role = data['role'];
        return role;
    };
    AuthenticationService.prototype.isLoggedIn = function () {
        return this.getUsername();
    };
    AuthenticationService.prototype.setRole = function () {
        var encodedToken = this.getToken();
        var data = jwt_decode_1.default(encodedToken);
        var role = data['role'];
        if (role) {
            if (role == 'ADMIN') {
                this.isAdmin = true;
            }
            else if (role == 'TECH_SUPP') {
                this.isTechSupp = true;
            }
            else {
                this.isUser = true;
            }
        }
    };
    AuthenticationService.prototype.getUserByLogin = function (credentials) {
        return this.http.post(this.apiUrl + '/user/login', credentials)
            .map(function (res) { return res.json(); })
            .catch(function (error) { return Observable_1.Observable.throw(error.json().error || 'Error'); });
    };
    AuthenticationService.prototype.logout = function () {
        localStorage.removeItem('currentUser');
        localStorage.removeItem('id');
        this.isLogged = false;
        this.isUser = false;
        this.isAdmin = false;
        this.isTechSupp = false;
    };
    AuthenticationService = __decorate([
        // npm install jwt-decode
        core_1.Injectable()
    ], AuthenticationService);
    return AuthenticationService;
}());
exports.AuthenticationService = AuthenticationService;
