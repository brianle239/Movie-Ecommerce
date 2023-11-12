function showForm(formType) {
    $(`#${formType}Form`).modal('show');
}
$(document).ready(function() {

    // Handle submission of the 'Add a Star' form
    $('#starForm form').on('submit', function(event) {
        event.preventDefault(); // Prevent the default form submission

        var starData = {
            starName: $('#starName').val(),
            birthYear: $('#birthYear').val()
        };

        $.post('/add-star', starData, function(response) {
            console.log(response); // Log the response from the server
            $('#starForm').modal('hide'); // Hide the modal
        });
    });

    // Handle submission of the 'Add a Movie' form
    $('#movieForm form').on('submit', function(event) {
        event.preventDefault(); // Prevent the default form submission

        var movieData = {
            movieTitle: $('#movieTitle').val(),
            year: $('#movieYear').val(),
            directorName: $('#directorName').val(),
            starName: $('#starNameMovie').val(),
            birthYear: $('#birthYearMovie').val(),
            genre: $('#genre').val()
        };

        $.post('/add-movie', movieData, function(response) {
            console.log(response); // Log the response from the server
            $('#movieForm').modal('hide'); // Hide the modal
        });
    });

    // Handle submission of the 'Add a Genre' form
    $('#genreForm form').on('submit', function(event) {
        event.preventDefault(); // Prevent the default form submission

        var genreData = {
            genreName: $('#genreName').val()
        };

        $.post('/add-genre', genreData, function(response) {
            console.log(response); // Log the response from the server
            $('#genreForm').modal('hide'); // Hide the modal
        });
    });

});
