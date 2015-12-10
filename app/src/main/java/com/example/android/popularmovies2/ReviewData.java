package com.example.android.popularmovies2;

import android.net.Uri;

/**
 * Created by Madhuri on 12/1/2015.
 */
public class ReviewData {

    private String author;
    private String content;
    private Uri reviewUri;

    public ReviewData(String author, String content, Uri reviewUri) {
        this.author = author;
        this.content = content;
        this.reviewUri = reviewUri;
    }

    protected String getAuthor() {return this.author;}

    protected String getContent() { return this.content;}

    protected Uri getReviewUri() { return this.reviewUri;}

}
