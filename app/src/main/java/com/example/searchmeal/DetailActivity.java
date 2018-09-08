package com.example.searchmeal;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.searchmeal.data.Recipe;
import com.example.searchmeal.sync.RecipeMealSyncTask;
import com.example.searchmeal.utilities.NumberUtil;
import com.example.searchmeal.utilities.SearchMealContentValueUtil;
import com.squareup.picasso.Picasso;
import com.example.searchmeal.data.FavoritesContract.FavoriteEntry;

public class DetailActivity extends AppCompatActivity implements RecipeMealSyncTask.RecipeMealOnPostExecuteHandler {

    private TextView mTitle;
    private ImageView mImageView;
    private TextView mRank;
    private TextView mIngredients;
    private TextView mPublisher;
    private ConstraintLayout mConstraintLayout;
    private ProgressBar mProgressBar;

    private RecipeMealSyncTask mRecipeMealSyncTask;
    private Recipe mRecipe;
    private String mRecipeId;
    private boolean mIsFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mTitle = findViewById(R.id.text_title_detail);
        mImageView = findViewById(R.id.image_view_detail);
        mRank = findViewById(R.id.text_rank_detail);
        mIngredients = findViewById(R.id.text_ingredients_detail);
        mPublisher = findViewById(R.id.text_publisher_detail);
        mConstraintLayout = findViewById(R.id.constraint_layout_recipe);
        mProgressBar = findViewById(R.id.recipe_loading_indicator);

        mRecipeId = getIntent().getStringExtra("recipe_id");
        mRecipeMealSyncTask = new RecipeMealSyncTask(this, this);
        mRecipeMealSyncTask.execute(mRecipeId);

        mConstraintLayout.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void onReceive(Recipe recipe) {
        if (recipe != null) {
            recipe.setRecipeId(mRecipeId);
            mRecipe = recipe;
            mTitle.setText(recipe.getTitle());

            Picasso.get().load(recipe.getImageUrl())
                    .fit()
                    .centerCrop()
//                    .placeholder(R.drawable.user_placeholder)
//                    .error(R.drawable.user_placeholder_error)
                    .into(mImageView);
            mRank.setText(NumberUtil.roundingRank(recipe.getSocialRank()));

            int arraySize = recipe.getIngredients().size();
            for (int i = 0; i < arraySize; i++) {
                String ingredient = recipe.getIngredients().get(i);
                if (i == (arraySize - 1)) {
                    mIngredients.append(ingredient);
                } else {
                    mIngredients.append(ingredient + "\n");
                }
            }

            mPublisher.setClickable(true);
            mPublisher.setMovementMethod(LinkMovementMethod.getInstance());
            String text = "<a href='" + recipe.getSourceUrl() + "'>  " + "View on " + recipe.getPublisher() + "  </a>";
            mPublisher.setText(Html.fromHtml(text));

            mConstraintLayout.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            Toast.makeText(getApplicationContext(), "There isn't any result", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sharing_favorites_meal, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem menuItem = menu.findItem(R.id.action_add_remove_to_favorites);
        menuItem.setActionView(new ProgressBar(this));
        menuItem.getActionView().postDelayed(new Runnable() {

            @Override
            public void run() {

                Uri uri = FavoriteEntry.buildFavoritesUriWithRecipeId(mRecipeId);
                String[] projectionColumns = {FavoriteEntry._ID};
                Cursor cursor = getContentResolver().query(uri, projectionColumns,
                        null, null, null);
                if (cursor == null) {
                    return;
                } else if (cursor.getCount() == 1) {
                    mIsFavorite = true;
                }
                cursor.close();

                if (mIsFavorite) {
                    menuItem.setTitle(getString(R.string.action_remove_from_favorites));
                    menuItem.setIcon(R.drawable.ic_favorite_white_24dp);
                } else {
                    menuItem.setTitle(getString(R.string.action_add_to_favorites));
                    menuItem.setIcon(R.drawable.ic_favorite_border_white_24dp);
                }
                menuItem.setActionView(null);
            }
        }, 0);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_share_meal:
                if (mRecipe.getF2fUrl() != null) {
                    Intent shareIntent = createShareForecastIntent();
                    startActivity(shareIntent);
                } else {
                    Toast.makeText(this, "There are not data to share", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_add_remove_to_favorites:
                if (mIsFavorite) {
                    Uri uri = FavoriteEntry.buildFavoritesUriWithRecipeId(mRecipeId);
                    getContentResolver().delete(uri,null, null);
                    item.setTitle(getString(R.string.action_add_to_favorites));
                    item.setIcon(R.drawable.ic_favorite_border_white_24dp);
                    mIsFavorite = false;
                } else {
                    getContentResolver().insert(FavoriteEntry.CONTENT_URI,
                            SearchMealContentValueUtil.getRecipeContentValues(mRecipe));
                    item.setTitle(getString(R.string.action_remove_from_favorites));
                    item.setIcon(R.drawable.ic_favorite_white_24dp);
                    mIsFavorite = true;
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mRecipe.getF2fUrl())
                .getIntent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        }

        return shareIntent;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecipeMealSyncTask.cancel(true);
    }
}
