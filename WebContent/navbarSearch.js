function search() {
    let movie_name = $( "#titleSearch" ).val();
    let year_search = $( "#yearSearch" ).val();
    let director_search = $( "#directorSearch" ).val();
    let star_search = $( "#starSearch" ).val();

    window.location.href = 'movieCard.html?movie_name=' + movie_name + "&year=" + year_search + "&director=" + director_search + "&star_name=" + star_search;
}