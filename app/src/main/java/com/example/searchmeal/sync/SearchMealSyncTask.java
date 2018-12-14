package com.example.searchmeal.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.searchmeal.data.SearchItem;

public class SearchMealSyncTask extends AsyncTask<String, Void, SearchItem> {

    private static final String TAG = SearchMealSyncTask.class.getSimpleName();

    private SearchMealOnPostExecuteHandler mSearchMealHandler;
    private Context mContext;

    public interface SearchMealOnPostExecuteHandler {
        void onReceive(SearchItem searchItem);
    }

    public SearchMealSyncTask(SearchMealOnPostExecuteHandler searchMealHandler) {
        mSearchMealHandler = searchMealHandler;
        mContext = (Context) mSearchMealHandler;
    }

    @Override
    protected SearchItem doInBackground(String[] params) {
        SearchItem searchItem = null;
        if (!isCancelled()) searchItem = SearchMealSync.syncMeal(mContext, params[0], params[1], params[2]);
        Log.e(TAG, searchItem.toString());
        return searchItem;
    }

    @Override
    protected void onPostExecute(SearchItem result) {
        super.onPostExecute(result);
        mSearchMealHandler.onReceive(result);
    }
}
