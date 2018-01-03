$(function () {
    // VARIABLES =============================================================
    var TOKEN_KEY = "jwtToken";
    var $notLoggedIn = $("#notLoggedIn");
    var $loggedIn = $("#loggedIn").hide();
    var $loggedInBody = $("#loggedInBody");
    var $response = $("#response");
    var $resp = $("#resp").hide();
    var $login = $("#login");
    var $userInfo = $("#userInfo").hide();
    var $updown = $("#updown").hide();
    var $logoutPanel = $("#logoutPanel").hide();
    var $loginPanel = $("#loginPanel");
    $("#users").hide();

    var $loader = $('#loader').hide();


    if (performance.navigation.type == 1) {
        console.log(userId);
        if (localStorage.getItem("userId") === 0) {
            $logoutPanel.show();
            $loginPanel.hide();
            $updown.show();
            $resp.show();
            $userInfo.show();
        }
    } else {

        console.info( "This page is not reloaded");
    }

    // var encrypted;
    var userId;

    // FUNCTIONS =============================================================
    function getJwtToken() {
        return localStorage.getItem(TOKEN_KEY);
    }

    function setJwtToken(token) {
        localStorage.setItem(TOKEN_KEY, token);
    }

    function removeJwtToken() {
        localStorage.removeItem(TOKEN_KEY);
    }

    function doLogin(loginData) {
        $.ajax({
            url: "/user/login",
            type: "POST",
            data: JSON.stringify(loginData),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function (data, textStatus, jqXHR) {
                setJwtToken(jqXHR.getResponseHeader("X-AUTH"));
                $login.hide();
                $notLoggedIn.hide();
                userId = data.id;
                localStorage.setItem("userId", userId);
                console.log(userId);
                showTokenInformation();
                showUserInformation();
                $logoutPanel.show();
                $loginPanel.hide();
                $updown.show();
                $resp.show();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(jqXHR.status);
                $('#loginErrorModal')
                    .modal("show")
                    .find(".modal-body")
                    .empty()
                    .html("<p>Validation exception:<br>" + jqXHR.responseJSON.message + "</p>");
            }
        });
    }

    function doLogout() {
        removeJwtToken();
        $login.show();
        $userInfo
            .hide()
            .find("#userInfoBody").empty();
        $loggedIn.hide();
        $loggedInBody.empty();
        $notLoggedIn.show();
        $updown.hide();
        $loginPanel.show();
        $logoutPanel.hide();
        $response.empty();
        $resp.hide();
        localStorage.removeItem("userId");
    }

    function createAuthorizationTokenHeader() {
        var token = getJwtToken();

        if (token) {
            return {"X-AUTH": "" + token};
        } else {
            return {};
        }
    }

    function showUserInformation() {
        $.ajax({
            url: "/users/" + localStorage.getItem("userId"),
            type: "GET",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            headers: createAuthorizationTokenHeader(),
            success: function (data, textStatus, jqXHR) {
                var $userInfoBody = $userInfo.find("#userInfoBody");

                $userInfoBody.append($("<div>").text("Username: " + data.firstname + " " + data.lastname));
                $userInfoBody.append($("<div>").text("Email: " + data.email));
                $userInfoBody.append($("<div>").text("UserId: " + data.id));

                userId = data.id;

                var $authorities = $("<div>").text("Authority: ");
                $authorities.append(data.role);

                $userInfoBody.append($authorities);
                $logoutPanel.show();
                $loginPanel.hide();
                $updown.show();
                $resp.show();
                $userInfo.show();
            }
        });
    }

    function showTokenInformation() {
        var jwtToken = getJwtToken();
        var decodedToken = jwt_decode(jwtToken);

        $loggedInBody.append($("<h4>").text("Token"));
        $loggedInBody.append($("<div>").text(jwtToken).css("word-break", "break-all"));
        $loggedInBody.append($("<h4>").text("Token claims"));

        var $table = $("<table>")
            .addClass("table table-striped");
        appendKeyValue($table, "email in token", decodedToken.sub);

        $loggedInBody.append($table);

        $loggedIn.show();
    }

    function appendKeyValue($table, key, value) {
        var $row = $("<tr>")
            .append($("<td>").text(key))
            .append($("<td>").text(value));
        $table.append($row);
    }

    function showResponse(statusCode, message) {
        $response
            .empty()
            .text("status code: " + statusCode + "\n-------------------------\n" + message);
    }

    // REGISTER EVENT LISTENERS =============================================================
    $("#loginForm").submit(function (event) {
        event.preventDefault();

        var $form = $(this);
        var formData = {
            email: $form.find('input[name="email"]').val(),
            password: $form.find('input[name="password"]').val()
        };

        doLogin(formData);
    });

    $("#logoutButton").click(doLogout);

    $("#updateForm").submit(function (event) {
       event.preventDefault();

        var $form = $(this);
        var formData = {
            firstname: $form.find('input[name="updateFirstName"]').val(),
            lastname: $form.find('input[name="updateLastName"]').val(),
            email: $form.find('input[name="updateEmail"]').val(),
            password: $form.find('input[name="updatePassword"]').val(),
            role: "ADMIN"
        };

        $.ajax({
            url: "/users/" + userId + "/pass",
            type: "POST",
            dataType: "json",
            data: JSON.stringify(formData),
            contentType: "application/json; charset=utf-8",
            headers: createAuthorizationTokenHeader(),
            success: function (data, textStatus, jqXHR) {
                showResponse(jqXHR.status, JSON.stringify(data));

            },
            error: function (jqXHR, textStatus, errorThrown) {
                showResponse(jqXHR.status, errorThrown);
            }
        });
    });

    var permission;

    $("#permission").change(function () {
        permission =  $("#permission").val();
        console.log(permission);
        if (permission == "LIST_OF_USERS") {
            $("#users").show();
            $.ajax({
                url: "/users/list/0/5",
                type: "GET",
                dataType: "json",
                headers: createAuthorizationTokenHeader(),
                success: function (data, textStatus, jqXHR) {
                    showResponse(jqXHR.status, JSON.stringify(data));
                    console.log(data.email);

                    $.each(data, function(i, item){
                        // alert("Mine is " + i + "|" + item.email );
                        $("#list_users").append($("<option>").text(item.email));
                    });
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    showResponse(jqXHR.status, errorThrown);
                }
            });
        } else {
            $("#list_users").empty();
            $("#users").hide();
        }
    });

    $("#list_users").change(function() {
        console.log(permission);
        permission = $("#list_users").val() + " ";
        console.log(permission);
    });
    var uuid;
    $("#btnSubmit").click(function (event) {

        event.preventDefault();
        //Get Form
        var form = $('#fileUploadForm')[0];

        var file = $('#fileId')[0].files[0];
        console.log(file.size);

        // var $form = $(this);
        // var formData = {
        //     file: file,
        //     userId: userId,
        //     ownerId: userId,
        //     fileSize: file.size,
        //     fileName: file.name,
        //     permission: "ALL_USERS",
        //     key: randomKey(16),
        //     expirationDate: "2020-11-26T15:25:09.651+02:00"
        // };

        var key = randomKey(16);
        // encryption(form, key);
        // var bytes;
        // if (file.size < 10) {
        //     var reader = new FileReader();
        //     reader.onload = function(e) {
        //
        //         // Use the CryptoJS library and the AES cypher to encrypt the
        //         // contents of the file, held in e.target.result, with the password
        //         encrypted = CryptoJS.AES.encrypt(e.target.result, key);
        //         bytes = new Uint8Array(e.target.result);
        //         console.log(bytes);
        //     };
        //     reader.readAsDataURL(file);
        // } else {
        //     bytes = file;
        // }
        permission = $("#list_users").val();
        console.log(permission);
        //Create an FormData object
        var data = new FormData(form);
        data.append("userId", userId);
        data.append("ownerId", userId);
        data.append("fileSize", file.size);
        data.append("mime", file.type);
        data.append("fileName", file.name);
        data.append("permission",  permission);
        data.append("key", key);

        // disabled the submit button
        $("#btnSubmit").prop("disabled", true);

        $.ajaxSetup({
            headers: { 'web': 'web' },
            beforeSend: function() {
                $('#loader').show();
            },
            complete: function(){
                $('#loader').hide();
            }
        });
        $.ajax({
            type: "POST",
            enctype: 'multipart/form-data',
            url: "user/files/uploadFile",
            dataType: "json",
            data: data,
            processData: false,
            contentType: false,
            cache: false,
            timeout: 600000,
            headers: createAuthorizationTokenHeader(),
            success: function (data, textStatus, jqXHR) {
                uuid = data.fileUUID.toString();
                showResponse(jqXHR.status, "Your file UUID to download crypted file: " + uuid);
                $response.append(document.createElement('br'));
                // var a = document.createElement('a');
                // a.setAttribute("id", "linkToFile");
                // a.href = 'javascript:void(0)';
                // a.innerHTML = "Your link to file";
                // $response.append(a);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                showResponse(jqXHR.status, errorThrown);
            }
        });
        $("#btnSubmit").prop("disabled", false);
    });

    $("#btnSubmitDownload").click(function (event) {
        event.preventDefault();
        var uuid = $("#uuidFile").val().trim();

        $.ajaxSetup({
            headers: { 'web': 'web' },
            beforeSend: function() {
                $('#loader').show();
            },
            complete: function(){
                $('#loader').hide();
            }
        });
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function() {
            console.log(typeof xhttp.response);
            if (this.readyState == 4 && this.status == 200) {
                var filename = "";
                var disposition = xhttp.getResponseHeader('Content-Disposition');
                if (disposition && disposition.indexOf('attachment') !== -1) {
                    var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
                    var matches = filenameRegex.exec(disposition);
                    if (matches != null && matches[1]) filename = matches[1].replace(/['"]/g, '');
                }

                var type = xhttp.getResponseHeader('Content-Type');
                var blob = new Blob([xhttp.response], { type: type });

                if (typeof window.navigator.msSaveBlob !== 'undefined') {
                    // IE workaround for "HTML7007: One or more blob URLs were revoked by closing the blob for which they were created. These URLs will no longer resolve as the data backing the URL has been freed."
                    window.navigator.msSaveBlob(blob, filename);
                } else {
                    var URL = window.URL || window.webkitURL;
                    var downloadUrl = URL.createObjectURL(blob);

                    if (filename) {
                        // use HTML5 a[download] attribute to specify filename
                        var a = document.createElement("a");
                        // safari doesn't support this yet
                        if (typeof a.download === 'undefined') {
                            window.location = downloadUrl;
                        } else {
                            a.href = downloadUrl;
                            a.download = filename;
                            document.body.appendChild(a);
                            a.click();
                        }
                    } else {
                        window.location = downloadUrl;
                    }

                    setTimeout(function () { URL.revokeObjectURL(downloadUrl); }, 100); // cleanup
                }
            }
        };
        xhttp.open("GET", "/files/" + uuid, true);
        xhttp.responseType = "blob";
        xhttp.setRequestHeader('X-Auth', getJwtToken());
        xhttp.send();
    });

    $("#register").submit(function(event) {

        event.preventDefault();

        var $form = $(this);
        var registerData = {
            firstname: $form.find('input[name="firstname"]').val(),
            lastname: $form.find('input[name="lastname"]').val(),
            email: $form.find('input[name="email"]').val(),
            password: $form.find('input[name="password"]').val(),
            role: "USER"
        };

        $.ajax({
            url: "/users",
            type: "POST",
            data: JSON.stringify(registerData),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            headers: createAuthorizationTokenHeader(),
            success: function (data, textStatus, jqXHR) {
                setJwtToken(jqXHR.getResponseHeader("X-AUTH"));
                $login.hide();
                $notLoggedIn.hide();
                userId = data.id;
                localStorage.setItem("userId", userId);
                console.log(userId);
                showTokenInformation();
                showUserInformation();
                $logoutPanel.show();
                $loginPanel.hide();
                $updown.show();
                $resp.show();
                $('#modal-register').modal('hide');
            },
            error: function (jqXHR, textStatus, errorThrown) {
                $('#modal-register').modal('hide');
                if (jqXHR.status == 401) {
                } else {
                    $('#loginErrorModal')
                        .modal("show")
                        .find(".modal-body")
                        .empty()
                        .html("<p>Validation exception:<br>" + jqXHR.responseJSON.message + "</p>");
                }
            }
        });
    });

    $("#exampleServiceBtn").click(function () {
        $.ajax({
            url: "/persons",
            type: "GET",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            headers: createAuthorizationTokenHeader(),
            success: function (data, textStatus, jqXHR) {
                showResponse(jqXHR.status, JSON.stringify(data));
            },
            error: function (jqXHR, textStatus, errorThrown) {
                showResponse(jqXHR.status, errorThrown);
            }
        });
    });

    $("#adminServiceBtn").click(function () {
        $.ajax({
            url: "/protected",
            type: "GET",
            contentType: "application/json; charset=utf-8",
            headers: createAuthorizationTokenHeader(),
            success: function (data, textStatus, jqXHR) {
                showResponse(jqXHR.status, data);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                showResponse(jqXHR.status, errorThrown);
            }
        });
    });

    $loggedIn.click(function () {
        $loggedIn
            .toggleClass("text-hidden")
            .toggleClass("text-shown");
    });

    // INITIAL CALLS =============================================================
    if (getJwtToken()) {
        $login.hide();
        $notLoggedIn.hide();
        showTokenInformation();
        showUserInformation();
    }
});
$(function () {
    $('#datetimepicker1').datetimepicker({
        format: "YYYY-MM-DDTkk:mm:ss.SSSZ",
        icons: {
            date: "fa fa-calendar",
            up: "fa fa-arrow-up",
            down: "fa fa-arrow-down"
        },
        minDate: new Date()
    })
});

function randomKey(length) {
    var chars = "abcdefghijklmnopqrstuvwxyz!@#$%^&*()-+<>ABCDEFGHIJKLMNOP1234567890";
    var pass = "";
    for (var x = 0; x < length; x++) {
        var i = Math.floor(Math.random() * chars.length);
        pass += chars.charAt(i);
    }
    return pass;
}


