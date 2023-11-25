
function handleResult(resultData) {
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

function handleLookup(query, doneCallback) {
    var startTime, endTime;
    startTime = new Date();
    console.log("Autocomplete initiated");

    // with the query data
    jQuery.ajax({
        "method": "GET",
        // generate the request url from the query.
        // escape the query string to avoid errors caused by special characters
        "url": "api/autocomplete?movie_name=" + escape(query),
        "success": function(data) {
            // pass the data, query, and doneCallback function into the success handler
            handleLookupAjaxSuccess(data, query, doneCallback)
            if (data["found"] === "true") {
                console.log("Used Cache");
            }
            else {
                console.log("Used Ajax Request")
            }
            var suggestionList = [];
            for(let i = 0; i < data["result"].length; i++) {
                suggestionList.push(data["result"][i]["value"]);
            }
            console.log("Suggestion List: " + suggestionList);
            endTime = new Date();
            let elapsedSec = (endTime - startTime)/1000
            console.log("Elapsed Time: " + elapsedSec);
        },
        "error": function(errorData) {
            console.log("lookup ajax error");
            console.log(errorData);
        }
    });
}
function handleLookupAjaxSuccess(data, query, doneCallback) {
    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback( { suggestions: data["result"] } );
}

$('#autocomplete').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        console.log(suggestion);
        window.location.href = "single-movie.html?id=" + suggestion["data"]["id"];

        // handleSelectSuggestion(suggestion);
    },
    // set delay time
    deferRequestBy: 300,
    minChars: 3,
    // autoSelectFirst: true,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
});

let search_icon = $("#autoSearch");
function autoCompleteSearch(formSubmitEvent) {
    let search = $( "#autocomplete" ).val();
    formSubmitEvent.preventDefault(); // or return False
    window.location.href = 'movieCard.html?movie_name=' + search;
}
search_icon.submit(autoCompleteSearch);

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