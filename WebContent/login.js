let login_form = $("#login_form");

function submitLoginForm(formSubmitEvent) {
    console.log("submit login form");
    formSubmitEvent.preventDefault();
    /*
    if (typeof grecaptcha !== "undefined" && grecaptcha.getResponse().length === 0) {
        console.log("error")
        const errorMessageDiv = jQuery("#login_error_message");
        errorMessageDiv.text("Please check the reCAPTCHA.");
        errorMessageDiv.show();
    } else { */

        $.ajax(
            "api/login", {
                method: "POST",
                data: login_form.serialize(),
                success: handleLoginResult
            }
        );

}


function handleLoginResult(resultDataString) {
    console.log(resultDataString)
    let resultDataJson = resultDataString;

    console.log("handle login response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    if (resultDataJson["status"] === "success") {
        window.location.href = "index.html";
    } else {

        console.log("show error message");
        console.log(resultDataJson["message"]);
        const errorMessageDiv = jQuery("#login_error_message");
        errorMessageDiv.text(resultDataJson["message"]);
        errorMessageDiv.show();

    }
}



login_form.submit(submitLoginForm);