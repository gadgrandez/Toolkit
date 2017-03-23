/**
 * Created by snam on 22.03.17.
 */


$(function() {
    var checkAuthResponse, loadMiniProfile;
    $("#auth-tabs").tabs();
    $("#signin-form").on('submit', function(event) {
        var form_data;
        event.preventDefault();
        form_data = $("#signin-form").serialize();
        return $.ajax({
            data: form_data,
            url: "signin/submit/",
            method: 'POST'
        }).done(function(json) {
            return checkAuthResponse(json);
        });
    });
    $("#signup-form").on('submit', function(event) {
        var form_data;
        event.preventDefault();
        if (!$("#signup-submit").hasClass("disabled")) {
            form_data = $("#signup-form").serialize();
            return $.ajax({
                data: form_data,
                url: "signup/submit/",
                method: 'POST'
            }).done(function(json) {
                return checkAuthResponse(json);
            });
        }
    });
    $("#acceptToS").change(function(event) {
        if ($("#acceptToS").val() === "true") {
            return $("#acceptToS").val("false");
        } else {
            return $("#acceptToS").val("true");
        }
    });
    $("#signup-form").change(function(event) {
        var buttonDisabled;
        buttonDisabled = $("#acceptToS").val() === "false";
        $("#signup-form").find(':input').each(function() {
            if (!this.value && this.type !== "submit") {
                return buttonDisabled = true;
            }
        });
        $(".is-invalid-input").each(function() {
            return buttonDisabled = true;
        });
        if (buttonDisabled) {
            return $("#signup-submit").addClass('disabled');
        } else {
            return $("#signup-submit").removeClass('disabled');
        }
    });
    checkAuthResponse = function(json) {
        if (json.successful) {
            $("#auth-link-text").html(json.user.nameLogin);
            $("#auth-dropdown-link").show();
            $("#auth-link").remove();
            $("#overlay-content").html(json.message);
            Job.reloadList();
            location.reload();
            return setTimeout(loadMiniProfile, 1000);
        } else {
            $("#auth-alert").html(json.message);
            return $("#auth-alert").fadeIn();
        }
    };
    loadMiniProfile = function() {
        closeNav();
        return $.ajax({
            url: "/miniprofile",
            method: 'GET'
        }).done(function(data) {
            return $("#overlay-content").html(data);
        });
    };
    $("#auth-alert").on('click', function(event) {
        return $("#auth-alert").fadeOut();
    });
    return $("#profile-open").on('click', function(event) {
        return $.ajax({
            url: "/profile",
            method: 'GET'
        }).done(function(data) {
            return $("#modal").html(data).foundation('open');
        });
    });
});