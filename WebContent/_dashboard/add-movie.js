let add_movie_info = $("#add_movie_info");

function handleMovie(result) {
    add_movie_info[0].reset();

    console.log("handle movie");
    console.log(result);

    let movie_message = $("#add_movie_message");

    if (result["message"] === "success") {
        let text_popup = $('<p>').text(`Success! movieId: ${result["movie_id"]}  
        starID: ${result["star_id"]}  
        genreID: ${result["genre_id"]}`);

        movie_message.append(text_popup);
    } else {
        let text_popup = $('<p>').text("Error: Movie is a duplicate!");
        movie_message.append(text_popup);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitMovieForm(formSubmitEvent) {
    console.log("adding movie");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    jQuery.ajax({
        dataType: "json",
        method: "POST",
        url: "api/add-movie",
        data: add_movie_info.serialize(),
        success: (resultData) => handleMovie(resultData)
    });
}

// Bind the submit action of the form to a handler function
add_movie_info.submit(submitMovieForm);

