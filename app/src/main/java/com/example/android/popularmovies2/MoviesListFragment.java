package com.example.android.popularmovies2;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.sql.RowIdLifetime;

/**
 * A placeholder fragment containing a simple view.
 */

public class MoviesListFragment extends Fragment {

    private ImageAdapter thumbNailImageAdapter;
    private static final String TAG = MoviesListFragment.class.getSimpleName();
    private String sortOrder ;
    private GridView gridView;
    private SharedPreferences sharedPreferences;
    private OnMovieClickListener mCallback;


    public MoviesListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thumbNailImageAdapter = new ImageAdapter(getActivity());
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movies_list, container, false);
        gridView = (GridView) rootView.findViewById(R.id.gridview);
        gridView.setAdapter(thumbNailImageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Log.e(TAG,"onItemClick entered position:"+position);
                int movieId = (int)thumbNailImageAdapter.getItemId(position);
                Log.e(TAG,"onItemClick entered movieId:"+movieId);
                mCallback.onMovieClick(movieId, position);
                /*
                Intent detailIntent = new Intent(getActivity().getApplicationContext(), MovieDetailActivity.class);
                detailIntent.putExtra("MovieListItem",movieId);
                detailIntent.putExtra("SortOrder", sortOrder);
                Log.e(TAG,"onItemClick starting activity");
                startActivity(detailIntent);
                */
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnMovieClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMovieClickListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.e(TAG, "OnResume entered");
        sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREF_NAME,Context.MODE_PRIVATE);
        sortOrder = sharedPreferences.getString(MainActivity.SHARED_PREF_KEY, getString(R.string.most_popular_value));
        Log.e(TAG,"OnResume shared pref sortOrder:"+sortOrder);
        boolean isConnected = checkInternetConnection();
        Log.v(TAG, "Network is " + isConnected);
        if(!isConnected) {
            Log.e(TAG, "Network is not available");
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Network is not available", Toast.LENGTH_LONG);
            toast.show();
        }
        if (sortOrder.equals(getString(R.string.favorites_value)) || !isConnected) {
            FetchFavoritesMovieTask favoritesTask = new FetchFavoritesMovieTask(thumbNailImageAdapter, getActivity().getContentResolver());
            gridView.setOnScrollListener(null);
            favoritesTask.execute();

        } else {
            if (!isConnected)
                return;
            gridView.setOnScrollListener(new MovieListViewScrollListener());
        }

        thumbNailImageAdapter.clearData();

    }

    public void notifyFavoriteDeleted(int position) {

        thumbNailImageAdapter.remove(position);
        thumbNailImageAdapter.notifyDataSetChanged();
        return;
    }


    private boolean checkInternetConnection() {
        Log.e(TAG,"checkInternetConnection entered");
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);


        Log.e(TAG,"checkInternetConnection ConnectivityManager:"+cm);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork != null)
         Log.e(TAG,"checkInternetConnection activeNetwork:"+activeNetwork);
        else
            Log.e(TAG,"checkInternetConnection activeNetwork is NULL");
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Log.e(TAG,"checkInternetConnection isConnected:"+isConnected);
        return isConnected;
    }

    private class MovieListViewScrollListener implements AbsListView.OnScrollListener,FetchMovieTaskListener {
        private static final int PAGE_SIZE = 20;
        private boolean loadingState = false;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if(firstVisibleItem + visibleItemCount >= totalItemCount) {
                if(!loadingState) {
                    Log.v("MovieListViewScrollListener", "onScroll calling FetchMovieTask sortOrder:"+sortOrder);
                    FetchMovieTask fetchMovieTask = new FetchMovieTask(thumbNailImageAdapter, sortOrder, this);
                    fetchMovieTask.execute(totalItemCount/PAGE_SIZE +1);
                    loadingState = true;
                }
            }
        }

        @Override
        public void onFetchCompleted() {
            loadingState = false;
        }

        @Override
        public void onFetchFailed() {
            loadingState = false;
            Toast.makeText(getActivity(),"Can not load the data from network", Toast.LENGTH_LONG).show();
        }
    }
}