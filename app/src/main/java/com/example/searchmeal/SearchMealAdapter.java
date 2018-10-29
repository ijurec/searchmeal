package com.example.searchmeal;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.searchmeal.data.Recipe;
import com.example.searchmeal.data.SearchItem;
import com.example.searchmeal.utilities.NumberUtil;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class SearchMealAdapter extends RecyclerView.Adapter<SearchMealAdapter.SearchMealAdapterViewHolder> {

    private static final String TAG = SearchMealAdapter.class.getSimpleName();

    private SearchItem mSearchItem;

    /**
     * The interface that receives onClick messages.
     */
    public interface SearchMealAdapterOnClickHandler {
        void onClick(String recipeId);
    }

    private final SearchMealAdapterOnClickHandler mSearchMealOnClickHandler;

    SearchMealAdapter (SearchMealAdapterOnClickHandler searchMealOnClickListener) {
        mSearchMealOnClickHandler = searchMealOnClickListener;
        mSearchItem = new SearchItem();
    }

    @NonNull
    @Override
    public SearchMealAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = R.layout.layout_list_item;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        view.setFocusable(true);

        return new SearchMealAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchMealAdapterViewHolder holder, int position) {

        final Recipe recipe = mSearchItem.getRecipes().get(position);

        holder.progressItemView.setVisibility(View.VISIBLE);
        Picasso.get()
                .load(recipe.getImageUrl())
                .fit()
                .centerCrop()
//                .placeholder(R.drawable.user_placeholder)
//                .error(R.drawable.user_placeholder_error)
                .transform(new RoundedCornersTransformation(7, 0))
                .into(holder.imageListItemView, new com.squareup.picasso.Callback() {

                    @Override
                    public void onSuccess() {
                        if (holder.progressItemView != null) {
                            holder.progressItemView.setVisibility(View.GONE);
                        }
                        Log.i(TAG, "Successfully loaded the image from: " + recipe.getImageUrl());
                    }

                    @Override
                    public void onError(Exception e) {
                        if (holder.progressItemView != null) {
                            holder.progressItemView.setVisibility(View.GONE);
                        }
                        Toast.makeText(holder.itemView.getContext(),
                                "Error during loading the image: " + recipe.getImageUrl(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, e.toString());
                    }
                });
        holder.textTitleItemView.setText(recipe.getTitle());
        String recipePublisher = holder.itemView.getContext().getString(R.string.publisher) + recipe.getPublisher();
        holder.textPublisherItemView.setText(recipePublisher);
            holder.textRatingItemView.setText(NumberUtil.roundingRank(recipe.getSocialRank()));
            holder.itemView.setTag(recipe.getRecipeId());
        }

    @Override
     public int getItemCount() {
        return mSearchItem == null ? 0 : mSearchItem.getRecipes().size();
    }

    public void swapCursor(SearchItem searchItem) {
        if (searchItem != null) {
            mSearchItem.getRecipes().addAll(searchItem.getRecipes());
        } else {
            mSearchItem.getRecipes().clear();
        }
        notifyDataSetChanged();
    }

    class SearchMealAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageListItemView;
        ProgressBar progressItemView;
        TextView textTitleItemView;
        TextView textPublisherItemView;
        TextView textRatingItemView;

        SearchMealAdapterViewHolder(View itemView) {
            super(itemView);

            imageListItemView = itemView.findViewById(R.id.image_list_item_view);
            progressItemView = itemView.findViewById(R.id.progressBar_image_item_view);
            textTitleItemView = itemView.findViewById(R.id.text_title_item_view);
            textPublisherItemView = itemView.findViewById(R.id.text_publisher_item_view);
            textRatingItemView = itemView.findViewById(R.id.text_rating_item_view);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Recipe recipe = mSearchItem.getRecipes().get(adapterPosition);
            mSearchMealOnClickHandler.onClick(recipe.getRecipeId());
        }
    }

    public SearchItem getSearchItem() {
        return mSearchItem;
    }
}
