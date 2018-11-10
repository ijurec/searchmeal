package com.example.searchmeal.utilities;

import android.content.Context;
import android.util.Log;

import com.example.searchmeal.R;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkUtil {

    private static final String TAG = NetworkUtil.class.getSimpleName();

    private static final String KEY_PARAM = "key";
    private static final String QUERY_PARAM = "q";
    private static final String SORT_PARAM = "sort";
    private static final String PAGE_PARAM = "page";
    private static final String RECIPE_ID_PARAM = "rId";

    public static final String SORT_BY_RATING = "r";
    public static final String SORT_BY_TRENDINGNESS = "t";

    private static OkHttpClient client = new OkHttpClient();

    static {
        System.loadLibrary("native-lib");
    }

    private native static String invokeNativeFunction();

    public static String getSearchUrl(Context context, String query, String sort, String page) throws IOException {
        HttpUrl weatherQueryUri = HttpUrl.parse(context.getString(R.string.api_url)).newBuilder()
            .addPathSegment("search")
            .addQueryParameter(KEY_PARAM, invokeNativeFunction())
            .addQueryParameter(QUERY_PARAM, query)
            .addQueryParameter(SORT_PARAM, sort)
            .addQueryParameter(PAGE_PARAM, page)
            .build();

        Log.i(TAG, weatherQueryUri.toString());

        Request request = new Request.Builder()
            .url(weatherQueryUri)
            .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static String getRecipeUrl(Context context, String recipeId) throws IOException {
        HttpUrl weatherQueryUri = HttpUrl.parse(context.getString(R.string.api_url)).newBuilder()
            .addPathSegment("get")
            .addQueryParameter(KEY_PARAM, invokeNativeFunction())
            .addQueryParameter(RECIPE_ID_PARAM, recipeId)
            .build();

        Log.i(TAG, weatherQueryUri.toString());

        Request request = new Request.Builder()
            .url(weatherQueryUri)
            .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
