package edu.uci.ics.fabflixmobile.ui.mainSearch;

import android.os.Bundle;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;
import android.widget.Button;
import android.widget.EditText;
import edu.uci.ics.fabflixmobile.databinding.ActivitySearchBinding;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

public class SearchActivity extends AppCompatActivity {
    private final String host = "18.219.176.42";
    private final String port = "8443";
    private final String domain = "fabflix";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;
    private EditText movieSearch;
    // guylee@hanmail.net
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ActivitySearchBinding binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        movieSearch = binding.searchMovie;
        final Button searchButton = binding.search;
        searchButton.setOnClickListener(view -> search());
    }

    public void search() {
        final String movieName = movieSearch.getText().toString();
        Intent resultIntent = new Intent(SearchActivity.this, MovieListActivity.class);
        resultIntent.putExtra("movie_name", movieName);
        startActivity(resultIntent);


    }

}
