function showForm(formType) {
    $(`#${formType}Form`).modal('show');
}
$(document).ready(function() {

    $('#movieForm form').on('submit', function(event) {
        event.preventDefault();

        var birth = 0;
        if($('#birthYearMovie').val()){
            birth = $('#birthYearMovie').val()
        } else {
            birth = null;
        }

        var movieData = {
            title: $('#movieTitle').val(),
            year: $('#movieYear').val(),
            director: $('#directorName').val(),
            starName: $('#starNameMovie').val(),
            birthYear:birth ,
            genre: $('#genre').val()
        };
        console.log(movieData)

        $.ajax("addMovie", {
            method: "POST",
            data: movieData,
            dataType: "json",
            success: function (response) {
                console.log(response)
                var status = response.status;
                var output = "Added succesfully! " + status

                if(status === "Duplicate movie found. No new movie added."){
                    $('#movieMessage')
                        .text(status)
                        .addClass('alert-danger')
                        .show();

                } else {
                    $('#movieMessage')
                        .text(output)
                        .addClass('alert-success')
                        .removeClass('alert-danger')
                        .show();
                }

            },
            error: function (xhr, status, error) {
                $('#movieMessage')
                    .text("Error adding movie: " + xhr.responseText)
                    .addClass('alert-danger')
                    .removeClass('alert-success')
                    .show();
                console.error("Error adding movie:", error);
                console.error("Status", status);
            },
        });

    });
    $('#starForm form').on('submit', function(event) {
        event.preventDefault();
        var birth = 0;
        if($('#birthYear').val()){
            birth = $('#birthYear').val()
        } else {
            birth = null;
        }

        var starData = {
            name: $('#starName').val(),
            birthYear:birth ,

        };
        console.log(starData)

        $.ajax("addStar", {
            method: "POST",
            data: starData,
            dataType: "json",
            success: function (response) {
                console.log(response)
                var status = response.status;
                var output = "Added succesfully! " + status

                $('#starMessage')
                    .text(output)
                    .addClass('alert-success')
                    .removeClass('alert-danger')
                    .show();

            },
            error: function (xhr, status, error) {
                $('#starMessage')
                    .text("Error adding star: " + xhr.responseText)
                    .addClass('alert-danger')
                    .removeClass('alert-success')
                    .show();
                console.error("Error adding star:", error);
                console.error("Status", status);
            },
        });

    });
    $('#genreForm form').on('submit', function(event) {
        event.preventDefault();

        var genreData = {
            name: $('#genreName').val(),

        };
        console.log(genreData)

        $.ajax("addGenre", {
            method: "POST",
            data: genreData,
            dataType: "json",
            success: function (response) {
                console.log(response)
                var status = response.status;
                var output = "Added succesfully! " + status

                $('#genreMessage')
                    .text(output)
                    .addClass('alert-success')
                    .removeClass('alert-danger')
                    .show();

            },
            error: function (xhr, status, error) {
                $('#genreMessage')
                    .text("Error adding genre: " + xhr.responseText)
                    .addClass('alert-danger')
                    .removeClass('alert-success')
                    .show();
                console.error("Error adding genre:", error);
                console.error("Status", status);
            },
        });

    });
});

