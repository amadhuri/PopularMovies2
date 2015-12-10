package com.example.android.popularmovies2;

/**
 * Created by Madhuri on 11/19/2015.
 */
public interface OnMovieClickListener {
    void onMovieClick(int movieId, int position);
    void onFavoriteDeleted(int movieId, int position);
}
