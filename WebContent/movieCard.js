let pageAmt = getParameterByName('amt');
let sort = getParameterByName('sort');
let offset = getParameterByName('offset');
let genreId = getParameterByName('genreId');
let titleChar = getParameterByName('titleChar');
let movie_name = getParameterByName('movie_name');
let year = getParameterByName('year');
let director = getParameterByName('director');
let star_name = getParameterByName('star_name');

var pages = [];
$('#pageBar li').each(function(){
    pages.push($(this));
});
if (pageAmt != null) {
    $("#formPgAmt").val(pageAmt).change();
}
else {
    pageAmt = "10";
}
if (sort != null) {
    $("#formSort").val(sort).change();
}
else {
    sort = "r Desc Desc";
}
if (offset != null && offset !== "0") {
    console.log(offset);
    let page = parseInt(offset) / pageAmt;
    $(pages[1]).find("a").text(page);
    $(pages[2]).find("a").text(page+1);
    pages[2].addClass("active");
    $(pages[3]).find("a").text(page+2);

    $(pages[1]).find("a").attr('href', 'movieCard.html?amt=' + pageAmt + "&sort=" + sort + "&offset=" + (parseInt(offset) - parseInt(pageAmt)) + addUrlParemeter());
    $(pages[3]).find("a").attr('href', 'movieCard.html?amt=' + pageAmt + "&sort=" + sort + "&offset=" + (parseInt(offset) + parseInt(pageAmt))+ "&genreId=" + genreId + "&titleChar=" + titleChar);

}
else {
    pages[0].addClass("disabled");
    pages[1].addClass("active");
    $(pages[2]).find("a").text(2);
    $(pages[3]).find("a").text(3);
    $(pages[2]).find("a").attr('href', 'movieCard.html?amt=' + pageAmt + "&sort=" + sort + "&offset=" + pageAmt + addUrlParemeter());
    $(pages[3]).find("a").attr('href', 'movieCard.html?amt=' + pageAmt + "&sort=" + sort + "&offset=" + 2 * parseInt(pageAmt) + addUrlParemeter());
    offset = "0";
}

function addUrlParemeter() {
    let res = "";
    if (genreId != null) {
        res += "&genreId=" + genreId;
    }
    if (titleChar != null) {
        res += "&titleChar=" + titleChar;
    }
    if (movie_name != null) {
        res += "&movie_name=" + movie_name;
    }
    if (year != null) {
        res += "&year=" + year;
    }
    if (director != null) {
        res += "&director=" + year;
    }
    if (star_name != null) {
        res += "&star_name=" + star_name;
    }

    return res;

}
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

        movieCardHtml+=`<h1><span>`;
        movieCardHtml+= '<a href="single-movie.html?id=' + movie_id[0] + '">' + movie_title[0] + '</a>';
        movieCardHtml+= `</span></h1>`;

        movieCardHtml +=` <h2 class="rating">Rating: <span>${resultData[i]["movie_rating"]}</span></h2></div>
                    <p class="release-date">Release Date: <span>${resultData[i]["movie_year"]}</span></p>
                    <p class="director">Director: <span>${resultData[i]["movie_director"]}</span></p>
                    </div>`;
        movieCardHtml +=
            `<div class="movie-content">
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
                           <p>Genre: <span>${resultData[i]["movie_genres"]}</span></p> </div> `;
        movieCardHtml += `  <div class="movie-footer">
                    <button class="cart-btn" id="${movie_id[0]}">Add to Cart</button>
                </div>
            </div>`;

        // Append the card to the container
        moviesContainer.append(movieCardHtml);
        // Set highest page
        let highestPage = Math.ceil(parseInt(resultData[0]["total_rows"])/parseInt(pageAmt));
        let lastOffset = (highestPage * (parseInt(pageAmt))-parseInt(pageAmt));
        $(pages[4]).find("a").text(highestPage);

        $(pages[4]).find("a").attr('href', 'movieCard.html?amt=' + pageAmt + "&sort=" + sort + "&offset=" + lastOffset + addUrlParemeter());
        if (parseInt(offset) === lastOffset) {
            pages[3].addClass("disabled");
            pages[5].addClass("disabled");
            $(pages[3]).find("a").text("...");
        }

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
    // Do not change the url
    url: "api/movies?amt=" + pageAmt + "&sort=" + sort + "&offset=" + offset + "&genreId=" + genreId + "&titleChar=" + titleChar + "&movie_name=" + movie_name + "&year=" + year + "&director=" + director + "&star_name=" + star_name, // Do not change url
    success: (resultData) => populateMovieCard(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

let page_form = $("#page_form");
function submitPageForm(formSubmitEvent) {
    console.log("submit page form");
    let pageAmt = $( "select#formPgAmt option:checked" ).val();
    console.log(pageAmt)
    let sort = $( "select#formSort option:checked" ).val();
    formSubmitEvent.preventDefault();
    window.location.href = 'movieCard.html?amt=' + pageAmt + "&sort=" + sort + addUrlParemeter();

}
page_form.submit(submitPageForm);

function nextPage() {
    offset = parseInt(offset) + parseInt(pageAmt);
    window.location.href = 'movieCard.html?amt=' + pageAmt + "&sort=" + sort + "&offset=" + offset + addUrlParemeter();

}

function prevPage() {
    offset = parseInt(offset) - parseInt(pageAmt);
    window.location.href = 'movieCard.html?amt=' + pageAmt + "&sort=" + sort + "&offset=" + offset + addUrlParemeter();

}