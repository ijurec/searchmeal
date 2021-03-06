package com.example.searchmeal.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Recipe implements Parcelable {

    @SerializedName("recipe_id")
    @Expose
    private String recipeId;
    @SerializedName("publisher")
    @Expose
    private String publisher;
    @SerializedName("social_rank")
    @Expose
    private String socialRank;
    @SerializedName("f2f_url")
    @Expose
    private String f2fUrl;
    @SerializedName("publisher_url")
    @Expose
    private String publisherUrl;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("source_url")
    @Expose
    private String sourceUrl;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;
    @SerializedName("ingredients")
    @Expose
    private List<String> ingredients;

    public Recipe () {

    }

    protected Recipe(Parcel in) {
        recipeId = in.readString();
        publisher = in.readString();
        socialRank = in.readString();
        f2fUrl = in.readString();
        publisherUrl = in.readString();
        title = in.readString();
        sourceUrl = in.readString();
        imageUrl = in.readString();
        ingredients = in.readArrayList(String.class.getClassLoader());
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public String getRecipeId() {
        return recipeId;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getSocialRank() {
        return socialRank;
    }

    public String getF2fUrl() {
        return f2fUrl;
    }

    public String getPublisherUrl() {
        return publisherUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setSocialRank(String socialRank) {
        this.socialRank = socialRank;
    }

    public void setF2fUrl(String f2fUrl) {
        this.f2fUrl = f2fUrl;
    }

    public void setPublisherUrl(String publisherUrl) {
        this.publisherUrl = publisherUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(recipeId);
        dest.writeString(publisher);
        dest.writeString(socialRank);
        dest.writeString(f2fUrl);
        dest.writeString(publisherUrl);
        dest.writeString(title);
        dest.writeString(sourceUrl);
        dest.writeString(imageUrl);
        dest.writeList(ingredients);
    }
}
