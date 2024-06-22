let login_form = $("#login_form");
let user = getParameterByName("user");

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

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle login response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        if (user === "employee") {
            window.location.replace("_dashboard/index.html");
        } else {
            window.location.replace("index.html");
        }
    } else {
        // If login fails, the web page will display 
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#login_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
    console.log("submit login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    if (user === "employee") {
        $.ajax(
            "api/login", {
                method: "POST",
                // Serialize the login form to the data sent by POST request
                data: login_form.serialize() + "&user=employee",
                success: handleLoginResult
            }
        );
    }
    else {
        $.ajax(
            "api/login", {
                method: "POST",
                // Serialize the login form to the data sent by POST request
                data: login_form.serialize() + "&user=customer",
                success: handleLoginResult
            }
        );
    }
}

// Bind the submit action of the form to a handler function
login_form.submit(submitLoginForm);

