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

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    let movieTableBodyElement = jQuery("#movie_table_body");
    console.log(resultData);
    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < Math.min(20, resultData.length); i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" +
            '<a href="single-movie.html?id=' + resultData[i]["movie_id"] + '">'
            + resultData[i]["movie_title"] +
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        // rowHTML += "<th>" + resultData[i]["movie_genres"] + "</th>";
        rowHTML += "<th>";
        const genres_array = resultData[i]["movie_genres"].split(",");
        const genres_id_array = resultData[i]["movie_genres_id"].split(",");
        for (let i = 0; i < Math.min(3, genres_array.length); i++) {
            if (i == Math.min(3, genres_array.length) - 1) {
                rowHTML +=
                    genres_array[i];  // display star_name for the link text
                // + '</a>';
            }
            else {
                rowHTML +=
                    genres_array[i] + ", ";   // display star_name for the link text
                // + '</a>';
            }
        }
        rowHTML += "</th>";
        const stars_array = resultData[i]["movie_stars"].split(",");
        const stars_id_array = resultData[i]["movie_stars_id"].split(",");
        rowHTML += "<th>";
        for (let i = 0; i < Math.min(3, stars_array.length); i++) {
            if (i == Math.min(3, stars_array.length) - 1) {
                rowHTML +=
                    '<a href="single-star.html?id=' + stars_id_array[i] + '">'
                    + stars_array[i] +   // display star_name for the link text
                    '</a>';
            }
            else {
                rowHTML +=
                    '<a href="single-star.html?id=' + stars_id_array[i] + '">'
                    + stars_array[i] + ", " +   // display star_name for the link text
                    '</a>';
            }
        }
        rowHTML += "</th>";

        rowHTML += "<td>" + resultData[i]["movie_rating"] + "</td>";
        rowHTML += "<th><button class='cart-btn btn' id='" + resultData[i]["movie_id"] + "'>Add to Cart</button></th>";


        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
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

jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/movielist", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

