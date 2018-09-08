package com.example.searchmeal.utilities;

import com.example.searchmeal.data.Recipe;
import com.example.searchmeal.data.SearchItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchMealJsonUtil {

    private static final String SM_COUNT = "count";
    private static final String SM_RECIPES = "recipes";
    private static final String SM_PUBLISHER = "publisher";
    private static final String SM_SOCIAL_RANK = "social_rank";
    private static final String SM_F2F_URL = "f2f_url";
    private static final String SM_PUBLISHER_URL = "publisher_url";
    private static final String SM_TITLE = "title";
    private static final String SM_SOURCE_URL = "source_url";
    private static final String SM_IMAGE_URL = "image_url";
    private static final String SM_RECIPE_ID = "recipe_id";
    private static final String SM_RECIPE = "recipe";
    private static final String SM_INGREDIENTS = "ingredients";

    public static SearchItem getSearchMealValuesFromJson(String searchMealJsonStr) throws JSONException {

        JSONObject searchMealJson = new JSONObject(searchMealJsonStr);
        int count = searchMealJson.getInt(SM_COUNT);
        JSONArray jsArray = searchMealJson.getJSONArray(SM_RECIPES);
        List<Recipe> recipes = new ArrayList<>();

        for(int i = 0; i < jsArray.length(); i++) {
            JSONObject jsObject = jsArray.getJSONObject(i);
            String recipeId = jsObject.getString(SM_RECIPE_ID);
            String publisher = jsObject.getString(SM_PUBLISHER);
            String socialRank = jsObject.getString(SM_SOCIAL_RANK);
            String f2fUrl = jsObject.getString(SM_F2F_URL);
            String publisherUrl = jsObject.getString(SM_PUBLISHER_URL);
            String title = jsObject.getString(SM_TITLE);
            String sourceUrl = jsObject.getString(SM_SOURCE_URL);
            String imageUrl = jsObject.getString(SM_IMAGE_URL);

            Recipe recipe = new Recipe();
            recipe.setPublisher(publisher);
            recipe.setSocialRank(socialRank);
            recipe.setF2fUrl(f2fUrl);
            recipe.setPublisherUrl(publisherUrl);
            recipe.setTitle(title);
            recipe.setSourceUrl(sourceUrl);
            recipe.setImageUrl(imageUrl);
            recipe.setRecipeId(recipeId);

            recipes.add(recipe);
        }

        SearchItem searchItem = new SearchItem();
        searchItem.setCount(count);
        searchItem.setRecipes(recipes);

        return searchItem;
    }

    public static Recipe getRecipeValueFromJson(String recipeJsonStr) throws JSONException {

        JSONObject searchMealJson = new JSONObject(recipeJsonStr);
        JSONObject recipeJson = searchMealJson.getJSONObject(SM_RECIPE);

        String publisher = recipeJson.getString(SM_PUBLISHER);
        String socialRank = recipeJson.getString(SM_SOCIAL_RANK);
        String f2fUrl = recipeJson.getString(SM_F2F_URL);
        String publisherUrl = recipeJson.getString(SM_PUBLISHER_URL);
        String title = recipeJson.getString(SM_TITLE);
        String sourceUrl = recipeJson.getString(SM_SOURCE_URL);
        String imageUrl = recipeJson.getString(SM_IMAGE_URL);

        ArrayList<String> ingredients = new ArrayList<>();
        JSONArray jsonIngredients = recipeJson.getJSONArray(SM_INGREDIENTS);
        for(int i= 0; i < jsonIngredients.length(); i++) {
            ingredients.add(jsonIngredients.getString(i));
        }

        Recipe recipe = new Recipe();
        recipe.setPublisher(publisher);
        recipe.setSocialRank(socialRank);
        recipe.setF2fUrl(f2fUrl);
        recipe.setPublisherUrl(publisherUrl);
        recipe.setTitle(title);
        recipe.setSourceUrl(sourceUrl);
        recipe.setImageUrl(imageUrl);
        recipe.setIngredients(ingredients);

        return recipe;
    }
}
