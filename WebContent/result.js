/**
 * Helper Functions
 */

// Function to save values to sessionStorage
function saveToSessionStorage() {
    sessionStorage.setItem('url', url);
    sessionStorage.setItem('page', page.toString(10));
    sessionStorage.setItem('entries', entries.toString(10));
    sessionStorage.setItem('sortType', sortType);
    sessionStorage.setItem('rsData', JSON.stringify(rsData));

}

function getSessionStorage() {
    url = sessionStorage.getItem('url');
    page = parseInt(sessionStorage.getItem('page'));
    entries = parseInt(sessionStorage.getItem('entries'));
    sortType = sessionStorage.getItem('sortType');
    rsData = JSON.parse(sessionStorage.getItem('rsData'));

}

function parseURLParams(url) {
    // Extract the query string from the URL
    let queryString = url.split('?')[1];

    // Create a URLSearchParams object from the query string
    let params = new URLSearchParams(queryString);

    // Initialize an empty array to store the formatted parameter strings
    let paramStrings = [];

    // Iterate over the parameters and construct the formatted strings
    for (let [name, value] of params) {
        // Only include parameters with non-empty values
        if (value) {
            // Construct the parameter string in the format "param=value"
            let paramString = `${name}=${value}`;

            // Push the parameter string to the array
            paramStrings.push(paramString);
        }
    }

    // Join the array of parameter strings into a single string with commas
    return paramStrings.join(', ');
}

function getQueryString(url) {
    // Split the URL by '?'
    const parts = url.split('?');

    // Check if there's anything after '?'
    if (parts.length === 2) {
        // Return everything after '?'
        return parts[1];
    } else {
        // If there's no query string, return an empty string
        return '';
    }
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
 * Global Variables
 */

const referrer = document.referrer;

let url = window.location.href,
    page= 1,
    entries= 10,
    sortType= "rating_desc_title_asc",
    rsData = null;

/**
 * Return To Top of Page Button
 */

// Get the button element
let backToTopBtn = document.getElementById("back-to-top-btn");

function scrollFunction() {
    if (document.documentElement.scrollTop > 20) {
        backToTopBtn.style.display = "block";
    } else {
        backToTopBtn.style.display = "none";
    }
}

// When the user scrolls down 20px from the top of the document, show the button
window.addEventListener("scroll", scrollFunction);

// When the user clicks on the button, scroll to the top of the document
backToTopBtn.addEventListener("click", function() {
    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });
});

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleMovieResult(resultData) {
    let resultHeader = $("#results-header");
    let headerString = "<h3>Results: <small>" + parseURLParams(url) + "</small></h3>";

    resultHeader.empty();
    resultHeader.append(headerString);

    document.getElementById("entry-options").value = entries;
    document.getElementById("sorting-options").value = sortType;

    document.getElementById("display-box").textContent = page.toString();
    
    // Empty the table body
    $("#movie-table-body").empty();
    console.log("handleMovieResult: populating movie table from resultData");

    // Populate the movie table
    // Find the empty table body by id "star_table_body"
    let movieTableBodyElement = jQuery("#movie-table-body");

    // Iterate through resultData
    for (let i = 0; i < resultData.length; i++) {
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
        movieTableBodyElement.append(rowHTML);
    }
}

/**
 * Get Data From Servlet Based On PageNum
 */

function getData(pageNum, update= false) {
    if (update) {
        entries = document.getElementById("entry-options").value;
        sortType = document.getElementById("sorting-options").value;
    }

    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/result?" + getQueryString(url) + "&page=" + pageNum.toString() + "&entries=" + entries + "&sort=" + sortType,
        success: (resultData) => {
            page = pageNum;
            rsData = resultData;
            handleMovieResult(rsData);
            saveToSessionStorage();
        } // Setting callback function to handle data returned successfully by the StarsServlet
    });
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
 * Update (Page, Sort-by, & Data)
 */

document.getElementById("update").addEventListener("click", (e) => {
    getData(1,true);
});

/**
 * Prev & Next Buttons
 */

document.getElementById("prev-btn").addEventListener("click", (e) => {
    if (page>1) {
        getData(page - 1);
        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });
    }
})

document.getElementById("next-btn").addEventListener("click", (e) => {
    if(rsData.length >= entries) {
        getData(page + 1);
        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });
    }
})

/**
 * Handle Session Data or Get New Data
 */
if (referrer.includes('single-movie.html') || referrer.includes('single-star.html')) {
    getSessionStorage();
    handleMovieResult(rsData);
}
else {
    getData(page);
}




