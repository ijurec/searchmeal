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

    private TextView mTextSpinner;
    private Spinner mSpinner;
    private RecyclerView mRecyclerView;
    private TextView mTextEmptyData;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefresh;

    private SearchMealSyncTask mSearchTask;
    private SearchMealAdapter mSearchMealAdapter;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar_search_meal);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mTextSpinner = findViewById(R.id.label_spinner_search_meal);
        mSpinner = findViewById(R.id.spinner_search_meal);
        mRecyclerView = findViewById(R.id.recycler_view_search_meal);
        mTextEmptyData = findViewById(R.id.label_search_empty);
        mProgressBar = findViewById(R.id.loading_indicator_search_meal);
        mSwipeRefresh = findViewById(R.id.swipe_refresh_search_meal);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                layoutManager.getOrientation()));
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

        mSearchTask = new SearchMealSyncTask(this, this);
        mSearchTask.execute(searchQuery, spnFilterText, "");

        mTextSpinner.setVisibility(View.INVISIBLE);
        mSpinner.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mTextEmptyData.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onReceive(SearchItem searchItem) {
        mTextSpinner.setVisibility(View.VISIBLE);
        mSpinner.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);

        if (searchItem != null && searchItem.getCount() != 0) {
            mSearchMealAdapter.swapCursor(searchItem);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "There isn't any result", Toast.LENGTH_SHORT).show();
            mTextEmptyData.setVisibility(View.VISIBLE);
        }
        mSwipeRefresh.setRefreshing(false);
    }

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
        searchRequest(query, mSpinner.getSelectedItemPosition());
    }
}
