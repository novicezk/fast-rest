/**
 * Created by zhukai on 17-2-9.
 */
$(function () {
    $("#error")[0].style.display = "none";
    $("#submit").click(function () {
        login();
    });
    $("#password").keyup(function () {
        if (event.keyCode == 13) {
            login();
        }
    });

    $("body").click(function () {
        $("#error")[0].style.display = "none";
    });

    var login = function () {
        var username = $("#username").val();
        var password = $("#password").val();
        $.ajax({
            type: "POST",
            url: "../user/login",
            data: {
                username: username,
                password: password
            },
            success: function (data) {
                var obj = JSON.parse(data);
                if (obj.code == -1) {
                    location.href = "home.html";
                } else {
                    $("#error").html(obj.description);
                    $("#error")[0].style.display = "";
                }
            }
        });
    }
});