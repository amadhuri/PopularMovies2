package com.example.android.popularmovies2;

import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Madhuri on 11/17/2015.
 */
public class FetchMovieTask extends AsyncTask<Integer, Void, ArrayList<MovieData>> {

    private final ImageAdapter adapter;
    private final String sortOrder;
    private final FetchMovieTaskListener fetchListener;
    private static final String TAG = FetchMovieTask.class.getSimpleName();
    private static final String POSTER_BASE_URI = "http://image.tmdb.org/t/p/w185";

    FetchMovieTask(ImageAdapter adapter, String sortOrder, FetchMovieTaskListener fetchMovieTaskListener){
        this.sortOrder = sortOrder;
        this.adapter = adapter;
        this.fetchListener = fetchMovieTaskListener;
    }
    @Override
    protected ArrayList<MovieData> doInBackground(Integer... params) {
        ArrayList<MovieData> moviePosters = new ArrayList<>();

        try{
            JSONObject jObj = JSONLoader.load("/discover/movie?sort_by=" + sortOrder + "&page=" + params[0]);
            if(jObj == null) {
                Log.v(TAG, "Can not load the data from remote service");
                return null;
            }
            Log.v(TAG, "page:" + jObj.getInt("page") + "params[0] =" + params[0]);
            JSONArray movieArray = jObj.getJSONArray("results");
            Log.v(TAG, "length:" + movieArray.length());

            for(int i=0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.optJSONObject(i);
                String moviePoster = movie.getString("poster_path");
                int movieId = movie.getInt("id");
                MovieData data = new MovieData(POSTER_BASE_URI + moviePoster, movieId);
                moviePosters.add(data);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error:",e);
        }
        return moviePosters;
    }

    @Override
    protected void onPostExecute(ArrayList<MovieData> moviePosters) {
        super.onPostExecute(moviePosters);
        if (moviePosters != null)
        {
            for (MovieData res : moviePosters)
            {
                adapter.add(res);
            }
            adapter.notifyDataSetChanged();
            fetchListener.onFetchCompleted();
        } else {
            fetchListener.onFetchFailed();

        }
    }
}
