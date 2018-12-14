package com.example.searchmeal.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.searchmeal.data.Recipe;

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
        Recipe recipe = null;
        if (!isCancelled()) recipe = SearchMealSync.syncRecipe(mContext, strings[0]);
        Log.e(TAG, recipe.toString());
        return recipe;
    }

    @Override
        protected void onPostExecute(Recipe recipe) {
            super.onPostExecute(recipe);
            mRecipeHandler.onReceive(recipe);
    }
}
