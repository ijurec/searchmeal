package com.example.searchmeal.utilities;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.searchmeal.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtil {

    private static final String TAG = NetworkUtil.class.getSimpleName();

    private static final String KEY_PARAM = "key";
    private static final String QUERY_PARAM = "q";
    private static final String SORT_PARAM = "sort";
    private static final String PAGE_PARAM = "page";
    private static final String RECIPE_ID_PARAM = "rId";

    public static final String SORT_BY_RATING = "r";
    public static final String SORT_BY_TRENDINGNESS = "t";

    static {
        System.loadLibrary("native-lib");
    }

    private native static String invokeNativeFunction();


    public static URL getSearchUrl(Context context, String query, String sort, String page) {
        Uri weatherQueryUri = Uri.parse(context.getString(R.string.api_url)).buildUpon()
                .appendPath("search")
                .appendQueryParameter(KEY_PARAM, invokeNativeFunction())
                .appendQueryParameter(QUERY_PARAM, query)
                .appendQueryParameter(SORT_PARAM, sort)
                .appendQueryParameter(PAGE_PARAM, page)
                .build();

        return makeUrl(weatherQueryUri);
    }

    public static URL getRecipeUrl(Context context, String recipeId) {
        Uri weatherQueryUri = Uri.parse(context.getString(R.string.api_url)).buildUpon()
                .appendPath("get")
                .appendQueryParameter(KEY_PARAM, invokeNativeFunction())
                .appendQueryParameter(RECIPE_ID_PARAM, recipeId)
                .build();

        return makeUrl(weatherQueryUri);
    }

    @Nullable
    private static URL makeUrl(Uri weatherQueryUri) {
        try {
            String weatherQueryUriStr = weatherQueryUri.toString();
            Log.i(TAG, weatherQueryUriStr);
            return new URL(weatherQueryUriStr);
        } catch (MalformedURLException e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            Log.i(TAG, response);
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }
}
