let cart = $("#cart");

/**
 * Handle the data returned by CartServlet
 * @param resultDataString jsonObject, consists of session info
 */
function handleSessionData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle session response");
    console.log(resultDataJson);
    console.log(resultDataJson["sessionID"]);

    // show the session information 
    $("#sessionID").text("Session ID: " + resultDataJson["sessionID"]);
    $("#lastAccessTime").text("Last access time: " + resultDataJson["lastAccessTime"]);

    // show cart information
    handleCartArray(resultDataJson["previousItems"]);
}

/**
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */
function handleCartArray(resultArray) {
    console.log(resultArray);
    let cart_table_body = $("#cart_table_body");
    cart_table_body.empty();
    let rowHTML = "";
    let total_price = 0;
    for (let i = 0; i < resultArray.length; i++) {
        if (resultArray[i]["sale_flag"] === -1) {
            total_price += (resultArray[i]["price"]*resultArray[i]["quantity"]);
            rowHTML = "";
            rowHTML += "<tr>";
            rowHTML += "<th>" + resultArray[i]["title"] + "</th>";
            rowHTML +=
                "<th><button onclick='addToCart(\"" +
                resultArray[i]["title"] + "\",\"decrement_quantity\")'>-</button> " + resultArray[i]["quantity"]
                + " <button onclick='addToCart(\"" +
                resultArray[i]["title"] + "\",\"increment_quantity\")'>+</button></th>";
            rowHTML +=
                "<th><button onclick='addToCart(\""
                + resultArray[i]["title"] + "\",\"delete_movie\")'>Delete</button></th>";
            rowHTML += "<th>$" + resultArray[i]["price"] + "</th>";
            rowHTML += "<th>$" + (resultArray[i]["quantity"]*resultArray[i]["price"]) + "</th>";
            rowHTML += "</tr>";
            cart_table_body.append(rowHTML);
        }
    }
    //Checkout button
    rowHTML = "<tr>";
    rowHTML += "<th>";
    rowHTML += "<a href='checkout.html'>";
    rowHTML += "<button>Checkout</button>";
    rowHTML += "</a>";
    rowHTML += "</th>";
    rowHTML += "<th></th>";
    rowHTML += "<th></th>";
    rowHTML += "<th></th>";
    rowHTML += `<th class="table-success">Total Cost: $${total_price}</th>`;
    rowHTML += "</tr>";

    cart_table_body.append(rowHTML);
}

function addToCart(movie_title, type) {
    console.log("Added: " + movie_title + " to cart")
    $.ajax("api/cart", { // Setting request url, which is mapped by CartServlet
        method: "POST", // Setting request method
        data: "item=" + movie_title + "&type=" + type,
        success: resultDataString => { let resultDataJson = JSON.parse(resultDataString);
            handleCartArray(resultDataJson["previousItems"]);
        }
    });
}

$.ajax("api/cart", {
    method: "GET",
    success: handleSessionData
});

