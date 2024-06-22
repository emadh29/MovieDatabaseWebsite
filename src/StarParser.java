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
import java.util.List;
import java.util.Map;

public class StarParser {

    HashMap<String, Star> stars;
    Document dom;
    Integer total_parsed = 0;
    DatabaseIds databaseIds = new DatabaseIds();

    public StarParser() {
        this.stars = new HashMap<>();
    }

    public void runParser() {

        parseXmlFile();

        parseDocument();

        //printData();

        //printDataToFile("stars_data.csv");
    }

    private void parseXmlFile() {
        // get the factory
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {

            // using factory get an instance of document builder
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            dom = documentBuilder.parse("stanford-movies/actors63.xml");
        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }

    private void parseDocument() {
        // get the document root Element
        Element documentElement = dom.getDocumentElement();

        // get a nodelist of employee Elements, parse each into Employee object
        NodeList nodeList = documentElement.getElementsByTagName("actor");
        //System.out.println("nodeList len: " + nodeList.getLength());
        for (int i = 0; i < nodeList.getLength(); i++) {
            // get the employee element
            Element element = (Element) nodeList.item(i);

            // get the Employee object
            parseStar(element);

        }
        //System.out.println("Parsing Complete");
        total_parsed = stars.size();
    }

    /**
     * It takes an employee Element, reads the values in, creates
     * an Employee object for return
     */
    private void parseStar(Element element) {

        // for each <employee> element get text or int values of
        // name ,id, age and name
        String name = getTextValue(element, "stagename");
        Integer birthYear = null;
        String birthYearString = getTextValue(element, "dob");
        if (birthYearString != null && !birthYearString.isEmpty()) {
            try {
                birthYear = Integer.parseInt(birthYearString);

            } catch (NumberFormatException e) {
                System.out.println("Inconsistency: Star Birth Year: " + birthYearString);
            }

        }

        String id = databaseIds.getNextStarId();
        stars.put(name, new Star(id, name, birthYear));
    }

    /**
     * It takes an XML element and the tag name, look for the tag and get
     * the text content
     * i.e for <Employee><Name>John</Name></Employee> xml snippet if
     * the Element points to employee node and tagName is name it will return John
     */
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

    /**
     * Iterate through the list and print the
     * content to console
     */
    private void printData() {

        System.out.println("Total parsed " + stars.size() + " stars");

        for (Map.Entry<String, Star> entry : stars.entrySet()) {
            Star star = entry.getValue();
            String starName = entry.getKey();
            Integer birthYear = star.getBirthYear();
            String birthYearString = (birthYear != null) ? birthYear.toString() : "";
            System.out.println(starName + ": " + star.getId() + ", " + birthYearString + "\n");
        }
    }

    public static void main(String[] args) {
        // create an instance
        StarParser StarParser = new StarParser();

        // call run example
        StarParser.runParser();
    }


}