
function handleResult(resultData) {
    console.log(resultData);
    formGenreElement = $("#genreForm");
    formTitleElement = $("#titleForm");
    let optionHTML = "";
    let count = 0;
    for (let i = 0; i <resultData.length; i++) {
        if (resultData[i]["name"] == null) {
            count = i;
            break;
        }
        let optionHTML = "";
        optionHTML += '<option value="' + resultData[i]["id"] + '">' + resultData[i]["name"] + '</option>';
        // <option value="10">10</option>

        formGenreElement.append(optionHTML);
    }
    for (let i = count; i <resultData.length; i++) {
        let optionHTML = "";
        optionHTML += '<option value="' + resultData[i]["titleChar"] + '">' + resultData[i]["titleChar"] + '</option>';
        // <option value="10">10</option>

        formTitleElement.append(optionHTML);
    }
}

jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/genre", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

function search() {
    let movie_name = $( "#titleSearch" ).val();
    let year_search = $( "#yearSearch" ).val();
    let director_search = $( "#directorSearch" ).val();
    let star_search = $( "#starSearch" ).val();

    window.location.href = 'movieCard.html?movie_name=' + movie_name + "&year=" + year_search + "&director=" + director_search + "&star_name=" + star_search;
}

let genre_form = $("#genre_form");
function genreBrowse(formSubmitEvent) {
    let genreId = $( "select#genreForm option:checked" ).val();
    formSubmitEvent.preventDefault(); // or return False
    window.location.href = 'movieCard.html?genreId=' + genreId;
}
genre_form.submit(genreBrowse);


let title_form = $("#title_form");
function titleBrowse(formSubmitEvent) {
    let titleChar = $( "select#titleForm option:checked" ).val();
    formSubmitEvent.preventDefault(); // or return False
    window.location.href = 'movieCard.html?titleChar=' + titleChar;
}
title_form.submit(titleBrowse);
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