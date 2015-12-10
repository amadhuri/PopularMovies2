package com.example.android.popularmovies2;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;


/**
 * Created by Madhuri on 11/24/2015.
 */
public class MovieDetailData extends MovieData {

    public final String movieTitle;
    public final String movieOverview;
    public final String movieReleaseYear;
    public final Double vote_average;
    public final String movieRunTime;
    public final String TAG = MovieDetailData.class.getSimpleName();

    public MovieDetailData(int movieId, String moviePoster, String movieTitle, String movieOverview, String releaseYear, String runTime, Double vote_average) {
        super(moviePoster, movieId);

            this.movieTitle = movieTitle;
            this.movieOverview = movieOverview;
            this.movieRunTime = runTime;
            this.vote_average = vote_average;
            if(!releaseYear.isEmpty() && releaseYear.length() >= 4)
                this.movieReleaseYear = releaseYear.substring(0,4);
            else
                this.movieReleaseYear="";
    }

    public String getMovieTitle() { return this.movieTitle;}

    public String getMovieOverview() { return this.movieOverview;}

    public String getMoveReleaseYear() { return movieReleaseYear;}

    public String getMovieRunTime() { return this.movieRunTime;}

    public Double getVoteAverage() { return this.vote_average;}
}
