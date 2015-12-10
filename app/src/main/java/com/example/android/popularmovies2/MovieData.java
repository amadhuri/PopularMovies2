package com.example.android.popularmovies2;

/**
 * Created by Madhuri on 11/14/2015.
 */
public class MovieData {
    private final String moviePoster;
    private final int movieId;

    public MovieData(String moviePoster, int movieId) {
        this.moviePoster = moviePoster;
        this.movieId = movieId;
    }

    public String getMoviePoster() {
        return moviePoster;
    }

    public int getMovieId() {
        return movieId;
    }

}
