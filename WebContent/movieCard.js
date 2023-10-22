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
    console.log("handleResult: populating movie info from resultData");
    console.log(resultData)


    // Get the movie container
    let moviesContainer = $(".cards-container")
    moviesContainer.empty(); // Remove any existing content

    // Loop through resultData, each element in resultData is info about a movie
    for (let i = 0; i < resultData.length; i++) {
        // Construct card content for each movie
        const stars_array = resultData[i]["movie_stars"].split(",");
        const stars_id_array = resultData[i]["movie_stars_id"].split(",");
        const movie_id = resultData[i]["movie_id"].split(",");
        const movie_title = resultData[i]["movie_title"].split(",");
        let movieCardHtml =
            `<div class="movie-card">
                <div class="movie-header">
                    <div class="title-and-rating">`;

        movieCardHtml+=
            `<h1><span>`;
        movieCardHtml+=
        '<a href="single-movie.html?id=' + movie_id[0] + '">'
        + movie_title[0] +   // display star_name for the link text
        '</a>';

        movieCardHtml+= `</span></h1>`;

        movieCardHtml +=` <h2 class="rating">Rating: <span>${resultData[i]["movie_rating"]}</span></h2>
                    </div>
                    <p class="release-date">Release Date: <span>${resultData[i]["movie_year"]}</span></p>
                </div> `;
            // limit genre and stars


        movieCardHtml += `<div class="movie-content">
                    <p>Stars: <span>`;



        for (let i = 0; i < Math.min(3, stars_array.length); i++) {
            if (i == Math.min(3, stars_array.length) - 1) {
                 movieCardHtml+=
                     '<a href="single-star.html?id=' + stars_id_array[i] + '">'
                    + stars_array[i] +   // display star_name for the link text
                    '</a>';
            }
            else {
                 movieCardHtml+=
                     '<a href="single-star.html?id=' + stars_id_array[i] + '">'
                    + stars_array[i] + ", " +   // display star_name for the link text
                    '</a>';
            }
        }
            movieCardHtml += `</span></p>
                    <p>Genre: <span>${resultData[i]["movie_genres"]}</span></p>
                </div> `;
            movieCardHtml += `  <div class="movie-footer">
                    <button class="cart-btn" ">Add to Cart</button>
                </div>
            </div>`;

        // Append the card to the container
        moviesContainer.append(movieCardHtml);
    }
}


// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/movies", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});
