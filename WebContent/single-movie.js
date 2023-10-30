
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");
    console.log(resultData);


    let starInfoElement = jQuery("#movie-title");

    starInfoElement.append("<p>Star Name: " + resultData[0]["movie_title"] + "</p>");

    console.log("handleResult: populating movie table from resultData");
    console.log(resultData);

    let movieTableBodyElement = jQuery("#movie_table_body");

    let rowHTML = "";
    rowHTML += "<tr>";
    rowHTML += "<th>" + resultData[0]["movie_title"] + "</th>";
    rowHTML += "<th>" + resultData[0]["movie_year"] + "</th>";
    rowHTML += "<th>" + resultData[0]["movie_director"] + "</th>";
    const genres_array = resultData[0]["movie_genres"].split(",");
    const genres_id_array = resultData[0]["movie_genres_id"].split(",");

    // rowHTML += "<th>" + resultData[0]["movie_genres"] + "</th>";
    rowHTML += "<th>";
    for (let i = 0; i < genres_array.length; i++) {
        if (i == genres_array.length - 1) {
            rowHTML +=
                '<a href="movieCard.html?id=' + genres_id_array[i] + '">'
                + genres_array[i] +
                '</a>';
        }
        else {
            rowHTML +=
                '<a href="movieCard.html?id=' + genres_id_array[i] + '">'
                + genres_array[i] + ", " +
                '</a>';
        }
    }
    rowHTML += "</th>";


    const stars_array = resultData[0]["movie_stars"].split(",");
    const stars_id_array = resultData[0]["movie_stars_id"].split(",");

    rowHTML += "<th>";
    for (let i = 0; i < stars_array.length; i++) {
        if (i == stars_array.length - 1) {
            rowHTML +=
                '<a href="single-star.html?id=' + stars_id_array[i] + '">'
                + stars_array[i] +
                '</a>';
        }
        else {
            rowHTML +=
                '<a href="single-star.html?id=' + stars_id_array[i] + '">'
                + stars_array[i] + ", " +
                '</a>';
        }
    }
    rowHTML += "</th>";

    rowHTML += "<th>" + resultData[0]["movie_rating"] + "</th>";
    rowHTML += "<th><button class='cart-btn btn' id='" + resultData[0]["movie_id"] + "'>Add to Cart</button></th>";
    rowHTML += "</tr>";

    movieTableBodyElement.append(rowHTML);


}
$(document).ready(function() {
    $(document).on('click', '.cart-btn', function(event) {
        event.preventDefault();
        var data = { item: this.id, increase: true, remove: "false" }
        jQuery.ajax({
            method: "POST",
            data: data,
            url: "api/cart",
            success: (resultData) => {
                console.log(resultData);
                alert("Added to cart!");

            },
            error: (jqXHR, textStatus, errorThrown) => {
                console.error("AJAX error:", textStatus, errorThrown);
                alert("Error occurred: " + textStatus);
            },
        });
    });
});


// Get id from URL
let movieId = getParameterByName('id');

jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "POST",// Setting request method
    // Do not change the url
    url: "api/session?single=true",
    success: (resultData) => setUrl(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});
// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Movie.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleMovieServlet
});