import java.util.ArrayList;

public class Movie {

    public final String id;
    public final String title;
    public String director;
    public int year;
    public ArrayList<Genre> genres;

    public Movie(String id, String title, String director, int year, ArrayList<Genre> genres) {
        this.id = id;
        this.title = title;
        this.director = director;
        this.year = year;
        this.genres = genres;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public int getYear() {
        return year;
    }

    public ArrayList<Genre> getGenres() {
        return genres;
    }
}