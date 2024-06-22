/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
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

function starIDListToHTML(StringListOfStarIDs, StringListOfStars) {
    let ListStringsOfStars = StringListOfStars.split(", "); // [a,b,c]
    let ListStringsOfStarIDs = StringListOfStarIDs.split(", "); // [1,2,3]

    let returnStr = "", size = ListStringsOfStarIDs.length;
    for(let i=0; i<size; ++i)
    {
        returnStr += '<a href="single-star.html?id=' + ListStringsOfStarIDs[i] + '">'
            + ListStringsOfStars[i] +     // display movie_title for the link text
            '</a>';
        if(i<size-1) {
            returnStr += ", ";
        }
    }
    return returnStr;
}

function genreListToHTML(StringListOfGenres) {
    let ListStringsOfGenres = StringListOfGenres.split(", "); // [a,b,c]

    let returnStr = "", size = ListStringsOfGenres.length;
    for(let i=0; i<size; ++i)
    {
        returnStr += '<a href="result.html?genre=' + ListStringsOfGenres[i] + '">'
            + ListStringsOfGenres[i] +     // display movie_title for the link text
            '</a>';
        if(i<size-1) {
            returnStr += ", ";
        }
    }
    return returnStr;
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {
    console.log("handleResult: populating movie info from resultData");

    // populate the movie info h3
    // find the empty h3 body by id "movie-info"
    let movieInfoElement = jQuery("#movie-info");

    // append two html <p> created to the h3 body, which will refresh the page
    movieInfoElement.append("<p>Movie Title: " + resultData[0]["movie_title"] + "</p>" +
        "<p>Release Year: " + resultData[0]["movie_year"] + "</p>");

    console.log("handleResult: populating single movie table from resultData");

    // Populate the movie table
    // Find the empty table body by id "movie-table-body"
    let singleMovieTableBodyElement = jQuery("#single-movie-table-body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < Math.min(1, resultData.length); i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";

        let genrehrefs = genreListToHTML(resultData[i]["movie_genres"]);
        rowHTML += "<th>" + genrehrefs + "</th>";

        let starhrefs = starIDListToHTML(resultData[i]["movie_star_ids"], resultData[i]["movie_stars"]);
        rowHTML += "<th>" + starhrefs + "</th>";

        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";

        let movieEncoded = encodeURIComponent(resultData[i]["movie_title"]);

        rowHTML += "<th><button onclick=addToCart('" +
            movieEncoded + "','" + resultData[i]['movie_id'] + "')>Add to Cart</button></th>";

        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        singleMovieTableBodyElement.append(rowHTML);
    }
}

function addToCart(movie_title, movieId) {
    console.log("Adding " + movie_title + " to cart")
    $.ajax("api/cart", {
        method: "POST",
        data: "item=" + movie_title + "&id=" + movieId
    });
    window.alert(decodeURIComponent(movie_title) + " Added to Cart");
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by MoviesServlet in Movies.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleMovieServlet
});