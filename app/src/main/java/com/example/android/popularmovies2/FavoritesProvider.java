package com.example.android.popularmovies2;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.w3c.dom.Text;

import java.sql.SQLException;

/**
 * Created by Madhuri on 12/3/2015.
 */
public class FavoritesProvider extends ContentProvider {

    static final String TAG = FavoritesProvider.class.getSimpleName();

    //To query content provider, we should specify the query string in the form of a URI of the below format
    // <prefix>://<authority>/<data_type>/<id>
    // <prefix> is always set to content://
    // authority specifies the name of the content provider. For third party content providers this should be fully qualified name
    // data_type indicates the particular data the provider provides.

    static final String PROVIDER_NAME = "com.popularmovies.favoritesprovider";
    static final String URL = "content://" + PROVIDER_NAME + "/favorites";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final int FAVORITES = 1;
    static final int FAVORITES_ID = 2;
    static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME,"favorites",FAVORITES);
        uriMatcher.addURI(PROVIDER_NAME, "favorites/#", FAVORITES_ID);
    }

    private SQLiteDatabase favDB;

    @Override
    public boolean onCreate() {
        Log.e(TAG,"FavoritesProvider onCreate called" );
        Context context = getContext();
        FavoritesDbHelper dbHelper = new FavoritesDbHelper(context);
        favDB = dbHelper.getWritableDatabase();
        return (favDB == null) ? false : true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(FavoritesMovieContract.FavoriteMoviesColumn.TABLE_FAVORITES);

        switch (uriMatcher.match(uri)) {
            case FAVORITES:
                  Log.e(TAG,"query uriMatcher favorites");
                break;
            case FAVORITES_ID: {
                Log.e(TAG, "query uriMatcher FAVORITES_ID");
                qb.appendWhere(FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_ID + "=" +
                                uri.getLastPathSegment());
                break;
            }
        }
        Cursor cursor = qb.query (
                favDB,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        cursor.setNotificationUri(getContext().getContentResolver(),CONTENT_URI);
        return cursor;

    }


    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /** Add a new favorites record */

        long rowID = favDB.insert(FavoritesMovieContract.FavoriteMoviesColumn.TABLE_FAVORITES, "", values);
        Log.e(TAG,"FavoritesProvider insert rowID:"+rowID);

        /**
         * If record is added successfully
         */
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        try {
            throw new SQLException("Failed to add new record into "+uri);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)) {
            case FAVORITES:
                count = favDB.delete(FavoritesMovieContract.FavoriteMoviesColumn.TABLE_FAVORITES, selection, selectionArgs);
                break;
            case FAVORITES_ID:
                String id = uri.getPathSegments().get(1);
                count = favDB.delete(FavoritesMovieContract.FavoriteMoviesColumn.TABLE_FAVORITES,FavoritesMovieContract.FavoriteMoviesColumn.COL_MOVIE_ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
