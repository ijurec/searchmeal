package com.example.searchmeal.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.searchmeal.data.FavoritesContract.FavoriteEntry;

public class FavoritesProvider extends ContentProvider {

    private static final String TAG = FavoritesProvider.class.getSimpleName();

    private FavoritesDbHelper mDbHelper;

    public static final int CODE_FAVORITES = 100;
    public static final int CODE_FAVORITES_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavoritesContract.CONTENT_AUTHORITY;

        /* This URI is content://com.example.searchmeal/favorites/ */
        matcher.addURI(authority, FavoritesContract.PATH_FAVORITES, CODE_FAVORITES);

        /*
         * This URI would look something like content://com.example.searchmeal/favorites/15811
         * The "/*" signifies to the UriMatcher that if PATH_FAVORITES is followed by ANY VALID CHARACTER id,
         * that it should return the CODE_FAVORITES_ID code
         */
        matcher.addURI(authority, FavoritesContract.PATH_FAVORITES + "/*", CODE_FAVORITES_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new FavoritesDbHelper(getContext());
        Log.i(TAG, "Data base is created");
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITES: {
                cursor = mDbHelper.getReadableDatabase().query(
                        FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case CODE_FAVORITES_ID: {
                String recipeIdString = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{recipeIdString};

                cursor = mDbHelper.getReadableDatabase().query(
                        FavoriteEntry.TABLE_NAME,
                        projection,
                        FavoriteEntry.COLUMN_RECIPE_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Log.i(TAG, "Query uri: " + uri);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues value) {
        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITES: {
                final SQLiteDatabase db = mDbHelper.getWritableDatabase();
                long id = db.insert(FavoriteEntry.TABLE_NAME, null, value);
                Uri resultUri = ContentUris.withAppendedId(uri, id);
                getContext().getContentResolver().notifyChange(resultUri, null);
                Log.i(TAG, "Insert uri: " + uri);
                return resultUri;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numRowsDeleted;
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITES_ID: {
                String recipeIdString = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{recipeIdString};
                numRowsDeleted = db.delete(FavoriteEntry.TABLE_NAME, FavoriteEntry.COLUMN_RECIPE_ID + " = ? ",
                        selectionArguments);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.i(TAG, "Delete uri: " + uri);
        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new RuntimeException("Not implementing update in Search Meal.");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("Not implementing getType in Search Meal.");
    }
}
