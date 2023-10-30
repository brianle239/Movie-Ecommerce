
let pageAmt = getParameterByName('amt');
let sort = getParameterByName('sort');
let offset = getParameterByName('offset');
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
if (offset != null) {

    let page = parseInt(offset) / pageAmt;
    if (page === 0) {
        pages[0].addClass("disabled");
        pages[1].addClass("active");
        $(pages[2]).find("a").attr('href', 'movies.html?amt=' + pageAmt + "&sort=" + sort + "&offset=" + pageAmt);
        $(pages[3]).find("a").attr('href', 'movies.html?amt=' + pageAmt + "&sort=" + sort + "&offset=" + 2*parseInt(pageAmt));
    }
    else {
        $(pages[1]).find("a").text(page);
        $(pages[2]).find("a").text(page+1);
        pages[2].addClass("active");
        $(pages[3]).find("a").text(page+2);

        $(pages[1]).find("a").attr('href', 'movies.html?amt=' + pageAmt + "&sort=" + sort + "&offset=" + (parseInt(offset) - parseInt(pageAmt)));
        $(pages[3]).find("a").attr('href', 'movies.html?amt=' + pageAmt + "&sort=" + sort + "&offset=" + (parseInt(offset) + parseInt(pageAmt)));
    }


    console.log("yes offset");
}
else {
    pages[0].addClass("disabled");
    pages[1].addClass("active");
    $(pages[2]).find("a").attr('href', 'movies.html?amt=' + pageAmt + "&sort=" + sort + "&offset=" + pageAmt);
    $(pages[3]).find("a").attr('href', 'movies.html?amt=' + pageAmt + "&sort=" + sort + "&offset=" + 2*parseInt(pageAmt));
    offset = "0";

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

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");
    // let starElement = jQuery("#stars-link");
    //
    // starElement.append('<a href=' + '"stars.html">'
    //     + "Stars list"+     // display star_name for the link text
    //     '</a>');
    let movieTableBodyElement = jQuery("#movie_table_body");
    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < Math.min(resultData.length); i++) {
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
        const genres_array = resultData[i]["movie_genres"].split("\t");
        const genres_id_array = resultData[i]["movie_genres_id"].split("\t");
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
        const stars_array = resultData[i]["movie_stars"].split("\t");
        const stars_id_array = resultData[i]["movie_stars_id"].split("\t");
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

        //rowHTML += "<th><button type='button' class='btn btn-primary add-to-cart'>Add to Cart</button></th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);

        // Set highest page
        let highestPage = Math.ceil(parseInt(resultData[0]["total_rows"])/parseInt(pageAmt));
        let lastOffset = (highestPage * (parseInt(pageAmt))-parseInt(pageAmt));
        $(pages[4]).find("a").text(highestPage);

        $(pages[4]).find("a").attr('href', 'movies.html?amt=' + pageAmt + "&sort=" + sort + "&offset=" + lastOffset);
        if (parseInt(offset) === lastOffset) {
            pages[3].addClass("disabled");
            pages[5].addClass("disabled");
            $(pages[3]).find("a").text("...");
        }

    }
}



// // Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/movies?amt=" + pageAmt + "&sort=" + sort + "&offset=" + offset, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

let page_form = $("#page_form");
function submitPageForm(formSubmitEvent) {
    console.log("submit page form");
    let pageAmt = $( "select#formPgAmt option:checked" ).val();
    console.log(pageAmt)
    let sort = $( "select#formSort option:checked" ).val();
    formSubmitEvent.preventDefault();
    window.location.href = 'movies.html?amt=' + pageAmt + "&sort=" + sort;

}


page_form.submit(submitPageForm);

function nextPage() {
    offset = parseInt(offset) + parseInt(pageAmt);
    window.location.href = 'movies.html?amt=' + pageAmt + "&sort=" + sort + "&offset=" + offset;

}

function prevPage() {
    offset = parseInt(offset) - parseInt(pageAmt);
    window.location.href = 'movies.html?amt=' + pageAmt + "&sort=" + sort + "&offset=" + offset;

}
