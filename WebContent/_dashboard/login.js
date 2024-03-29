let login_form = $("#login_form");

function submitLoginForm(formSubmitEvent) {

    console.log("submit login form");
    formSubmitEvent.preventDefault();

    $.ajax(
        "login", {
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