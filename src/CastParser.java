import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CastParser {
    Document dom;
    HashMap<String, ArrayList<String>> starIDAndMovieID;
    Integer total_parsed = 0;
    DatabaseIds databaseIds = new DatabaseIds();


    public CastParser() {
        this.starIDAndMovieID = new HashMap<>();
    }

    public void runParser() {
        parseXmlFile();
        parseDocument();
        //printData();
    }

    private void parseXmlFile() {
        // get the factory
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {

            // using factory get an instance of document builder
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            dom = documentBuilder.parse("stanford-movies/casts124.xml");
        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }

    private void parseDocument() {
        // get the document root Element
        Element documentElement = dom.getDocumentElement();

        // get a nodelist of employee Elements, parse each into Employee object
        NodeList nodeList = documentElement.getElementsByTagName("m");
        //System.out.println("nodeList len: " + nodeList.getLength());

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            //setStarIDAndMovieID(parseCast(element));
            parseCast(element);
        }
        //System.out.println("Parsing Complete");
    }

    /*
    private HashMap<String, String> parseCast(Element element) {
        HashMap<String, String> starNameAndMovieID = new HashMap<>();

        String name = getTextValue(element, "a");
        String movieID = getTextValue(element, "f");

        if(name != null && !name.isEmpty() && movieID != null && !movieID.isEmpty())
            starNameAndMovieID.put(name, movieID);

        return starNameAndMovieID;
    } */

    private void parseCast(Element element) {
        String name = getTextValue(element, "a");
        String movieID = getTextValue(element, "f");

        if (name != null && !name.isEmpty() && movieID != null && !movieID.isEmpty()) {
            // Check if the name is already in the hashmap
            if (starIDAndMovieID.containsKey(movieID)) {
                // If the movieID already exists, retrieve the ArrayList and add the name to it
                ArrayList<String> namesList = starIDAndMovieID.get(movieID);
                namesList.add(name);
            } else {
                // If the movieID doesn't exist, create a new ArrayList and add the name to it
                ArrayList<String> namesList = new ArrayList<>();
                namesList.add(name);
                starIDAndMovieID.put(movieID, namesList);
            }
            total_parsed++;
        } else {
            System.out.println("Inconsistency: One or More Null Values for Movie or Star");
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

    private void printData() {
        int count = 0;
        for (Map.Entry<String, ArrayList<String>> entry : starIDAndMovieID.entrySet()) {
            String movieID = entry.getKey();
            ArrayList<String> starNames = entry.getValue();
            System.out.print(movieID + ": ");
            for (String starName : starNames) {
                System.out.print(starName + ", ");
                count++;
            }
            System.out.print("\n");
        }
        System.out.println("Total parsed " + count + " starIDs and movieIDs");
    }


    public static void main(String[] args) {
        CastParser CastParser = new CastParser();
        CastParser.runParser();
    }

}

/*
protected void setStarIDAndMovieID(HashMap<String, String> starNameAndMovieID) {
        String query = "SELECT id FROM stars WHERE name = ?";

        try(Connection conn = dataSource.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(query);
            for (Map.Entry<String,String> entry : starNameAndMovieID.entrySet()) { // <starName, movieID> -> <starID, movieID>
                statement.setString(1, entry.getKey());
                ResultSet rs = statement.executeQuery();

                while (rs.next()) {
                    starIDAndMovieID.put(rs.getString("id"), entry.getValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 */