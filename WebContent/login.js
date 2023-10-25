let login_form = $("#login_form");

function submitLoginForm(formSubmitEvent) {
    console.log("submit login form");
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/login", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
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
        window.location.replace("http://localhost:8080/fabflix_war/index.html");
    } else {

        console.log("show error message");
        console.log(resultDataJson["message"]);
        const errorMessageDiv = jQuery("#login_error_message");
        errorMessageDiv.text(resultDataJson["message"]);
        errorMessageDiv.show();

    }
}



login_form.submit(submitLoginForm);