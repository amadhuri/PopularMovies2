package com.example.android.popularmovies2;

import android.provider.BaseColumns;

/**
 * Created by Madhuri on 12/3/2015.
 */
public class FavoritesMovieContract  {

    private FavoritesMovieContract() {

    }

    /* Inner class that defines table contents */
    public static abstract class FavoriteMoviesColumn implements BaseColumns {

        public static final String TABLE_FAVORITES = "favorites_table";
        public static final String COL_MOVIE_ID = "movie_id";
        public static final String COL_MOVIE_TITLE = "movie_title";
        public static final String COL_MOVIE_OVERVIEW = "movie_overview";
        public static final String COL_MOVIE_YEAR = "movie_year";
        public static final String COL_MOVIE_RUNTIME = "movie_runtime";
        public static final String COL_MOVIE_RATING = "movie_rating";
        public static final String COL_MOVIE_TRAILERS = "movie_trailers";
        public static final String COL_MOVIE_REVIEWS = "movie_reviews";
        public static final String COL_MOVIE_POSTER_FILE = "movie_poster";
    }
}
