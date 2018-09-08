package com.example.searchmeal.sync;

import android.content.Context;
import android.util.Log;

import com.example.searchmeal.utilities.NetworkUtil;

import java.io.IOException;
import java.net.URL;

public class SearchMealSync {

    private static final String TAG = SearchMealSync.class.getSimpleName();

    public static String syncMeal(Context context, String editSearchText, String spnFilterText, String page) {

        URL searchMealUrl = NetworkUtil.getSearchUrl(context, editSearchText, spnFilterText, page);
        String result = null;
        try {
            result = NetworkUtil.getResponseFromHttpUrl(searchMealUrl);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        return result;
    }

    public static String syncRecipe(Context context, String recipeId) {

        URL recipeUrl = NetworkUtil.getRecipeUrl(context, recipeId);
        String result = null;
        try {
            result = NetworkUtil.getResponseFromHttpUrl(recipeUrl);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        return result;
    }
}
