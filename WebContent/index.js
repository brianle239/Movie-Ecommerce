
function handleResult(resultData) {
    formElement = $("#genreForm");
    let optionHTML = "";
    for (let i = 0; i <resultData.length; i++) {
        let optionHTML = "";
        optionHTML += '<option value="' + resultData[i]["id"] + '">' + resultData[i]["name"] + '</option>';
        // <option value="10">10</option>

        formElement.append(optionHTML);
    }
}

console.log("Hello");
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/genre", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});
/*
session data logic

function handleSessionData(resultDataString) {
    let resultDataJson = resultDataString;
    console.log(resultDataJson);
    //console.log(resultDataJson["sessionID"]);

}

$("#session-button").click(function(event) {
    event.preventDefault();

    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "/fabflix_war/api/session", // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleSessionData(resultData)// Setting callback function to handle data returned successfully by the SingleStarServlet
    });
})

*/