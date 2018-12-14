package com.example.searchmeal.sync;

import android.content.Context;
import android.util.Log;

import com.example.searchmeal.data.Recipe;
import com.example.searchmeal.data.RecipeItem;
import com.example.searchmeal.data.SearchItem;
import com.example.searchmeal.utilities.NetworkUtil;
import com.google.gson.Gson;

import java.io.IOException;

public class SearchMealSync {

    private static final String TAG = SearchMealSync.class.getSimpleName();
    private static Gson sGson = new Gson();

    public static SearchItem syncMeal(Context context, String editSearchText, String spnFilterText, String page) {
        SearchItem searchItem = null;
        String result;
        try {
            result = NetworkUtil.getSearchUrl(context, editSearchText, spnFilterText, page);
            searchItem = sGson.fromJson(result, SearchItem.class);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        return searchItem;
    }

    public static Recipe syncRecipe(Context context, String recipeId) {
        RecipeItem recipeItem;
        Recipe recipe = null;
        String result;
        try {
            result = NetworkUtil.getRecipeUrl(context, recipeId);
            recipeItem = sGson.fromJson(result, RecipeItem.class);
            recipe = recipeItem.getRecipe();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        return recipe;
    }
}
