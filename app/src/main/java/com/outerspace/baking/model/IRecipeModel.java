package com.outerspace.baking.model;

import androidx.core.util.Consumer;
import androidx.lifecycle.MutableLiveData;

import com.outerspace.baking.api.Recipe;

import java.util.List;

public interface IRecipeModel {
    void fetchRecipeList(MutableLiveData<List<Recipe>> mutableRecipeList, MutableLiveData<Integer> mutableErrorCode);
    void fetchRecipeList(Consumer<List<Recipe>> recipeListConsumer, Consumer<Integer> networkErrorConsumer);
}
