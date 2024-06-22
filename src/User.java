/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */
public class User {

    private final String username;
    private final int customerID;

    public User(String username, int customerID) {
        this.username = username;
        this.customerID = customerID;
    }

    public User(String username) {
        this(username, -1); // Default value for customerID is null
    }

    public int getId() {
        return customerID;
    }

    // Getter for username
    public String getUsername() {
        return username;
    }
}
