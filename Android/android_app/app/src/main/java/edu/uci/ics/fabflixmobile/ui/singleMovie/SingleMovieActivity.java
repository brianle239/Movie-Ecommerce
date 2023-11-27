package edu.uci.ics.fabflixmobile.ui.singleMovie;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import edu.uci.ics.fabflixmobile.R;

import edu.uci.ics.fabflixmobile.data.model.Movie;;
import android.widget.TextView;

public class SingleMovieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlemovie);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        Movie movie = (Movie) getIntent().getSerializableExtra("movie");


        if (movie != null) {
            TextView titleTextView = findViewById(R.id.title);
            TextView yearTextView = findViewById(R.id.year);
            TextView directorTextView = findViewById(R.id.director);
            TextView genresTextView = findViewById(R.id.genres);
            TextView starsTextView = findViewById(R.id.stars);

            titleTextView.setText(movie.getName());
            yearTextView.setText(String.valueOf(movie.getYear()));
            directorTextView.setText(movie.getDirector());
            genresTextView.setText(TextUtils.join(", ", movie.getAllGenres()));
            starsTextView.setText(TextUtils.join(", ", movie.getAllStars()));

        } else {

            finish();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
