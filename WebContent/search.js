/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */

let rsData;

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

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating movie table from resultData");

    // Populate the movie table
    // Find the empty table body by id "star_table_body"
    let movieTableBodyElement = jQuery("#search-table-body");


    // Iterate through resultData, no more than 20 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {


        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-movie.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +     // display movie_title for the link text
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_genres"] + "</th>";


        let starhrefs = starIDListToHTML(resultData[i]["movie_star_ids"], resultData[i]["movie_stars"]);

        rowHTML += "<th>" + starhrefs + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

/**
 * Sorting Buttons along with their EventListeners
 */

let sortByTitleBtnCount = 1; // Determines sort order (ASC, DESC), default ASC
let sortByRatingBtnCount = 1; // Determines sort order (ASC, DESC), default DESC

const sortByTitleBtn = document.querySelector("#sort-title-btn"); // Gets the sort by title button
const sortByRatingBtn = document.querySelector("#sort-rating-btn"); // Gets the sort by rating button

const titleBtnArrow = document.querySelector(".title-arrow"); // Gets the title button arrow
const ratingBtnArrow = document.querySelector(".rating-arrow"); // Gets the rating button arrow
sortByTitleBtn.addEventListener("click", (e) => {
    ratingBtnArrow.style.display = "none";
    titleBtnArrow.style.display = "inline-block";

    sortByTitleBtnCount === 1 ?
        titleBtnArrow.style.transform = "rotate(-135deg)": // DESC on next click
        titleBtnArrow.style.transform = "rotate(45deg)"; // ASC on next click

    // Empty the table body
    $("#search-table-body").empty();

    // Sort rsData (title, rating)
    rsData.sort((a, b) => {
        const titleA = a.movie_title.toUpperCase();
        const titleB = b.movie_title.toUpperCase();
        if (titleA < titleB) {
            return -1*sortByTitleBtnCount;
        } else if (titleA > titleB) {
            return 1*sortByTitleBtnCount;
        } else {
            // If titles are the same, compare ratings
            const ratingA = parseFloat(a.movie_rating);
            const ratingB = parseFloat(b.movie_rating);
            return (ratingB - ratingA)*sortByTitleBtnCount;
        }
    });

    // Flip order for next click
    sortByTitleBtnCount *= -1;

    // Repopulate the table with sorted data
    handleMovieResult(rsData);
});

sortByRatingBtn.addEventListener("click", (e) => {
    titleBtnArrow.style.display = "none";
    ratingBtnArrow.style.display = "inline-block";

    sortByRatingBtnCount === 1 ?
        ratingBtnArrow.style.transform = "rotate(-135deg)": // DESC on next click
        ratingBtnArrow.style.transform = "rotate(45deg)"; // ASC on next click

    // Empty the table body
    $("#search-table-body").empty();

    // Sort rsData (rating, title)
    rsData.sort((a, b) => {
        // Compare ratings first
        const ratingA = parseFloat(a.movie_rating);
        const ratingB = parseFloat(b.movie_rating);
        if (ratingA !== ratingB) {
            return (ratingB - ratingA)*sortByRatingBtnCount;
        } else {
            // If ratings are the same, compare titles
            const titleA = a.movie_title.toUpperCase();
            const titleB = b.movie_title.toUpperCase();
            if (titleA < titleB) {
                return -1*sortByRatingBtnCount;
            } else if (titleA > titleB) {
                return 1*sortByRatingBtnCount;
            }

            return 0; // Titles are equal
        }
    });

    // Flip order for next click
    sortByRatingBtnCount *= -1;

    // Repopulate the table with sorted data
    handleMovieResult(rsData);
});

let pTitle = getParameterByName('title');
let pYear = getParameterByName('year');
let pDirector = getParameterByName('director');
let pStar = getParameterByName('star');

let title = encodeURIComponent(pTitle);
let year = encodeURIComponent(pYear);
let director = encodeURIComponent(pDirector);
let star = encodeURIComponent(pStar);

if(!pTitle)
    pTitle="N/A";
if(!pYear)
    pYear="N/A";
if(!pDirector)
    pDirector="N/A";
if(!pStar)
    pStar="N/A";

/**
 * Search Header
 */
// populate the movie info h3
// find the empty h3 body by id "movie-info"
let movieInfoElement = jQuery("#movie-info");

let pString = "<p><b>Search: </b><small>Title: " + pTitle + ", Release Year: " + pYear + ", Director: " + pDirector + ", Star Name: " + pStar +"</small></p>";

// append two html <p> created to the h3 body, which will refresh the page
movieInfoElement.append(pString);

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/search?title=" + title + "&year=" + year + "&director=" + director + "&star=" + star, // Setting request url, which is mapped by MoviesServlet in Movie.java
    success: (resultData) => {rsData = resultData; handleMovieResult(resultData);} // Setting callback function to handle data returned successfully by the StarsServlet
});