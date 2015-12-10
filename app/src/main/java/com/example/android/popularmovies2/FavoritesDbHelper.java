package com.example.android.popularmovies2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Madhuri on 12/2/2015.
 */
public class FavoritesDbHelper extends SQLiteOpenHelper {

    private static final String TAG = FavoritesDbHelper.class.getSimpleName();
    private static final int DB_VERSION = 3;
    private static final String DB_NAME = "open_movie_favorites.db";


    private static final String CREATE_TABLE_FAVORITES = "create table " + FavoritesMovieContract.FavoriteMoviesColumn.TABLE_FAVORITES
            + "(" + FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_ID + " integer primary key, "
                  + FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_TITLE + " text not null,"
                  + FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_POSTER_FILE + " text, "
                  + FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_OVERVIEW + " text, "
                  + FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_YEAR + " text, "
                  + FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_RUNTIME + " text, "
                  + FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_RATING + " double, "
                  + FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_TRAILERS + " text, "
                  + FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_REVIEWS + " text );";

    private static final String DB_SCHEMA = CREATE_TABLE_FAVORITES;

    private SQLiteDatabase mDB;

    public FavoritesDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mDB = getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e(TAG,"FavoritesDbHelper onCreate");
        db.execSQL(DB_SCHEMA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(TAG, "Upgrading database. Existing contents will be lost.[" + oldVersion + "]->[" + newVersion + "]");
        db.execSQL("DROP TABLE IF EXISTS " + FavoritesMovieContract.FavoriteMoviesColumn.TABLE_FAVORITES);
        onCreate(db);
    }
}
