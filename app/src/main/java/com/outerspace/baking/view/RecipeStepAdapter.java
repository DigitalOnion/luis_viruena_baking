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
import com.outerspace.baking.helper.StepIngredients;
import com.outerspace.baking.helper.StepAbstract;
import com.outerspace.baking.helper.StepDescription;
import com.outerspace.baking.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.RecipeViewHolder> {
    private List<StepAbstract> stepAbstractList = new ArrayList<>();
    private Recipe recipe;
    private MainViewModel mainViewModel;
    private int selectedPosition = -1;

    public RecipeStepAdapter(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    public void setRecipe(Context context, Recipe recipe) {
        stepAbstractList.clear();
        StepIngredients ingredients = new StepIngredients();
        ingredients.title = context.getString(R.string.ingredients);
        ingredients.ingredients = ingredientsToString(recipe.ingredients);
        stepAbstractList.add(ingredients);

        for(Step step : recipe.steps) {
            StepDescription stepDescription = new StepDescription();
            stepDescription.title = step.shortDescription;
            stepDescription.step = step;
            stepAbstractList.add(stepDescription);
        }
        if(selectedPosition > -1 && selectedPosition < stepAbstractList.size()) {
            stepAbstractList.get(selectedPosition).selected = true;
        }
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
        holder.binding.itemName.setText(stepAbstractList.get(position).title);
        holder.item = stepAbstractList.get(position);
        holder.binding.itemLayout.setBackgroundResource(
                stepAbstractList.get(position).selected ?
                        R.drawable.border_selected_recipe_list_card :
                        R.drawable.border_recipe_list_card);
        holder.binding.itemLayout.setOnClickListener(view -> onClickDetail(position));
    }

    @Override
    public int getItemCount() {
        return stepAbstractList.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        StepAbstract item;
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
        if(position < 0 || position >= stepAbstractList.size()) { return; }

        stepAbstractList.get(position).selected = true;
        notifyItemChanged(position);
        if(selectedPosition != position && selectedPosition >=0 && selectedPosition < stepAbstractList.size()) {
            stepAbstractList.get(selectedPosition).selected = false;
            notifyItemChanged(selectedPosition);
        }
        selectedPosition = position;

        mainViewModel.getMutableStep()
                .setValue(stepAbstractList.get(position));
        mainViewModel.getMutableViewPagerPage()
                .setValue(IMainView.RECIPE_STEPS_PAGE);
    }
}
