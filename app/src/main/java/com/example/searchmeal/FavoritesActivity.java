package com.example.searchmeal;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.searchmeal.data.FavoritesContract;
import com.example.searchmeal.data.FavoritesContract.FavoriteEntry;
import com.example.searchmeal.data.Recipe;
import com.example.searchmeal.data.SearchItem;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        SearchMealAdapter.SearchMealAdapterOnClickHandler {

    private static final int ID_FORECAST_LOADER = 1;
    public static final String[] FAVORITES_PROJECTION = {
            FavoriteEntry.COLUMN_RECIPE_ID,
            FavoriteEntry.COLUMN_PUBLISHER,
            FavoriteEntry.COLUMN_SOCIAL_RANK,
            FavoriteEntry.COLUMN_TITLE,
            FavoriteEntry.COLUMN_IMAGE_URL
    };

    private RecyclerView mRecyclerView;
    private SearchMealAdapter mSearchMealAdapter;
    private ProgressBar mProgressBar;
    private TextView mTextEmptyData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mRecyclerView = findViewById(R.id.recycler_view_favorites);
        mProgressBar = findViewById(R.id.loading_indicator_favorites);
        mTextEmptyData = findViewById(R.id.label_favorites_empty);

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                layoutManager.getOrientation()));
        mSearchMealAdapter = new SearchMealAdapter(this);
        mRecyclerView.setAdapter(mSearchMealAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete

                String recipeIdToDelete = (String) viewHolder.itemView.getTag();
                Uri uri = FavoritesContract.FavoriteEntry.buildFavoritesUriWithRecipeId(recipeIdToDelete);
                getContentResolver().delete(uri, null, null);

                getSupportLoaderManager().restartLoader(ID_FORECAST_LOADER, null, FavoritesActivity.this);
            }
        }).attachToRecyclerView(mRecyclerView);

        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);

        getSupportLoaderManager().initLoader(ID_FORECAST_LOADER, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        switch (id) {
            case ID_FORECAST_LOADER:
                Uri favoritesQueryUri = FavoriteEntry.CONTENT_URI;
                return new CursorLoader(this,
                        favoritesQueryUri,
                        FAVORITES_PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data.getCount() == 0) {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
            mTextEmptyData.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
            SearchItem searchItem = new SearchItem();
            ArrayList<Recipe> recipes = new ArrayList<>();
            searchItem.setCount(data.getCount());
            for (int i = 0; i < data.getCount(); i++) {
                data.moveToPosition(i);

                Recipe recipe = new Recipe();
                recipe.setRecipeId(data.getString(data.getColumnIndex(FavoriteEntry.COLUMN_RECIPE_ID)));
                recipe.setPublisher(data.getString(data.getColumnIndex(FavoriteEntry.COLUMN_PUBLISHER)));
                recipe.setSocialRank(data.getString(data.getColumnIndex(FavoriteEntry.COLUMN_SOCIAL_RANK)));
                recipe.setTitle(data.getString(data.getColumnIndex(FavoriteEntry.COLUMN_TITLE)));
                recipe.setImageUrl(data.getString(data.getColumnIndex(FavoriteEntry.COLUMN_IMAGE_URL)));

                recipes.add(recipe);
            }

            searchItem.setRecipes(recipes);
            mSearchMealAdapter.swapCursor(null);
            mSearchMealAdapter.swapCursor(searchItem);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mSearchMealAdapter.swapCursor(null);
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
}
