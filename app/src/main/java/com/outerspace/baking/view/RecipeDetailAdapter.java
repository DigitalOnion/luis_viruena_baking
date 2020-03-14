package com.outerspace.baking.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModel;
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
    private List<DetailItem> items = new ArrayList<>();
    private Recipe recipe;
    private MainViewModel mainViewModel;

    public RecipeDetailAdapter(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    public void setRecipe(Context context, Recipe recipe) {
        items.clear();
        DetailIngredients ingredients = new DetailIngredients();
        ingredients.title = context.getString(R.string.ingredients);
        ingredients.ingredients = ingredientsToString(recipe.ingredients);
        items.add(ingredients);

        for(Step step : recipe.steps) {
            DetailStep detailStep = new DetailStep();
            detailStep.title = step.shortDescription;
            detailStep.step = step;
            items.add(detailStep);
        }
        notifyDataSetChanged();
    }

    private String ingredientsToString(List<Ingredient> ingredientList) {
        return ingredientList.stream()
                .map(ingredient -> ingredient.quantity + " " + ingredient.measure + " " + ingredient.ingredient)
                .reduce("", String::concat);
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
        holder.binding.itemName.setText(items.get(position).title);
        holder.item = items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        DetailItem item;
        ItemRecipeListBinding binding;

        public void setBinding(ItemRecipeListBinding binding) {
            this.binding = binding;
            binding.itemLayout.setOnClickListener(view -> mainViewModel
                    .getMutableDetailItem()
                    .setValue(item));
        }

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
