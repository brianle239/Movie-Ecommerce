jQuery.ajax({
    method: "GET",
    url: "api/cart",
    success: function(resultData) {
        console.log(resultData)
        var username = resultData["user"];
        $("#username").text(username);
        for (let key in resultData["cart"]) {
            $("#userItems").append(`<li class="list-group-item">${key}</li>`);
        }
    },
    error: (jqXHR, textStatus, errorThrown) => {
        console.error("AJAX error:", textStatus, errorThrown);
        console.log("Raw response:", jqXHR.responseText);
        alert("Error occurred: " + textStatus);
    },
});