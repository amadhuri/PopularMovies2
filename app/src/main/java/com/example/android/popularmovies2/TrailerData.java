package com.example.android.popularmovies2;

import android.net.Uri;

/**
 * Created by Madhuri on 11/28/2015.
 */
public class TrailerData {
    private Uri trailerUri;
    private String trailerName;

    public TrailerData(Uri trailerUri, String trailerName) {
        this.trailerUri = trailerUri;
        this.trailerName = trailerName;
    }

    public Uri getTrailerUri() { return trailerUri;}

    public String getTrailerName() { return trailerName;}
}
