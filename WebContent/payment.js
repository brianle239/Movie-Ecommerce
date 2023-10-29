$(document).ready(function() {
    $('#paymentForm').on('submit', function(event) {
        event.preventDefault();
        console.log("pressed payment")

        var firstName = $('#firstName').val();
        var lastName = $('#lastName').val();
        var cardNumber = $('#cardNumber').val();
        var expiry = $('#expiry').val();

        let data = {
            firstName: firstName,
            lastName: lastName,
            cardNumber: cardNumber,
            expiry: expiry
        }
        console.log(data)

        jQuery.ajax({
            type: 'POST',
            url: 'api/payment',
            data: data,
            success: function(resultDataJson) {

                if (resultDataJson["status"] === "success") {
                    console.log(resultDataJson)
                    window.location.href = "confirmation.html";
                } else {

                    console.log("show error message");
                    console.log(resultDataJson["message"]);
                    const errorMessageDiv = jQuery("#login_error_message");
                    errorMessageDiv.text(resultDataJson["message"]);
                    errorMessageDiv.show();

                }
            }
        });

    });
});
