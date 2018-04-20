package com.android.www.moviebro.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.www.moviebro.data.MovieContract.FavoriteEntry;

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TABLE = "CREATE TABLE " + FavoriteEntry.TABLE_NAME + " ("
                + FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FavoriteEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + FavoriteEntry.COLUMN_MOVIE_NAME + " TEXT NOT NULL, "
                + FavoriteEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL, "
                + FavoriteEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, "
                + FavoriteEntry.COLUMN_MOVIE_VOTE_AVERAGE + " TEXT NOT NULL, "
                + FavoriteEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, "
                + FavoriteEntry.COLUMN_MOVIE_IS_FAVORITE + " INTEGER DEFAULT 0, "
                + " UNIQUE (" + FavoriteEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";
        ;

        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME;
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE);

        onCreate(sqLiteDatabase);
    }
}
