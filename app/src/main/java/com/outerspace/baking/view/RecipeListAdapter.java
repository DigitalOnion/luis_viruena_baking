package com.outerspace.baking.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.outerspace.baking.R;
import com.outerspace.baking.api.Recipe;
import com.outerspace.baking.databinding.ItemRecipeListBinding;
import com.outerspace.baking.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder> {
    private List<Recipe> recipeList = new ArrayList<>();
    private MainViewModel mainViewModel;
    private int selectedPosition = -1;

    public RecipeListAdapter(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    public void setRecipeList(@Nullable List<Recipe> recipeList) {
        //recipeList.forEach(recipe -> recipe.selected = false);
        this.recipeList = recipeList == null
                ? new ArrayList<>()
                : recipeList;
        notifyDataSetChanged();
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
        holder.binding.itemName.setText(recipeList.get(position).name);
        holder.recipe = recipeList.get(position);
        holder.binding.itemLayout.setBackgroundResource(
                holder.recipe.selected ?
                        R.drawable.border_selected_recipe_list_card :
                        R.drawable.border_recipe_list_card);
        holder.binding.itemLayout.setOnClickListener(
                view ->
                        onClickItem(holder.recipe, position));
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        Recipe recipe;
        ItemRecipeListBinding binding;

        RecipeViewHolder(@NonNull ItemRecipeListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private void onClickItem(Recipe recipe, int position) {
        mainViewModel.getMutableRecipe()
                .setValue(recipe);
        mainViewModel.getMutableRecipeSelection()
                .setValue(position);
        mainViewModel.getMutableStep()
                .setValue(null);
        mainViewModel.getMutableViewPagerPage()
                .setValue(IMainView.RECIPE_DETAIL_PAGE);

        mainViewModel.getMutableRecipeList().setValue(recipeList);
    }

    void selectPosition(int position) {
        if(position >= 0 && position < recipeList.size()) {
            recipeList.get(position).selected = true;
            //notifyItemChanged(position);
        }
        if(selectedPosition != position && selectedPosition >= 0 && selectedPosition < recipeList.size()) {
            recipeList.get(selectedPosition).selected = false;
            //notifyItemChanged(selectedPosition);
        }
        selectedPosition = position;
    }
}
