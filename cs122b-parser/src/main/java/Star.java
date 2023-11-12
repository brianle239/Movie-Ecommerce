import java.sql.Array;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
public class Star {
    public static HashSet<String> genreSet = new HashSet<String>();
    private String id;
    private String name;

    private int year; // Default 0

    private HashSet<String> movies;


    public Star(){
        this.movies = new HashSet<String>();

    }

    public Star(String id, String name, int year) {
        this.id = id;
        this.name = name;
        this.year  = year;
        this.movies = new HashSet<String>();


    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name.replaceAll("\"", "\\\\\"");
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }

    public HashSet<String> getMovies() {return movies;};
    public void appendMovies(String movie)
    {
        if (!movie.isEmpty()) {
            movies.add(movie.trim());
        }
    }


    public String insertFormat() {
        if (year == 0) {
            return "(\"" + this.id + "\", \"" + name + "\", null)";
        }
        else {
            return "(\"" + this.id + "\", \"" + name + "\", " + year + ")";

        }
    }



    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Star Details - ");
        sb.append("Name:" + getName());
        sb.append(", ");
        sb.append("Id:" + getId());
        sb.append(", ");
        sb.append("Year:" + getYear());
        sb.append(", ");
        sb.append("Movies:" + getMovies());
        sb.append(". ");

        return sb.toString();
    }
}
