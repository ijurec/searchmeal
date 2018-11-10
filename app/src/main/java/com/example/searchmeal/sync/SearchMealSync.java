package com.example.searchmeal.sync;

import android.content.Context;
import android.util.Log;

import com.example.searchmeal.utilities.NetworkUtil;

import java.io.IOException;

public class SearchMealSync {

    private static final String TAG = SearchMealSync.class.getSimpleName();

    public static String syncMeal(Context context, String editSearchText, String spnFilterText, String page) {
        String result = null;
        try {
            result = NetworkUtil.getSearchUrl(context, editSearchText, spnFilterText, page);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        return result;
    }

    public static String syncRecipe(Context context, String recipeId) {
        String result = null;
        try {
            result = NetworkUtil.getRecipeUrl(context, recipeId);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        return result;
    }
}
