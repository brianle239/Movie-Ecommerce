package edu.uci.ics.fabflixmobile.ui.movielist;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import edu.uci.ics.fabflixmobile.databinding.ActivityMovielistBinding;
import edu.uci.ics.fabflixmobile.ui.singleMovie.SingleMovieActivity;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.android.volley.RequestQueue;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
public class MovieListActivity extends AppCompatActivity {
    private final String host = "10.0.2.2";
    private final String port = "8080";
    private final String domain = "fabflix_war";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain;
    private int currentPage = 1;
    private final int pageSize = 10;
    private int totalNumMovies = 0;
    private ArrayList<Movie> movies;
    private MovieListViewAdapter adapter;
    private ListView listView;
    private Button prevButton;
    private Button nextButton;


    // guylee@hanmail.net
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);
        ActivityMovielistBinding binding;
        binding = ActivityMovielistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String movieName = getIntent().getStringExtra("movie_name");
        movies = new ArrayList<>();
        adapter = new MovieListViewAdapter(this, movies);
        listView = findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Movie movie = movies.get(position);
            Intent singleMovieIntent = new Intent(MovieListActivity.this, SingleMovieActivity.class);
            singleMovieIntent.putExtra("movie", movie);
            startActivity(singleMovieIntent);
            //@SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getName(), movie.getYear());
            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();


        });

        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        prevButton.setEnabled(false);

        prevButton.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                fetchMovies(currentPage, movieName);
            }
        });

        nextButton.setOnClickListener(v -> {
            if ((currentPage - 1) * pageSize < totalNumMovies) {
                currentPage++;
                fetchMovies(currentPage, movieName);
            }
        });

        fetchMovies(currentPage, movieName);

    }
    private void fetchMovies(int page, String name) {
        int offset = (page - 1) * pageSize;
    ;
        String url = baseURL + "/api/movies?amt="+ pageSize + "&sort=null&offset="+ offset +"&genreId=null&titleChar=null&movie_name="+name+"&year=null&director=null&star_name=null";
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray moviesJsonArray = new JSONArray(response);
                        if (moviesJsonArray.length() > 0) {
                            totalNumMovies = moviesJsonArray.getJSONObject(0).getInt("total_rows");
                        }
                        ArrayList<Movie> newMovies = new ArrayList<>();
                        for (int i = 0; i < moviesJsonArray.length(); i++) {
                            JSONObject movieJson = moviesJsonArray.getJSONObject(i);

                            String mname = movieJson.getString("movie_title");
                            short year = (short) movieJson.getInt("movie_year");
                            String director = movieJson.getString("movie_director");
                            String movieStars = movieJson.getString("movie_stars");
                            String[] starsArray = movieStars.split("\t");
                            List<String> starsList = new ArrayList<>(Arrays.asList(starsArray));
                            String movieGenres = movieJson.getString("movie_genres");
                            String[] genresArray = movieGenres.split("\t");
                            List<String> genreList = new ArrayList<>(Arrays.asList(genresArray));

                            newMovies.add(new Movie(mname, year, director, starsList, genreList));
                        }

                        movies.clear();
                        movies.addAll(newMovies);
                        adapter.notifyDataSetChanged();

                        updateButton();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                });


        queue.add(stringRequest);
    }
    private void updateButton() {
        prevButton.setEnabled(currentPage > 1);
        nextButton.setEnabled((currentPage * pageSize) < totalNumMovies);
    }



}



