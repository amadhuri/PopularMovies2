package com.example.android.popularmovies2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Movie;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.MovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Madhuri on 11/18/2015.
 */
public class MovieDetailActivity extends Activity implements OnMovieClickListener{
    private final String TAG = MovieDetailActivity.class.getSimpleName();
    private int movieId;
    private String sortOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getResources().getBoolean(R.bool.landscape_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        setContentView(R.layout.activity_movie_detail);
        Intent detailIntent = getIntent();

        Log.e(TAG,"MovieDetailActivity entered");
        movieId=detailIntent.getIntExtra("MovieListItem",movieId);

        sortOrder = detailIntent.getStringExtra("SortOrder");
        Log.e(TAG, "MovieDetailActivity movieId:"+movieId+" "+"sortOrder:"+sortOrder);

        if( savedInstanceState == null ) {
            //MovieDetailFragment detailFragment = new MovieDetailFragment();
            MovieDetailFragment detailFragment = (MovieDetailFragment) getFragmentManager().findFragmentById(R.id.movie_list_frag);
            Bundle args = new Bundle();
            args.putInt("movieId", movieId);
            args.putString("sortOrder", sortOrder);
            detailFragment.updateContent(args);
            //detailFragment.setArguments(args);
           /* getFragmentManager().beginTransaction()
                    .add(R.id.detail_container, detailFragment)
                    .commit();
                    */
        }
        /*

        setupMovieDetailsView();
        if(sortOrder.equals("favorites")){
            fetchFavoriteMovieData(movieId);
        }else
            fetchMovieData(movieId);
        */
    }

    @Override
    public void onMovieClick(int movieId, int position) {
        //do nothing
    }

    @Override
    public void onFavoriteDeleted(int movieId, int position) {
        //do nothing
    }
}
