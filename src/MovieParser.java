import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MovieParser {

    HashMap<String, Movie> movies;
    Document dom;
    Integer total_parsed = 0;
    DatabaseIds database_ids;

    public MovieParser() {
        this.movies = new HashMap<>();
        this.database_ids = new DatabaseIds();
    }

    public void runParser() {

        parseXmlFile();

        parseDocument();

        //printData();

        //writeToFile("movies_data.csv");

    }

    private void parseXmlFile() {
        // get the factory
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {

            // using factory get an instance of document builder
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            dom = documentBuilder.parse("stanford-movies/mains243.xml");
        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }

    private void parseDocument() {
        Element documentElement = dom.getDocumentElement();

        NodeList nodeList = documentElement.getElementsByTagName("directorfilms");
        //System.out.println("nodeList len: " + nodeList.getLength());
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            NodeList film_NodeList = element.getElementsByTagName("film");
            for (int j = 0; j < film_NodeList.getLength(); j++) {
                Element film = (Element) film_NodeList.item(j);
                parseMovie(film);
            }
        }
        total_parsed = movies.size();
    }


    private void parseMovie(Element element) {
        String id = getTextValue(element, "fid");
        String title = getTextValue(element, "t");
        Integer year = null;
        String director = getTextValue(element, "dirn");
        try {
            year = getIntValue(element, "year");
        } catch (NumberFormatException e) {
            System.out.println("Inconsistency: Movie Year: " + getTextValue(element, "year"));
        }

        ArrayList<Genre> genres = new ArrayList<>();

        NodeList cat_NodeList = element.getElementsByTagName("cat");
        for (int j = 0; j < cat_NodeList.getLength(); j++) {
            Element genreElement = (Element) cat_NodeList.item(j);
            String genreText = genreElement.getTextContent();
            int genreId = -1;

            // Check if the genre text is in the database_ids.genreTable
            if (database_ids.genreTable.containsKey(genreText)) {
                // If the genre text is in the genreTable, get the genre ID
                genreId = database_ids.genreTable.get(genreText);
            } else {
                // If the genre text is not in the genreTable, add it and get the next genre ID
                genreId = database_ids.getNextGenreId();
                database_ids.genreTable.put(genreText, genreId);
            }

            // Add the genre to the genres list
            if (genreText !=null && !genreText.isEmpty()) {
                genres.add(new Genre(genreId, genreText));
            }

        }


        if (id != null && !id.trim().isEmpty() && title != null && !title.isEmpty() && director != null && !director.isEmpty() && year != null) {
            movies.put(id, new Movie(id, title, director, year, genres));
            //total_parsed++;
        } else {
            System.out.println("Inconsistency: One or More Null Values in Movie");
        }
    }

    private String getTextValue(Element element, String tagName) {
        String textVal = null;
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            // here we expect only one <Name> would present in the <Employee>
            textVal = nodeList.item(0).getTextContent();

        }
        return textVal;
    }

    /**
     * Calls getTextValue and returns a int value
     */
    private int getIntValue(Element ele, String tagName) {
        // in production application you would catch the exception
        return Integer.parseInt(getTextValue(ele, tagName));
    }

    private void printData() {

        System.out.println("Total parsed " + movies.size() + " movies");

        for (Map.Entry<String, Movie> entry : movies.entrySet()) {
            String key = entry.getKey();
            Movie movie = entry.getValue();
            System.out.println(key + ": "  + movie.getId() + ", " + movie.getTitle() + ", " + movie.getDirector() + ", " + movie.getYear() + ", " + movie.getGenres());
        }
    }

    public void writeToFile(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (Map.Entry<String, Movie> entry : movies.entrySet()) {
                Movie movie = entry.getValue();
                writer.append(String.join("|", movie.getId(), movie.getTitle(), String.valueOf(movie.getYear()), movie.getDirector()));
                writer.append('\n');
            }
            //System.out.println("Movie data has been written to file: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        // create an instance
        MovieParser Movie_Parser = new MovieParser();

        // call run example
        Movie_Parser.runParser();
    }

}