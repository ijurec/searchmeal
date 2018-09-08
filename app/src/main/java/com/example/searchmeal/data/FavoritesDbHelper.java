package com.example.searchmeal.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.searchmeal.data.FavoritesContract.FavoriteEntry;

public class FavoritesDbHelper extends SQLiteOpenHelper {

    private static final String TAG = FavoritesDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;

    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_WEATHER_TABLE =

                "CREATE TABLE " + FavoriteEntry.TABLE_NAME + " (" +
                FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoriteEntry.COLUMN_RECIPE_ID + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_PUBLISHER + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_SOCIAL_RANK + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_F2F_URL + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_PUBLISHER_URL + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_SOURCE_URL + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_INGREDIENTS + " TEXT NOT NULL );";

        db.execSQL(SQL_CREATE_WEATHER_TABLE);
        Log.i(TAG, "SQL for created weather table: \n" + SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);
        onCreate(db);
    }
}
