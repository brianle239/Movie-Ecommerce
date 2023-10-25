

function populateMovieCard(resultData) {
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
                    <button class="cart-btn" id="${movie_id[0]}">Add to Cart</button>
                </div>
            </div>`;

        // Append the card to the container
        moviesContainer.append(movieCardHtml);
    }
}



$(document).ready(function() {
    $(document).on('click', '.cart-btn', function(event) {
        var data = { item: this.id, increase: true, remove: "false" }
        jQuery.ajax({
            method: "POST",
            data: data,
            url: "api/cart", // Setting request url, which is mapped by StarsServlet in Stars.java
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
    url: "api/movies", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => populateMovieCard(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

