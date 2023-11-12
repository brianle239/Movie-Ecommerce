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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Random;


public class MovieParser extends DefaultHandler {

    public HashMap<String, Movie> movieDict;

    private String tempVal;
    private String tempDirector;

    //to maintain context
    private Movie tempMovie;
    private Boolean inconsistent;

    private DataSource dataSource;

    private Connection connection;
    HashMap<String, Integer> genreDict;

    private int iNoDirector;
    private int iNoYear;
    private int iNoId;
    private int iCastNoMovie;
    private int iInvalidMidCast;

    private HashMap<String, Star> stars;


    public MovieParser() {
        movieDict = new HashMap<String, Movie>();
        genreDict = new HashMap<String, Integer>();
        stars = new HashMap<String, Star>();
        inconsistent = false;
        iNoDirector = 0;
        iNoId = 0;
        iNoYear = 0;
        iCastNoMovie = 0;
        iInvalidMidCast = 0;

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

    public void runGenreInsert() {
        try {
            if (connection != null) {

                Statement select = connection.createStatement();
                String query = "Select * From genres;";
                ResultSet rs = select.executeQuery(query);
                while (rs.next()) {
                    genreDict.put(rs.getString("name"), rs.getInt("id"));
                }

                int count = genreDict.size()+1;
                query = "Insert Into genres Values ";

                for (String g: Movie.genreSet) {
                    if (genreDict.get(g) == null) {
                        genreDict.put(g, count);
                        query += "(" + count + ", \"" + g + "\"),\n";
                        count += 1;
                    }
                }
//                System.out.println(query.substring(0, query.length()-2)+";");
                select.executeUpdate(query.substring(0, query.length()-2)+";");

            }
        }
        catch (Exception e) {
            System.out.println("Error: "+ e);
        }

    }
    private void runInsert() {
        try {
            if (connection != null) {
                System.out.println("Connection established!!");
                System.out.println();

                Statement select = connection.createStatement();
                String query = "Insert Into movies\n" +
                        "Values ";
                String queryGM = "Insert Into genres_in_movies\n" +
                        "Values ";
                for (HashMap.Entry<String, Movie> set : movieDict.entrySet()) {

                    // Printing all elements of a Map
                    query += set.getValue().insertFormat() + ",\n";
                    for (String g :  set.getValue().getGenres()) {
                        queryGM += "(" + genreDict.get(g) + ", \"" + set.getValue().getId() + "\"),\n";
                    }


                }
//                System.out.println(query.substring(0, query.length()-2));
                select.executeUpdate(query.substring(0, query.length()-2)+";");
                select.executeUpdate(queryGM.substring(0, queryGM.length()-2)+";");

            }
        }
        catch (Exception e) {
            System.out.println("Error: "+ e);
        }


    }
    private void runStarInsert() {
        try {
            if (connection != null) {
                Statement select = connection.createStatement();
                String query = "Insert Into stars\n" +
                        "Values ";
                String querySM = "Insert Into stars_in_movies\n" +
                        "Values ";
                for (HashMap.Entry<String, Star> set : stars.entrySet()) {

                    // Printing all elements of a Map
                    query += set.getValue().insertFormat() + ",\n";
                    if (set.getValue().getMovies().size() == 0) {
                        iCastNoMovie += 1;
                    }
                    for (String g :  set.getValue().getMovies()) {
                        if (movieDict.get(g) != null) {
                            querySM += "(\"" + set.getValue().getId() + "\", \"" + g + "\"),\n";
                        }
                        else {
                            iInvalidMidCast += 1;
                        }
                    }

                }
//                System.out.println(query);
                select.executeUpdate(query.substring(0, query.length()-2)+";");
                select.executeUpdate(querySM.substring(0, querySM.length()-2)+";");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    private void printIncon() {
        System.out.println("No Director: " + iNoDirector);
        System.out.println("No FID: " + iNoId);
        System.out.println("Incorrect format Year: " + iNoYear);
        System.out.println("Cast not in any movie: " + iCastNoMovie);
        System.out.println("Cast Movie ID not found: " + iInvalidMidCast);

    }
    public void runExample() {

        long startTime = System.currentTimeMillis();

        parseDocument();
        runGenreInsert();
        runInsert();
        StarParser spx = new StarParser();
        spx.runExampleStar();
        stars = spx.getStarDict();
        runStarInsert();

        long estimatedTime = System.currentTimeMillis() - startTime;
        printIncon();
        spx.printInconStars();
        System.out.println("TIME: " + estimatedTime);

        //printData();
        // tempMovie.printSet();
        //tempMovie.printMap();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("cs122b-parser/mains243.xml", this);

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


        for (HashMap.Entry<String, Movie> set :
                movieDict.entrySet()) {

            // Printing all elements of a Map
            System.out.println(set.getKey() + " = "
                    + set.getValue());
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset

        tempVal = "";
        if (qName.equalsIgnoreCase("film")) {
            //create a new instance of employee
            tempMovie = new Movie();
            inconsistent = false;
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("film")) {
            //add it to the list

            tempMovie.setDirector(tempDirector);
            if (tempDirector.trim().isEmpty()) {
                iNoDirector += 1;
                inconsistent = true;
            }
            if (!inconsistent) {
                if (movieDict.get(tempMovie.getId()) == null) {
                    movieDict.put(tempMovie.getId(), tempMovie);
                }

            }
//            else {
//                System.out.println(tempMovie.toString());
//            }

        } else if (qName.equalsIgnoreCase("t")) {
            tempMovie.setTitle(tempVal);
        } else if (qName.equalsIgnoreCase("fid")) {

            if (!tempVal.trim().isEmpty()) {
                tempMovie.setId(tempVal);
            }
            else {
//                System.out.println("Empty FID: "+ tempVal);
                iNoId += 1;
                inconsistent = true;
            }

        } else if (qName.equalsIgnoreCase("year")) {
            try {
                tempMovie.setYear(Integer.parseInt(tempVal));
            }
            catch(Exception e) {
//                System.out.println("Year not int: "+ tempVal);
                iNoYear += 1;
                inconsistent = true;
            }

        } else if (qName.equalsIgnoreCase("dirName")) {
            tempDirector = tempVal.trim();
        } else if (qName.equalsIgnoreCase("cat")) {
            tempMovie.appendGenre(tempVal);
        }
        else if (qName.equalsIgnoreCase("directorfilms")) {
            tempDirector = "";
        }



    }

    public static void main(String[] args) {
        MovieParser spe = new MovieParser();
        spe.runExample();
        ExecutorService executor = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 6; i++) {
            QueryWorker worker = new QueryWorker(i);
            executor.execute(worker);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {}

    }
    static class QueryWorker implements Runnable {
        Random random;
        String connection;
        String query;

        QueryWorker(int i) {
            random = new Random(i);
            connection = "New connection " + i;
            System.out.println(connection);
            query = "SELECT * FROM Foo WHERE id = " + i;
        }

        @Override
        public void run() {
            System.out.println(String.format("Executing query: %s", query));
            try {
                Thread.sleep(random.nextInt(5000));
            } catch (InterruptedException e) { e.printStackTrace();}
        }
    }

}




