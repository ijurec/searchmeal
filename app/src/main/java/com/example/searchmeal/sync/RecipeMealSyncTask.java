package com.example.searchmeal.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.searchmeal.data.Recipe;
import com.example.searchmeal.utilities.SearchMealJsonUtil;

import org.json.JSONException;

public class RecipeMealSyncTask extends AsyncTask<String, Void, Recipe> {

    private static final String TAG = RecipeMealSyncTask.class.getSimpleName();

    public interface RecipeMealOnPostExecuteHandler {
        void onReceive(Recipe recipe);
    }

    private Context mContext;
    private RecipeMealOnPostExecuteHandler mRecipeHandler;

    public RecipeMealSyncTask(Context context, RecipeMealOnPostExecuteHandler recipeHandler) {
        mContext = context;
        mRecipeHandler = recipeHandler;
    }

    @Override
    protected Recipe doInBackground(String... strings) {
        String result = SearchMealSync.syncRecipe(mContext, strings[0]);
        Recipe recipe = null;
        try {
            if (result != null && !isCancelled()) recipe = SearchMealJsonUtil.getRecipeValueFromJson(result);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
        return recipe;
    }

    @Override
        protected void onPostExecute(Recipe recipe) {
            super.onPostExecute(recipe);
            mRecipeHandler.onReceive(recipe);
    }
}
