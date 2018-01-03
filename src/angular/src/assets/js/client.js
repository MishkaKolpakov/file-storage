// window["$"] = $;
// $(function () {
//     // VARIABLES =============================================================
//     // var $response = $("#response");
//     // var $resp = $("#resp").hide();
//     $("#users").hide();
//
//     $("#btnSubmitDownload").click(function (event) {
//         event.preventDefault();
//         var uuid = $("#uuidFile").val().trim();
//
//         $.ajaxSetup({
//             headers: { 'web': 'web' },
//             beforeSend: function() {
//                 $('#loader').show();
//             },
//             complete: function(){
//                 $('#loader').hide();
//             }
//         });
//         var xhttp = new XMLHttpRequest();
//         xhttp.onreadystatechange = function() {
//             console.log(typeof xhttp.response);
//             if (this.readyState == 4 && this.status == 200) {
//                 var filename = "";
//                 var disposition = xhttp.getResponseHeader('Content-Disposition');
//                 if (disposition && disposition.indexOf('attachment') !== -1) {
//                     var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
//                     var matches = filenameRegex.exec(disposition);
//                     if (matches != null && matches[1]) filename = matches[1].replace(/['"]/g, '');
//                 }
//
//                 var type = xhttp.getResponseHeader('Content-Type');
//                 var blob = new Blob([xhttp.response], { type: type });
//
//                 if (typeof window.navigator.msSaveBlob !== 'undefined') {
//                     // IE workaround for "HTML7007: One or more blob URLs were revoked by closing the blob for which they were created. These URLs will no longer resolve as the data backing the URL has been freed."
//                     window.navigator.msSaveBlob(blob, filename);
//                 } else {
//                     var URL = window.URL || window.webkitURL;
//                     var downloadUrl = URL.createObjectURL(blob);
//
//                     if (filename) {
//                         // use HTML5 a[download] attribute to specify filename
//                         var a = document.createElement("a");
//                         // safari doesn't support this yet
//                         if (typeof a.download === 'undefined') {
//                             window.location = downloadUrl;
//                         } else {
//                             a.href = downloadUrl;
//                             a.download = filename;
//                             document.body.appendChild(a);
//                             a.click();
//                         }
//                     } else {
//                         window.location = downloadUrl;
//                     }
//
//                     setTimeout(function () { URL.revokeObjectURL(downloadUrl); }, 100); // cleanup
//                 }
//             }
//         };
//         xhttp.open("GET", "/files/" + uuid, true);
//         xhttp.responseType = "blob";
//
//         console.log(localStorage.getItem('currentUser'));
//         xhttp.setRequestHeader('X-Auth', localStorage.getItem('token'));
//         xhttp.send();
//     });
//
// });
//
