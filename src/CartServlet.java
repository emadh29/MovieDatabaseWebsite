import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Random;
import java.util.Date;

/**
 * This CartServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/cart.
 */
@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {

    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        long lastAccessTime = session.getLastAccessedTime();

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("sessionID", sessionId);
        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());

        HashMap<String, MovieClass> previousItems = (HashMap<String, MovieClass>) session.getAttribute("previousItems");
        if (previousItems == null) {
            previousItems = new HashMap<String, MovieClass>();
        }
        // Log to localhost log
        request.getServletContext().log("getting " + previousItems.size() + " items");
        JsonArray previousItemsJsonArray = new JsonArray();
        int sale_flag = -1;
        for (MovieClass movie : previousItems.values()) {
            JsonObject jsonObject = new JsonObject();
            sale_flag = movie.getSaleFlag();
            jsonObject.addProperty("title", movie.getTitle());
            jsonObject.addProperty("quantity", movie.getQuantity());
            jsonObject.addProperty("price", movie.getPrice());
            jsonObject.addProperty("sale_flag", sale_flag);
            previousItemsJsonArray.add(jsonObject);
        }
        responseJsonObject.add("previousItems", previousItemsJsonArray);

        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());

        //If the sale has been made, we clear the array for the next cart
        if (sale_flag != -1) {
            previousItems.clear();
        }
    }

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String movie_name = request.getParameter("item");
        String type = request.getParameter("type");
        String movieId = request.getParameter("id");
        System.out.println(movie_name);
        HttpSession session = request.getSession();

        // get the previous items in a ArrayList
        HashMap<String, MovieClass> previousItems = (HashMap<String, MovieClass>) session.getAttribute("previousItems");
        if (previousItems == null) {
            previousItems = new HashMap<String, MovieClass>();
            Random rand = new Random();
            previousItems.put(movie_name, new MovieClass(movie_name, 1, rand.nextInt(40), movieId));
            session.setAttribute("previousItems", previousItems);
        } else {
            // prevent corrupted states through sharing under multi-threads
            // will only be executed by one thread at a time
            synchronized (previousItems) {
                //hashmap allows for easy deletion
                 if (previousItems.containsKey(movie_name) && "delete_movie".equals(type)) {
                    previousItems.remove(movie_name);
                } else if (previousItems.containsKey(movie_name) && "decrement_quantity".equals(type)) {
                    if (previousItems.get(movie_name).quantity != 1) {
                        previousItems.get(movie_name).quantity--;
                    }
                } else if (previousItems.containsKey(movie_name)) {
                    previousItems.get(movie_name).quantity++;
                }else {
                    Random rand = new Random();
                    previousItems.put(movie_name, new MovieClass(movie_name, 1, rand.nextInt(40), movieId));
                }
            }
        }

        JsonObject responseJsonObject = new JsonObject();

        JsonArray previousItemsJsonArray = new JsonArray();
        for (MovieClass movie : previousItems.values()) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("title", movie.getTitle());
            jsonObject.addProperty("quantity", movie.getQuantity());
            jsonObject.addProperty("price", movie.getPrice());
            jsonObject.addProperty("sale_flag", movie.getSaleFlag());
            previousItemsJsonArray.add(jsonObject);
        }
        responseJsonObject.add("previousItems", previousItemsJsonArray);

        response.getWriter().write(responseJsonObject.toString());
    }
}