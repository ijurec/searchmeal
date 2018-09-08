package com.example.searchmeal.utilities;

import android.content.ContentValues;

import com.example.searchmeal.data.Recipe;

import static com.example.searchmeal.data.FavoritesContract.FavoriteEntry;

public class SearchMealContentValueUtil {

    public static ContentValues getRecipeContentValues(Recipe recipe) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoriteEntry.COLUMN_RECIPE_ID, recipe.getRecipeId());
        contentValues.put(FavoriteEntry.COLUMN_PUBLISHER, recipe.getPublisher());
        contentValues.put(FavoriteEntry.COLUMN_SOCIAL_RANK, recipe.getSocialRank());
        contentValues.put(FavoriteEntry.COLUMN_F2F_URL, recipe.getF2fUrl());
        contentValues.put(FavoriteEntry.COLUMN_PUBLISHER_URL, recipe.getPublisherUrl());
        contentValues.put(FavoriteEntry.COLUMN_TITLE, recipe.getTitle());
        contentValues.put(FavoriteEntry.COLUMN_SOURCE_URL, recipe.getSourceUrl());
        contentValues.put(FavoriteEntry.COLUMN_IMAGE_URL, recipe.getImageUrl());
        contentValues.put(FavoriteEntry.COLUMN_INGREDIENTS, recipe.getIngredients().toString());

        return contentValues;
    }
}
