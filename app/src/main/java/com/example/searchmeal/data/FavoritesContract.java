package com.example.searchmeal.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavoritesContract {

    public static final String CONTENT_AUTHORITY = "com.example.searchmeal";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FAVORITES = "favorites";

    public static final class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES).build();

        public static final String TABLE_NAME = "favorites";

        public static final String COLUMN_RECIPE_ID = "recipe_id";
        public static final String COLUMN_PUBLISHER = "publisher";
        public static final String COLUMN_SOCIAL_RANK = "social_rank";
        public static final String COLUMN_F2F_URL = "f2f_url";
        public static final String COLUMN_PUBLISHER_URL = "publisher_url";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SOURCE_URL = "source_url";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_INGREDIENTS = "ingredients";

        public static Uri buildFavoritesUriWithRecipeId(String recipeId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(recipeId)
                    .build();
        }
    }
}
