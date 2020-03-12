package com.outerspace.baking.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModel;
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

    public void setRecipeList(@Nullable List<Recipe> recipeList) {
        this.recipeList = recipeList == null ? new ArrayList<>() : recipeList;
        notifyDataSetChanged();
    }

    public void setViewModel(ViewModel viewModel) {
        this.mainViewModel = (MainViewModel) viewModel;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRecipeListBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_recipe_list, parent, false);
        RecipeViewHolder holder = new RecipeViewHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.binding.itemName.setText(recipeList.get(position).name);
        holder.recipe = recipeList.get(position);
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        Recipe recipe;
        ItemRecipeListBinding binding;

        public void setBinding(ItemRecipeListBinding binding) {
            this.binding = binding;
            binding.itemLayout.setOnClickListener(view -> mainViewModel
                    .getMutableRecipe()
                    .setValue(recipe));
        }

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
