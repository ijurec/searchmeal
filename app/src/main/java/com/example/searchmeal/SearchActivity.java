package com.example.searchmeal;

import android.app.SearchManager;
import android.content.Intent;
import android.provider.SearchRecentSuggestions;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.searchmeal.data.SearchItem;
import com.example.searchmeal.data.SearchMealSuggestionProvider;
import com.example.searchmeal.sync.SearchMealSyncTask;
import com.example.searchmeal.utilities.NetworkUtil;

public class SearchActivity extends AppCompatActivity implements SearchMealAdapter.SearchMealAdapterOnClickHandler,
        SearchMealSyncTask.SearchMealOnPostExecuteHandler, AdapterView.OnItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener {

    public static final String SPINNER_FILTER_ID = "filter_text";

    private Spinner mSpinner;
    private RecyclerView mRecyclerView;
    private TextView mTextEmptyData;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefresh;
    private ProgressBar mHorizontalProgressBar;

    private SearchMealSyncTask mSearchTask;
    private SearchMealAdapter mSearchMealAdapter;
    private String query;
    private boolean isRefreshingData;
    private boolean isLoadingMoreData;
    private int mPageNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar_search_meal);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mSpinner = findViewById(R.id.spinner_search_meal);
        mRecyclerView = findViewById(R.id.recycler_view_search_meal);
        mTextEmptyData = findViewById(R.id.label_search_empty);
        mProgressBar = findViewById(R.id.loading_indicator_search_meal);
        mHorizontalProgressBar = findViewById(R.id.horizontal_loading_indicator_search_meal);
        mSwipeRefresh = findViewById(R.id.swipe_refresh_search_meal);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                layoutManager.getOrientation()));
        mRecyclerView.addOnScrollListener(onScrollListener);
        mSearchMealAdapter = new SearchMealAdapter(this);
        mRecyclerView.setAdapter(mSearchMealAdapter);

        mSwipeRefresh.setOnRefreshListener(this);

        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_filter_array, R.layout.spinner_layout);
        sortAdapter.setDropDownViewResource(R.layout.spinner_layout);
        mSpinner.setOnItemSelectedListener(this);
        mSpinner.setAdapter(sortAdapter);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SearchMealSuggestionProvider.AUTHORITY, SearchMealSuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
            Bundle appData = intent.getBundleExtra(SearchManager.APP_DATA);
            if (appData != null) {
                int spnFilterId = (int) appData.getLong(SPINNER_FILTER_ID);
                mSpinner.setSelection(spnFilterId);
            }
        }
    }

    private void searchRequest(String searchQuery, int spnFilterPosition) {
        String spnFilterText;
        if (spnFilterPosition == 0) {
            spnFilterText = NetworkUtil.SORT_BY_RATING;
        } else {
            spnFilterText = NetworkUtil.SORT_BY_TRENDINGNESS;
        }

        mSpinner.setEnabled(false);

        if (isRefreshingData && isLoadingMoreData) {
            if (mHorizontalProgressBar.getVisibility() == View.VISIBLE) {
                mPageNumber = 1;
                mSearchTask.cancel(true);
                mSearchTask = new SearchMealSyncTask(this);
                mSearchTask.execute(searchQuery, spnFilterText, String.valueOf(mPageNumber++));

                mHorizontalProgressBar.setVisibility(View.INVISIBLE);
            }
            isLoadingMoreData = false;
        } else if (isRefreshingData) {
            mPageNumber = 1;
            mSearchTask = new SearchMealSyncTask(this);
            mSearchTask.execute(searchQuery, spnFilterText, String.valueOf(mPageNumber++));
        } else if (isLoadingMoreData) {
            mSearchTask = new SearchMealSyncTask(this);
            mSearchTask.execute(searchQuery, spnFilterText, String.valueOf(mPageNumber++));

            mHorizontalProgressBar.setVisibility(View.VISIBLE);
        } else {
            mPageNumber = 1;
            mSearchTask = new SearchMealSyncTask(this);
            mSearchTask.execute(searchQuery, spnFilterText, String.valueOf(mPageNumber++));

            mRecyclerView.setVisibility(View.INVISIBLE);
            mTextEmptyData.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    @Override
    public void onReceive(SearchItem searchItem) {
        mSpinner.setEnabled(true);
        if (isLoadingMoreData && searchItem != null && searchItem.getCount() != 0) {
            mSearchMealAdapter.swapCursor(searchItem);
            mRecyclerView.setVisibility(View.VISIBLE);
            isLoadingMoreData = false;
        } else if (isRefreshingData && searchItem != null && searchItem.getCount() != 0) {
            mSearchMealAdapter.swapCursor(null);
            mSearchMealAdapter.swapCursor(searchItem);
            mRecyclerView.setVisibility(View.VISIBLE);
            mTextEmptyData.setVisibility(View.INVISIBLE);
            mRecyclerView.scrollToPosition(MainActivity.DEFAULT_POSITION);
            isRefreshingData = false;
        } else if (searchItem != null && searchItem.getCount() != 0) {
            mSearchMealAdapter.swapCursor(null);
            mSearchMealAdapter.swapCursor(searchItem);
            mRecyclerView.setVisibility(View.VISIBLE);
            mRecyclerView.scrollToPosition(MainActivity.DEFAULT_POSITION);
        } else {
            isLoadingMoreData = false;
            isRefreshingData = false;
            Toast.makeText(this, "There isn't any result", Toast.LENGTH_SHORT).show();
            if (mSearchMealAdapter.getSearchItem().getRecipes().size() == 0) {
                mTextEmptyData.setVisibility(View.VISIBLE);
            }
        }

        mProgressBar.setVisibility(View.INVISIBLE);
        mHorizontalProgressBar.setVisibility(View.INVISIBLE);
        mSwipeRefresh.setRefreshing(false);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!recyclerView.canScrollVertically(1) && dy != 0) {
                // Load more results here
                if (mHorizontalProgressBar.getVisibility() != View.VISIBLE) {
                    isLoadingMoreData = true;
                    searchRequest(query, mSpinner.getSelectedItemPosition());
                }
            }
        }
    };

    @Override
    public void onClick(String recipeId) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("recipe_id", recipeId);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            searchRequest(query, position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onRefresh() {
        isRefreshingData = true;
        searchRequest(query, mSpinner.getSelectedItemPosition());
    }
}
