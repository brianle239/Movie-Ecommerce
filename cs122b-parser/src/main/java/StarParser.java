import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.HashMap;
import java.util.ArrayList;

public class StarParser extends DefaultHandler {

    static int count = 0;
    private String tempVal;

    //to maintain context
    private Star tempStar;
    private String tempName;
    private String tempFid;
    private Boolean inconsistent;

    private Connection connection;
    private HashMap<String, Star> starDict; // Key is name + year

    private int iNoStarMovie;
    private int iDupe;
    private int iNoYear;
    private int iNoDob;

    private int iInvalidCast;
    private int iSA;




    public HashMap<String,Star> getStarDict() {
        return starDict;
    }

    public StarParser() {
        starDict = new HashMap<String, Star>();

        inconsistent = false;
        iNoStarMovie = 0;
        iNoYear = 0;
        iDupe = 0;
        iNoDob = 0;
        iNoStarMovie = 0;
        iInvalidCast = 0;
        iSA = 0;

//        Class.forName("com.mysql.cj.jdbc.Driver");

        // Connect to the test database
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:" + "mysql" + ":///" + "moviedbexample" + "?autoReconnect=true&useSSL=false",
                    "mytestuser", "milk");

        }
        catch (Exception e) {
            System.out.println("Error" + e);
        }

    }

    public void populateStar() {
        try {
            if (connection != null) {

                Statement select = connection.createStatement();
                String query = "SELECT * FROM STARS;";
            }
        }
        catch (Exception e) {
            System.out.println("Error: " + e);
        }

    }

    public void printInconStars() {
        System.out.println("Star name in Cast not found Actors: " + iNoStarMovie);
        System.out.println("Star name unkown but improtant (sa) " + iSA);
        System.out.println("Invalid Cast Formating: " + iInvalidCast);
        System.out.println("Incorrect format Year: " + iNoYear);
        System.out.println("Dupe stars: " + iDupe);
        System.out.println("No DOB for stars: " + iNoDob);
    }
    public void runExampleStar() {
        parseDocument("cs122b-parser/actors63.xml");
        parseDocument("cs122b-parser/casts124.xml");
    }

    private void parseDocument(String file) {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse(file, this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData() {
        for (HashMap.Entry<String, Star> set :
                starDict.entrySet()) {
            // Printing all elements of a Map
            System.out.println(set.getKey() + " = "
                    + set.getValue());
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset

        tempVal = "";
        if (qName.equalsIgnoreCase("actor")) {
            //create a new instance of employee
            tempStar = new Star();
            inconsistent = false;
        } else if (qName.equalsIgnoreCase("m")) {
            tempName = "";
            tempFid = "";
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("actor")) {
            //add it to the list
            if (!inconsistent) {
                String key = tempStar.getName(); //+ ":" + tempStar.getYear()
                tempStar.setId("sn"+count);
                count += 1;
                if (starDict.get(key) == null) {
                    starDict.put(key, tempStar);
                }
                else {
                    iDupe += 1;
                }
            }
            tempVal = "";

        } else if (qName.equalsIgnoreCase("stagename")) {
            tempStar.setName(tempVal.trim());
        }
        else if (qName.equalsIgnoreCase("dob")) {
            if (tempVal.trim().isEmpty()) {
                iNoDob += 1;
            }
            else {
                try {
                    tempStar.setYear(Integer.parseInt(tempVal));
                }
                catch(Exception e) {
//                System.out.println("Year not int: "+ tempVal);
                    iNoYear += 1;
                    inconsistent = true;
                }
            }

        } else if (qName.equalsIgnoreCase("f")) {
            tempFid = tempVal.trim();

        }
        else if (qName.equalsIgnoreCase("m")) {
            if (tempFid.trim().isEmpty() || tempName.trim().isEmpty()) {
                iInvalidCast += 1;
            }
            else if (tempName.equals("sa")) {
                iSA += 1;
            }
            else if (starDict.get(tempName) != null) {
                Star s =  starDict.get(tempName);
                s.appendMovies(tempFid);

            }
            else {
                iNoStarMovie += 1;
            }

        } else if (qName.equalsIgnoreCase("a")) {
            tempName = tempVal.trim();
        }



    }

//    public static void main(String[] args) {
//        StarParser spe = new StarParser();
//        spe.runExampleStar();
//    }

}
