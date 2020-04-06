package com.outerspace.baking.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.outerspace.baking.R;
import com.outerspace.baking.api.Ingredient;
import com.outerspace.baking.api.Recipe;
import com.outerspace.baking.api.Step;
import com.outerspace.baking.databinding.ItemRecipeListBinding;
import com.outerspace.baking.helper.DetailIngredients;
import com.outerspace.baking.helper.DetailItem;
import com.outerspace.baking.helper.DetailStep;
import com.outerspace.baking.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

class RecipeDetailAdapter extends RecyclerView.Adapter<RecipeDetailAdapter.RecipeViewHolder> {
    private List<DetailItem> detailItemList = new ArrayList<>();
    private Recipe recipe;
    private MainViewModel mainViewModel;
    private int selectedPosition = -1;

    public RecipeDetailAdapter(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    public void setRecipe(Context context, Recipe recipe) {
        detailItemList.clear();
        DetailIngredients ingredients = new DetailIngredients();
        ingredients.title = context.getString(R.string.ingredients);
        ingredients.ingredients = ingredientsToString(recipe.ingredients);
        detailItemList.add(ingredients);

        for(Step step : recipe.steps) {
            DetailStep detailStep = new DetailStep();
            detailStep.title = step.shortDescription;
            detailStep.step = step;
            detailItemList.add(detailStep);
        }
        if(selectedPosition > -1 && selectedPosition < detailItemList.size())
            detailItemList.get(selectedPosition).selected = true;
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    private String ingredientsToString(List<Ingredient> ingredientList) {
        String content =  ingredientList.stream()
                .map(ingredient -> "<tr><td>" + ingredient.quantity +
                        "</td><td>" + ingredient.measure +
                        "</td><td>" + ingredient.ingredient +
                        "</td></tr>\n")
                .reduce("", String::concat);
        return "<table>" + content + "</table>";
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRecipeListBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_recipe_list, parent, false);
        return new RecipeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.binding.itemName.setText(detailItemList.get(position).title);
        holder.item = detailItemList.get(position);
        holder.binding.itemLayout.setBackgroundResource(
                detailItemList.get(position).selected ?
                        R.drawable.border_selected_recipe_list_card :
                        R.drawable.border_recipe_list_card);
        holder.binding.itemLayout.setOnClickListener(view -> onClickDetail(position));
    }

    @Override
    public int getItemCount() {
        return detailItemList.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        DetailItem item;
        ItemRecipeListBinding binding;

        public RecipeViewHolder(@NonNull ItemRecipeListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void moveDetailRelative(int offset) {
        onClickDetail(selectedPosition + offset);
    }

    private void onClickDetail(int position) {
        if(position < 0 || position >= detailItemList.size()) { return; }

        detailItemList.get(position).selected = true;
        notifyItemChanged(position);
        if(selectedPosition != position && selectedPosition >=0 && selectedPosition < detailItemList.size()) {
            detailItemList.get(selectedPosition).selected = false;
            notifyItemChanged(selectedPosition);
        }
        selectedPosition = position;

        mainViewModel.getMutableDetailItem()
                .setValue(detailItemList.get(position));
        mainViewModel.getMutableViewPagerPage()
                .setValue(IMainView.RECIPE_STEPS_PAGE);
    }
}
