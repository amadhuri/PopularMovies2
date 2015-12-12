package com.example.android.popularmovies2;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Movie;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Madhuri on 12/7/2015.
 */
public class MovieDetailFragment extends Fragment {

    private final String TAG = MovieDetailFragment.class.getSimpleName();
    private String sortOrder;
    private View rootView;
    private int position;

    private int movieId;
    private MovieDetailData detailsData;
    private TextView movieTitle;
    private ImageView thumbImageView;
    private TextView releaseYear;
    private TextView rating;
    private TextView overview;
    private TextView runTime;
    private ImageView favsIcon;
    private LinearLayout trailersListView;
    private LinearLayout reviewsListView;
    private JSONObject trailerObj;
    private JSONObject reviewObj;
    private List<TrailerData> trailerData;
    private List<ReviewData> reviewData;
    private boolean isFavorite;
    private ScrollView detailScrollView;
    private TextView tv_SelectMovie;
    private OnMovieClickListener mCallback;

    private static final String POSTER_BASE_URI = "http://image.tmdb.org/t/p/w185";

    public MovieDetailFragment() {

    }

    public static MovieDetailFragment newInstance(int movieId, String sortOrder) {
        MovieDetailFragment f = new MovieDetailFragment();

        Bundle args = new Bundle();
        args.putInt("movieId", movieId);
        args.putString("sortOrder", sortOrder);
        f.setArguments(args);
        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {

        super.onActivityCreated(savedInstance);
        rootView = this.getView();
       // getFragmentArguments();


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

     rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
     setupMovieDetailsView();
     return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (getActivity().getClass().getSimpleName().compareTo("MainActivity") == 0) {

            tv_SelectMovie.setVisibility(View.VISIBLE);
            detailScrollView.setVisibility(View.INVISIBLE);
        }
        Log.e(TAG,"onResume calling activity name:"+getActivity().getClass().getSimpleName());
    }

   /* @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

    }*/

    @Override
    public void onPause() {
        super.onPause();
    }

    public void updateContent(Bundle args) {
        movieId = args.getInt("movieId");
        if (movieId == 0) {
            tv_SelectMovie.setVisibility(View.VISIBLE);
            detailScrollView.setVisibility(View.INVISIBLE);
            return;
        }
        sortOrder = args.getString("sortOrder");
        position = args.getInt("position");

        trailersListView.removeAllViews();
        reviewsListView.removeAllViews();
        tv_SelectMovie.setVisibility(View.GONE);
        detailScrollView.setVisibility(View.VISIBLE);

        if(sortOrder.equals("favorites")){
            fetchFavoriteMovieData(movieId);
        }else
            fetchMovieData(movieId);
    }

    private void setupMovieDetailsView() {
        movieTitle = (TextView)rootView.findViewById(R.id.movie_title);
        thumbImageView = (ImageView)rootView.findViewById(R.id.thumb_imageView);
        releaseYear = (TextView)rootView.findViewById(R.id.release_date);
        rating = (TextView)rootView.findViewById(R.id.rating);
        overview = (TextView)rootView.findViewById(R.id.overview);
        favsIcon = (ImageView)rootView.findViewById(R.id.favorites_icon);
        runTime  = (TextView)rootView.findViewById(R.id.run_time);
        trailersListView = (LinearLayout)rootView.findViewById(R.id.trailer_list);
        reviewsListView = (LinearLayout)rootView.findViewById(R.id.reviews_list);
        detailScrollView = (ScrollView)rootView.findViewById(R.id.sv_movieDetail);
        tv_SelectMovie = (TextView)rootView.findViewById(R.id.tv_selectMovieDetail);
    }

    private void populateDetailsViewData(final MovieDetailData detailData) {

        Picasso pic = Picasso.with(getActivity().getApplicationContext());
        pic.load(POSTER_BASE_URI + detailData.getMoviePoster())
                .error(R.drawable.no_movies)
                .into(thumbImageView);
        movieTitle.setText(detailData.getMovieTitle());
        releaseYear.setText(detailData.getMoveReleaseYear());
        overview.setText(detailData.getMovieOverview());
        overview.setEllipsize(TextUtils.TruncateAt.END);
        overview.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        runTime.setText(detailData.getMovieRunTime()+"mins");
        rating.setText(detailData.getVoteAverage()+"/10");

        //check if this movie is inserted into favorites
        Uri movieUri = Uri.parse(FavoritesProvider.CONTENT_URI+"/"+detailData.getMovieId());
        String[] projection = new String[]{FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_ID};
        Cursor cursor = getActivity().getContentResolver().query(movieUri,projection,null, null, null);
        if (cursor.getCount() == 0) {
            favsIcon.setImageResource(R.drawable.grey_star);
            isFavorite = false;
        }
        else {
            favsIcon.setImageResource(R.drawable.yellow_star);
            isFavorite = true;
        }
        favsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isFavorite) {
                    ContentValues values = new ContentValues();
                    values.put(FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_ID, detailData.getMovieId());
                    values.put(FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_TITLE, detailData.getMovieTitle());
                    values.put(FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_OVERVIEW, detailData.getMovieOverview());
                    values.put(FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_RATING, detailData.getVoteAverage());
                    values.put(FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_RUNTIME, detailData.getMovieRunTime());
                    values.put(FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_YEAR, detailData.getMoveReleaseYear());
                    values.put(FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_POSTER_FILE, detailData.getMoviePoster());
                    if (trailerObj.length()>0)
                        values.put(FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_TRAILERS, trailerObj.toString());
                    else
                        values.put(FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_TRAILERS, "");
                    if (reviewObj.length() > 0)
                        values.put(FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_REVIEWS, reviewObj.toString());
                    else
                        values.put(FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_REVIEWS, "");
                    Uri uri = getActivity().getContentResolver().insert(FavoritesProvider.CONTENT_URI, values);

                    if (uri != null) {
                        favsIcon.setImageResource(R.drawable.yellow_star);
                        isFavorite = true;
                    }
                }
                else {
                    favsIcon.setImageResource(R.drawable.grey_star);
                    Uri deleteUri = Uri.parse(FavoritesProvider.CONTENT_URI+"/"+detailData.getMovieId());
                    int count = getActivity().getContentResolver().delete(deleteUri, null, null);
                    isFavorite = false;
                    mCallback.onFavoriteDeleted(movieId, position);
                }
            }
        });
    }

    private void populateTrailersView(final List<TrailerData> trailerData) {
        if (trailerData.size()==0) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.no_item, null);
            TextView noItem = (TextView) view.findViewById(R.id.no_item_text);
            noItem.setText("No Trailers");
            trailersListView.addView(view);
            return;
        }

        for (final TrailerData trailer : trailerData) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.trailer_list_item,null);
            TextView trailerName = (TextView) view.findViewById(R.id.trailer_list_item_title);
            trailerName.setText(trailer.getTrailerName());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "Trailer URI:"+trailer.getTrailerUri().toString());
                    startActivity(new Intent(Intent.ACTION_VIEW, trailer.getTrailerUri()));
                }
            });
            ImageButton shareButton = (ImageButton)view.findViewById(R.id.shareButton);

            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    //share.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                    share.putExtra(Intent.EXTRA_SUBJECT, "Trailer");
                    share.putExtra(Intent.EXTRA_TEXT, trailer.getTrailerUri().toString());
                    startActivity(Intent.createChooser(share, "Share link!"));
                }
            });
            trailersListView.addView(view);
        }
        return;
    }

    private void populateReviewsView(final List<ReviewData> reviewsData) {

        if (reviewsData.size()==0) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.no_item, null);
            TextView noItem = (TextView) view.findViewById(R.id.no_item_text);
            noItem.setText("No User Reviews");
            reviewsListView.addView(view);
            return;
        }
        for (final ReviewData review : reviewsData) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.review_item, null);
            TextView author = (TextView) view.findViewById(R.id.review_author);
            author.setText("A review by "+review.getAuthor());
            TextView content = (TextView) view.findViewById(R.id.review_content);
            content.setText(review.getContent());
            reviewsListView.addView(view);
        }
        return;
    }

    private void fetchMovieData(int movieId) {
        FetchDetailsMovieTask detailTask = new FetchDetailsMovieTask();
        detailTask.execute(movieId);

        FetchTrailersTask trailersTask = new FetchTrailersTask();
        trailersTask.execute(movieId);

        FetchReviewsTask reviewsTask = new FetchReviewsTask();
        reviewsTask.execute(movieId);
    }

    private void fetchFavoriteMovieData(int movieId) {
        String[] projection = new String[]{FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_ID,
                FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_POSTER_FILE,
                FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_OVERVIEW,
                FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_RATING,
                FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_RUNTIME,
                FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_YEAR,
                FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_TITLE,
                FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_REVIEWS,
                FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_TRAILERS
        };
        Uri uri = Uri.parse(FavoritesProvider.CONTENT_URI+"/"+movieId);
        final Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        cursor.moveToFirst();
        detailsData = new MovieDetailData(movieId,
                cursor.getString(cursor.getColumnIndexOrThrow(FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_POSTER_FILE)),
                cursor.getString(cursor.getColumnIndex(FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_TITLE)),
                cursor.getString(cursor.getColumnIndex(FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_OVERVIEW)),
                cursor.getString(cursor.getColumnIndex(FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_YEAR)),
                cursor.getString(cursor.getColumnIndex(FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_RUNTIME)),
                cursor.getDouble(cursor.getColumnIndex(FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_RATING))
        );

        try {
            JSONObject trailerJObj = new JSONObject(cursor.getString(cursor.getColumnIndexOrThrow(FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_TRAILERS)));
            JSONObject reviewJObj  = new JSONObject(cursor.getString(cursor.getColumnIndexOrThrow(FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_REVIEWS)));

            parseTrailerObj(trailerJObj);
            populateTrailersView(trailerData);

            JSONArray reviewResultArray = reviewJObj.getJSONArray("results");
            reviewData = new ArrayList<ReviewData>();
            for (int i=0; i < reviewResultArray.length(); i++) {
                JSONObject reviewObj = reviewResultArray.getJSONObject(i);
                String author = reviewObj.getString("author");
                String content = reviewObj.getString("content");
                Uri reviewUri = Uri.parse(reviewObj.getString("url"));
                ReviewData reviewItem = new ReviewData(author, content, reviewUri);
                reviewData.add(reviewItem);
            }
            populateReviewsView(reviewData);

        }catch(JSONException e) {
            Log.e(TAG,"fetchFavoriteMovieData e:"+e);
        }

        populateDetailsViewData(detailsData);
        //populateTrailersView(trailers);
        //populateReviewsView(reviews);
    }

    private void parseTrailerObj(JSONObject jObj) {
        trailerData = new ArrayList<TrailerData>();
        try {
            JSONArray trailersResultArray = jObj.getJSONArray("results");

            for (int i = 0; i < trailersResultArray.length(); i++) {
                JSONObject trailerDetails = trailersResultArray.getJSONObject((i));
                Uri trailerUri = Uri.parse(new String("http://www.youtube.com/watch?v="+trailerDetails.getString("key")));
                TrailerData trailerEntry = new TrailerData(trailerUri, trailerDetails.getString("name"));
                trailerData.add(trailerEntry);
            }

        } catch (JSONException e) {
            Log.e(TAG,"FetchTrailersTask Exception:",e);
        }
        return;
    }

    private class FetchDetailsMovieTask extends AsyncTask<Integer, Void, JSONObject> {
        private final String TAG = FetchDetailsMovieTask.class.getSimpleName();

        @Override
        protected JSONObject doInBackground(Integer... params) {
            JSONObject jObj = JSONLoader.load("/movie/"+params[0]);
            return jObj;
        }

        @Override
        protected void onPostExecute(final JSONObject jObj) {
            super.onPostExecute(jObj);
            if(jObj != null) {

                try {
                    //poster_path, title, overview, release_year, run time, ratings
                    detailsData = new MovieDetailData(movieId, POSTER_BASE_URI+jObj.getString("poster_path"),
                            jObj.getString("title"),
                            jObj.getString("overview"),
                            jObj.getString("release_date"),
                            jObj.getString("runtime"),
                            jObj.getDouble("vote_average"));
                    populateDetailsViewData(detailsData);
                } catch(JSONException e) {
                    Log.e(TAG,"onPostExecute e:"+e);
                }
            }
        }
    }

    private class FetchTrailersTask extends AsyncTask<Integer, Void, JSONObject> {
        private final String TAG = FetchTrailersTask.class.getSimpleName();

        @Override
        protected JSONObject doInBackground(Integer... params) {
            JSONObject jObj = JSONLoader.load("/movie/" + params[0]+"/videos");
            return jObj;
        }

        @Override
        protected void onPostExecute(JSONObject jObj) {
            super.onPostExecute(jObj);
            if (jObj != null) {
                trailerObj = jObj;
                parseTrailerObj(jObj);

                populateTrailersView(trailerData);
            }
        }
    }

    private class FetchReviewsTask extends AsyncTask<Integer, Void, JSONObject> {
        private final String TAG = FetchReviewsTask.class.getSimpleName();

        @Override
        protected JSONObject doInBackground(Integer... params) {
            JSONObject jObj = JSONLoader.load("/movie/" + params[0] + "/reviews");
            return jObj;
        }

        @Override
        protected void onPostExecute(JSONObject jObj) {
            super.onPostExecute(jObj);
            reviewObj = jObj;

            if (jObj != null) {
                reviewData = new ArrayList<ReviewData>();

                try {
                    JSONArray reviewResultArray = jObj.getJSONArray("results");
                    for (int i=0; i < reviewResultArray.length(); i++) {
                        JSONObject reviewObj = reviewResultArray.getJSONObject(i);
                        String author = reviewObj.getString("author");
                        String content = reviewObj.getString("content");
                        Uri reviewUri = Uri.parse(reviewObj.getString("url"));
                        ReviewData reviewItem  = new ReviewData(author, content, reviewUri);
                        reviewData.add(reviewItem);
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "FetchReviewsTask e:"+e);
                }
                populateReviewsView(reviewData);
            }
        }
    }



}
