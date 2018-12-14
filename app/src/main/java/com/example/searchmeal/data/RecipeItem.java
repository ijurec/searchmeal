package com.example.searchmeal.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecipeItem {

    @SerializedName("recipe")
    @Expose
    private Recipe recipe;

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
}
