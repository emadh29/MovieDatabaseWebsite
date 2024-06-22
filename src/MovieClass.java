//This is the class that describes a movie when it is in the cart.

public class MovieClass {

    public final String title;
    public int quantity;
    public int price;
    public String movieId;
    public int sale_flag;

    public MovieClass(String title, int quantity, int price, String movieId) {
        this.title = title;
        this.quantity = quantity;
        this.price = price;
        this.movieId = movieId;
        this.sale_flag=-1;
    }
    public String getTitle() {
        return title;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }

    public String getMovieId() {
        return movieId;
    }

    public int getSaleFlag() {
        return sale_flag;
    }
}