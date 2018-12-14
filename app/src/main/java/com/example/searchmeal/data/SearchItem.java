package com.example.searchmeal.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SearchItem implements Parcelable {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("recipes")
    @Expose
    private List<Recipe> recipes;

    public SearchItem() {
        recipes = new ArrayList<>();
    }

    protected SearchItem(Parcel in) {
        count = in.readInt();
        recipes = in.readArrayList(Recipe.class.getClassLoader());
    }

    public static final Creator<SearchItem> CREATOR = new Creator<SearchItem>() {
        @Override
        public SearchItem createFromParcel(Parcel in) {
            return new SearchItem(in);
        }

        @Override
        public SearchItem[] newArray(int size) {
            return new SearchItem[size];
        }
    };

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(count);
        dest.writeList(recipes);
    }
}
