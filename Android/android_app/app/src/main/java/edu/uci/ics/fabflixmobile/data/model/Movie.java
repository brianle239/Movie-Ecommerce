package edu.uci.ics.fabflixmobile.data.model;
import java.io.Serializable;



import java.util.List;

/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
public class Movie implements Serializable {
    public final String name;
    public final short year;
    private String director;
    private List<String> genres;
    private List<String> stars;

    public Movie(String name, short year, String director, List<String> stars, List<String> genres) {
        this.name = name;
        this.year = year;
        this.director = director;
        this.stars = stars;
        this.genres = genres;
    }

    public String getName() {
        return name;
    }

    public short getYear() {
        return year;
    }
    public String getDirector() { return director; }
    public List<String> getAllGenres() { return genres; }
    public List<String> getGenres() { return genres.size() > 3 ? genres.subList(0, 3) : genres;}
    public List<String> getAllStars() { return stars; }
    public List<String> getStars() { return stars.size() > 3 ? stars.subList(0, 3) : stars; }
}