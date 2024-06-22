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

function handleMovieResult(resultData) {
    let genres = $("#genres");

    // Iterate through resultData
    for (let i = 0; i < resultData.length; i++) {
        // Concatenate the html tags with resultData jsonObject
        let HTML = "";
        HTML += genreListToHTML(resultData[i]["genre_name"]);

        // Append the row created to the table body, which will refresh the page
        genres.append(HTML);
    }
}

$.ajax({
    dataType: "json",
    method: "GET",
    url: "api/index",
    success: (resultData) => {
        handleMovieResult(resultData);
    }
});