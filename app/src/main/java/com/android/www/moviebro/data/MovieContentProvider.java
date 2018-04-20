package com.android.www.moviebro.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.www.moviebro.data.MovieContract.FavoriteEntry;

/**
 * Created by Mohamed on 3/10/2018.
 */

public class MovieContentProvider extends ContentProvider {

    private MovieDbHelper mDbHelper;

    public static final int FAVORITE_MOVIES = 100;
    public static final int FAVORITE_MOVIES_ID = 101;

    private static UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(
                MovieContract.AUTHORITY, MovieContract.PATH_FAVORITE_MOVIES, FAVORITE_MOVIES);
        uriMatcher.addURI(
                MovieContract.AUTHORITY, MovieContract.PATH_FAVORITE_MOVIES + "/#", FAVORITE_MOVIES_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mDbHelper = new MovieDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase sqLiteDatabase = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITE_MOVIES:
                cursor = sqLiteDatabase.query(
                        FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVORITE_MOVIES_ID:
                selection = FavoriteEntry.COLUMN_MOVIE_ID + "=?";

                String movieServerId = uri.getPathSegments().get(1);
                selectionArgs = new String[]{movieServerId};

                cursor = sqLiteDatabase.query(
                        FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);

        switch (match) {
            case FAVORITE_MOVIES:
                return FavoriteEntry.CONTENT_DIR_TYPE;
            case FAVORITE_MOVIES_ID:
                return FavoriteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        Uri itemUri;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITE_MOVIES:
                long itemId = database.insert(FavoriteEntry.TABLE_NAME, null, contentValues);
                if (itemId > 0) {
                    itemUri = ContentUris.withAppendedId(FavoriteEntry.CONTENT_URI, itemId);
                } else {
                    throw new SQLException("Failed to insert new row " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return itemUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();

        int rowCount;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITE_MOVIES_ID:
                String selection = FavoriteEntry.COLUMN_MOVIE_ID + "=?";

                String movieId = uri.getPathSegments().get(1);
                String[] selectionArgs = new String[]{movieId};
                rowCount = sqLiteDatabase.delete(FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return rowCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
