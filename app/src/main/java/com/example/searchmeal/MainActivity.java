package com.example.searchmeal;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.searchmeal.data.Recipe;
import com.example.searchmeal.data.SearchItem;
import com.example.searchmeal.sync.SearchMealSyncTask;
import com.example.searchmeal.utilities.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchMealSyncTask.SearchMealOnPostExecuteHandler,
        SearchMealAdapter.SearchMealAdapterOnClickHandler, AdapterView.OnItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener {

    public static final int DEFAULT_POSITION = 0;

    private Spinner mSpnFilter;
    private RecyclerView mRecyclerView;
    private TextView mTextEmptyData;
    private ProgressBar mProgressBar;
    private ProgressBar mHorizontalProgressBar;
    private SwipeRefreshLayout mSwipeRefresh;

    private SearchMealSyncTask mSearchTask;
    private SearchMealSyncTask mSearchSuggestionsTask;
    private SearchMealAdapter mSearchMealAdapter;

    private int mPageNumber = 1;
    private boolean isLoadingMoreData;
    private boolean isRefreshingData;
    private SearchView mSearchView;
    private List<String> suggestions = new ArrayList<>();
    private CursorAdapter mSuggestionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_search_meal);
        if (toolbar != null) setSupportActionBar(toolbar);

        mSpnFilter = findViewById(R.id.spinner_search_meal);
        mRecyclerView = findViewById(R.id.recycler_view_search_meal);
        mTextEmptyData = findViewById(R.id.label_search_empty);
        mProgressBar = findViewById(R.id.loading_indicator_search_meal);
        mHorizontalProgressBar = findViewById(R.id.horizontal_loading_indicator_search_meal);
        mSwipeRefresh = findViewById(R.id.swipe_refresh_search_meal);

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager =
                    new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        StaggeredGridLayoutManager gridLayoutManager =
//                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                layoutManager.getOrientation()));
        mRecyclerView.addOnScrollListener(onScrollListener);
        mSearchMealAdapter = new SearchMealAdapter(this);
        mRecyclerView.setAdapter(mSearchMealAdapter);

        mSwipeRefresh.setOnRefreshListener(this);

        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_filter_array, R.layout.spinner_layout);
        sortAdapter.setDropDownViewResource(R.layout.spinner_layout);
        mSpnFilter.setOnItemSelectedListener(this);
        mSpnFilter.setAdapter(sortAdapter);
    }

    private void searchRequest(int spnFilterPosition) {
        String spnFilterText;
        if (spnFilterPosition == 0) {
            spnFilterText = NetworkUtil.SORT_BY_RATING;
        } else {
            spnFilterText = NetworkUtil.SORT_BY_TRENDINGNESS;
        }

        if (isRefreshingData && isLoadingMoreData) {
            if (mHorizontalProgressBar.getVisibility() == View.VISIBLE) {
                mPageNumber = 1;
                mHorizontalProgressBar.setVisibility(View.INVISIBLE);
                mSearchTask.cancel(true);
                mSearchTask = new SearchMealSyncTask(this, this);
                mSearchTask.execute("", spnFilterText, String.valueOf(mPageNumber++));
            }
            isLoadingMoreData = false;
        } else if (isRefreshingData) {
            mPageNumber = 1;

            mSearchTask = new SearchMealSyncTask(this, this);
            mSearchTask.execute("", spnFilterText, String.valueOf(mPageNumber++));
        } else if (isLoadingMoreData) {
            mSearchTask = new SearchMealSyncTask(this, this);
            mSearchTask.execute("", spnFilterText, String.valueOf(mPageNumber++));

            mHorizontalProgressBar.setVisibility(View.VISIBLE);
        } else {
            mSearchTask = new SearchMealSyncTask(this, this);
            mSearchTask.execute("", spnFilterText, String.valueOf(mPageNumber++));

            mRecyclerView.setVisibility(View.INVISIBLE);
            mTextEmptyData.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    @Override
    public void onReceive(SearchItem searchItem) {
        mProgressBar.setVisibility(View.INVISIBLE);
        mHorizontalProgressBar.setVisibility(View.INVISIBLE);

        if (isLoadingMoreData && searchItem != null && searchItem.getCount() != 0) {
            mSearchMealAdapter.swapCursor(searchItem);
            mRecyclerView.setVisibility(View.VISIBLE);
            isLoadingMoreData = false;
        } else if (isRefreshingData && searchItem != null && searchItem.getCount() != 0) {
            isRefreshingData = false;
            mSearchMealAdapter.swapCursor(null);
            mSearchMealAdapter.swapCursor(searchItem);
            mRecyclerView.setVisibility(View.VISIBLE);
            mTextEmptyData.setVisibility(View.INVISIBLE);
            mRecyclerView.scrollToPosition(DEFAULT_POSITION);
        } else if (searchItem != null && searchItem.getCount() != 0) {
            mSearchMealAdapter.swapCursor(null);
            mSearchMealAdapter.swapCursor(searchItem);
            mRecyclerView.setVisibility(View.VISIBLE);
            mRecyclerView.scrollToPosition(DEFAULT_POSITION);
        } else {
            Toast.makeText(this, "There isn't any result", Toast.LENGTH_SHORT).show();
            if (mSearchMealAdapter.getSearchItem().getRecipes().size() == 0) {
                mTextEmptyData.setVisibility(View.VISIBLE);
            }
        }

        mSwipeRefresh.setRefreshing(false);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mPageNumber = 1;
        searchRequest(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onRefresh() {
        isRefreshingData = true;
        mPageNumber = 1;
        searchRequest(mSpnFilter.getSelectedItemPosition());
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!recyclerView.canScrollVertically(1) && dy != 0) {
                // Load more results here
                if (mHorizontalProgressBar.getVisibility() != View.VISIBLE) {
                    isLoadingMoreData = true;
                    searchRequest(mSpnFilter.getSelectedItemPosition());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.favorites_meal, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search_meal).getActionView();
        // Assumes current activity is the searchable activity
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        mSuggestionAdapter = new SimpleCursorAdapter(this,
                R.layout.searchview_list_item,
                null,
                new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1},
                new int[]{android.R.id.text1},
                0);

        mSearchView.setSuggestionsAdapter(mSuggestionAdapter);
        mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {

            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                mSearchView.setQuery(suggestions.get(position), true);
                mSearchView.clearFocus();
                return true;
            }
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                long spnItemId = mSpnFilter.getSelectedItemId();

                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.setAction(Intent.ACTION_SEARCH);
                intent.putExtra(SearchManager.QUERY, query);
                Bundle appData = new Bundle();
                appData.putLong(SearchActivity.SPINNER_FILTER_ID, spnItemId);
                intent.putExtra(SearchManager.APP_DATA, appData);
                startActivity(intent);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && !newText.equals("")) {
                    searchSuggestions(newText);
                }
                return true;
            }
        });

        mSearchView.setQueryHint(getString(R.string.label_edit_hint));
        mSearchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
//        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryRefinementEnabled(true);

        return true;
    }

    private void searchSuggestions(String newText) {
        if (mSearchSuggestionsTask != null) {
            mSearchSuggestionsTask.cancel(true);
        }
        mSearchSuggestionsTask = new SearchMealSyncTask(new SearchMealSyncTask.SearchMealOnPostExecuteHandler() {

            @Override
            public void onReceive(SearchItem searchItem) {
                if (searchItem != null && searchItem.getCount() != 0) {
                    suggestions.clear();
                    for (Recipe recipe : searchItem.getRecipes()) {
                        suggestions.add(recipe.getTitle());
                    }

                    String[] columns = { BaseColumns._ID,
                            SearchManager.SUGGEST_COLUMN_TEXT_1 };
                    MatrixCursor cursor = new MatrixCursor(columns);
                    for (int i = 0; i < suggestions.size(); i++) {
                        String[] tmp = {Integer.toString(i), suggestions.get(i)};
                        cursor.addRow(tmp);
                    }
                    mSuggestionAdapter.swapCursor(cursor);
                } else {
                    Toast.makeText(MainActivity.this, "There isn't any result", Toast.LENGTH_SHORT).show();
                }
                hideProgressBar(mSearchView);
            }
        }, MainActivity.this);
        mSearchSuggestionsTask.execute(newText, NetworkUtil.SORT_BY_RATING, String.valueOf(0));
        View viewSearchPlate = mSearchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        showProgressBar(viewSearchPlate, MainActivity.this);
        mSuggestionAdapter.swapCursor(null);
    }

    public void showProgressBar(View viewSearchPlate, Context context) {
        if (viewSearchPlate.findViewById(R.id.searchview_progress_bar) != null)
            viewSearchPlate.findViewById(R.id.searchview_progress_bar).animate().setDuration(200).alpha(1).start();
        else {
            View v = LayoutInflater.from(context).inflate(R.layout.searchview_loading_icon, null);
            ((ViewGroup) viewSearchPlate).addView(v, 1);
        }
    }

    public void hideProgressBar(View viewSearchPlate) {
        if (viewSearchPlate.findViewById(R.id.searchview_progress_bar) != null) {
            viewSearchPlate.findViewById(R.id.searchview_progress_bar).animate().setDuration(200).alpha(0).start();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorites_meal:
                startActivity(new Intent(this, FavoritesActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
