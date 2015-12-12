package com.example.android.popularmovies2;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;


public class MainActivity extends Activity implements OnMovieClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    protected static final String SHARED_PREF_NAME = "com.example.android.popularmovies2.fetch.settings";
    protected  static final String SHARED_PREF_KEY = "sort_order_key";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getResources().getBoolean(R.bool.landscape_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        setContentView(R.layout.activity_main);
        /*if( savedInstanceState == null ) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MoviesListFragment())
                    .commit();
        }*/
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getResources().getBoolean(R.bool.landscape_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        MovieDetailFragment detailFrag = (MovieDetailFragment)getFragmentManager().findFragmentById(R.id.detail_frag);
        if (detailFrag != null) {
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(detailFrag);
            ft.attach(detailFrag);
            ft.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent settingsIntent = new Intent(this, UserSettingActivity.class);
        startActivity(settingsIntent);

        return true;
    }

    @Override
    public void onMovieClick(int movieId, int position) {
        MovieDetailFragment detailFrag = (MovieDetailFragment)getFragmentManager().findFragmentById(R.id.detail_frag);
        if (detailFrag == null) {
            Log.v(TAG, "onMovieClick movieId :" + movieId);
            Intent detailIntent = new Intent(this, MovieDetailActivity.class);
            detailIntent.putExtra("MovieListItem",movieId);
            detailIntent.putExtra("SortOrder", sharedPreferences.getString(SHARED_PREF_KEY, getString(R.string.most_popular_value)));
            detailIntent.putExtra("position", position);
            startActivity(detailIntent);
        } else {
            Bundle args = new Bundle();
            args.putInt("movieId", movieId);
            args.putString("sortOrder", sharedPreferences.getString(SHARED_PREF_KEY, getString(R.string.most_popular_value)));
            args.putInt("position", position);
            detailFrag.updateContent(args);
        }
    }

    @Override
    public void onFavoriteDeleted(int movieId, int position) {
        MovieDetailFragment detailFrag = (MovieDetailFragment)getFragmentManager().findFragmentById(R.id.detail_frag);
        if (detailFrag != null) {
            //update the movie list fragment
            MoviesListFragment listFrag = (MoviesListFragment)getFragmentManager().findFragmentById(R.id.movie_list_frag);
            listFrag.notifyFavoriteDeleted(position);
            Bundle args = new Bundle();
            args.putInt("movieId",0);
            detailFrag.updateContent(args);
        }
    }
}
