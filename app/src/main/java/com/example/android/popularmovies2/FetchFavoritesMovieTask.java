package com.example.android.popularmovies2;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Madhuri on 12/3/2015.
 */
public class FetchFavoritesMovieTask extends AsyncTask<Void, Void, ArrayList<MovieData>> {
    private static final String TAG = FetchFavoritesMovieTask.class.getSimpleName();
    private final ImageAdapter adapter;
    private final ContentResolver contentResolver;

    FetchFavoritesMovieTask(ImageAdapter adapter, ContentResolver contentResolver) {
        this.adapter = adapter;
        this.contentResolver = contentResolver;
    }

    @Override
    protected ArrayList<MovieData> doInBackground(Void... params) {

        ArrayList<MovieData> moviePosters = new ArrayList<>();
        String[] projection = new String[]{FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_ID, FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_POSTER_FILE};
        final Cursor cursor = contentResolver.query(FavoritesProvider.CONTENT_URI,projection,null,null,null);
        Log.d(TAG,"Cursor count:"+cursor.getCount() );
        if (cursor.getCount()!=0) {
            Log.e(TAG,"cursor column count:"+cursor.getColumnCount());
            while(cursor.moveToNext()) {
                MovieData data = new MovieData(cursor.getString(1), cursor.getInt(0));
                moviePosters.add(data);
            }
        }
        return moviePosters;
    }

    @Override
    protected void onPostExecute(ArrayList<MovieData> moviePosters) {
        super.onPostExecute(moviePosters);
        if (moviePosters!=null) {
            for (MovieData res : moviePosters) {
                adapter.add(res);
            }
            adapter.notifyDataSetChanged();
        }
    }
}
