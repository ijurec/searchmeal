package com.example.searchmeal.data;

import android.content.SearchRecentSuggestionsProvider;

public class SearchMealSuggestionProvider extends SearchRecentSuggestionsProvider {

    public final static String AUTHORITY = "com.example.searchmeal.data.SearchMealSuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SearchMealSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
