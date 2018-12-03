package com.example.searchmeal.sync;

import com.example.searchmeal.data.RecipeItem;
import com.example.searchmeal.data.SearchItem;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchMealApi {

    String KEY_PARAM = "key";
    String QUERY_PARAM = "q";
    String SORT_PARAM = "sort";
    String PAGE_PARAM = "page";
    String RECIPE_ID_PARAM = "rId";

    String SORT_BY_RATING = "r";
    String SORT_BY_TRENDINGNESS = "t";

    @GET("search")
    Call<SearchItem> syncMeal(@Query(KEY_PARAM) String key, @Query(QUERY_PARAM) String query,
                              @Query(SORT_PARAM) String sort, @Query(PAGE_PARAM) String page);

    @GET("get")
    Call<RecipeItem> syncRecipe(@Query(KEY_PARAM) String key, @Query(RECIPE_ID_PARAM) String recipeId);
}
