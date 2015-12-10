package com.example.android.popularmovies2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import com.squareup.picasso.Picasso;

/**
 * Created by Madhuri on 11/13/2015.
 */
public class ImageAdapter extends BaseAdapter {

    private final ArrayList<MovieData> finalMoviePosters = new ArrayList<>();
    private final String TAG = ImageAdapter.class.getSimpleName();
    private final Context mContext;
    private static final int IMAGE_WIDTH = 185;
    private static final int IMAGE_HEIGHT = 278;
    private final float density;


    public ImageAdapter(Context c) {
        mContext = c;
        density = mContext.getResources().getDisplayMetrics().density;
    }

    public int getCount() { return finalMoviePosters.size();}

    public long getItemId(int position) {
        return finalMoviePosters.get(position).getMovieId();
    }

    public Object getItem(int position) {
        return finalMoviePosters.get(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
            imageView.setPadding(0,0,0,0);
            imageView.setBackgroundColor(Color.rgb(0,0,0));

            //imageView.setLayoutParams(new GridView.LayoutParams((int) (IMAGE_WIDTH * density), (int) (IMAGE_HEIGHT * density)));
            //imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            imageView = (ImageView) convertView;
        }
        Picasso pic = Picasso.with(mContext);

        pic.load(finalMoviePosters.get(position).getMoviePoster())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.no_movies)
                .into(imageView);

        return imageView;
    }

    public void add(MovieData res) {
        finalMoviePosters.add(res);
    }

    public void addAll(List<MovieData> res) {
        finalMoviePosters.addAll(res);
    }

    public void remove(int position) {

        Log.e(TAG, "Remove position:"+position+" finalMoviePosters.size:"+finalMoviePosters.size());
        finalMoviePosters.remove(position);
    }

    public void clearData() {
        finalMoviePosters.clear();
        notifyDataSetChanged();
    }

    public List<MovieData> getMovieData() {
        return finalMoviePosters;
    }


}
