function handleSessionData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle session response");
    console.log(resultDataJson);
    console.log(resultDataJson["sessionID"]);

    $("#sessionID").text("Session ID: " + resultDataJson["sessionID"]);
    $("#lastAccessTime").text("Last access time: " + resultDataJson["lastAccessTime"]);

    handleConfirmationArray(resultDataJson["previousItems"]);
}

function handleConfirmationArray(resultArray) {
    console.log(resultArray);
    let confirmation_table_body = $("#confirmation_table_body");
    confirmation_table_body.empty();
    // change it to html list
    let rowHTML = "";
    let total_price = 0;
    for (let i = 0; i < resultArray.length; i++) {
        total_price += (resultArray[i]["quantity"]*resultArray[i]["price"]);
        rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultArray[i]["sale_flag"] + "</th>";
        rowHTML += "<th>" + resultArray[i]["title"] + "</th>";
        rowHTML += "<th>" + resultArray[i]["quantity"] + "</th>";
        rowHTML += "<th>$" + resultArray[i]["price"] + "</th>";
        rowHTML += "<th>$" + (resultArray[i]["quantity"]*resultArray[i]["price"]) + "</th>";
        rowHTML += "</tr>";
        confirmation_table_body.append(rowHTML);
    }
    rowHTML = "<tr>";
    rowHTML += "<th></th>";
    rowHTML += "<th></th>";
    rowHTML += "<th></th>";
    rowHTML += "<th></th>";
    rowHTML += `<th class="table-success">Total Cost: $${total_price}</th>`;
    rowHTML += "</tr>";
    confirmation_table_body.append(rowHTML);
}

$.ajax("api/cart", {
    method: "GET",
    success: handleSessionData
});