package com.example.searchmeal.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.searchmeal.data.SearchItem;
import com.example.searchmeal.utilities.SearchMealJsonUtil;

import org.json.JSONException;

public class SearchMealSyncTask extends AsyncTask<String, Void, SearchItem> {

    private static final String TAG = SearchMealSyncTask.class.getSimpleName();

    private SearchMealOnPostExecuteHandler mSearchMealHandler;
    private Context mContext;

    public interface SearchMealOnPostExecuteHandler {
        void onReceive(SearchItem searchItem);
    }

    public SearchMealSyncTask(SearchMealOnPostExecuteHandler searchMealHandler, Context context) {
        mSearchMealHandler = searchMealHandler;
        mContext = context;
    }

    @Override
    protected SearchItem doInBackground(String[] params) {
        String result = SearchMealSync.syncMeal(mContext, params[0], params[1], params[2]);
        SearchItem searchItem = null;
        try {
            if (result != null && !isCancelled()) searchItem = SearchMealJsonUtil.getSearchMealValuesFromJson(result);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
        return searchItem;
    }

    @Override
    protected void onPostExecute(SearchItem result) {
        super.onPostExecute(result);
        mSearchMealHandler.onReceive(result);
    }
}
