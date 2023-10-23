
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