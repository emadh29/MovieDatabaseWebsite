let add_star_info = $("#add_star_info");

function handleStar(result) {
    add_star_info[0].reset();

    console.log("handle star");
    console.log(result);

    let star_message = $("#add_star_message");
    let text_popup = $('<p>').text(`Success! starID: ${result["star_id"]}`);
    star_message.append(text_popup);
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitStarForm(formSubmitEvent) {
    console.log("adding star");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    jQuery.ajax({
        dataType: "json",
        method: "POST",
        url: "api/add-star",
        data: add_star_info.serialize(),
        success: (resultData) => handleStar(resultData)
    });
}

// Bind the submit action of the form to a handler function
add_star_info.submit(submitStarForm);

