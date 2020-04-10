package com.outerspace.baking.model;

import androidx.core.util.Consumer;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.outerspace.baking.api.Recipe;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class RecipeModelFactory {
    private static IRecipeModel instance;

    public static IRecipeModel getInstance() {
        if(instance == null) {
            instance = new IRecipeModel() {
                @Override
                public void fetchRecipeList(MutableLiveData<List<Recipe>> mutableRecipeList, MutableLiveData<Integer> mutableErrorCode) {
                    fetchRecipeList(mutableRecipeList::setValue,  mutableErrorCode::setValue);
                }

                @Override
                public void fetchRecipeList(Consumer<List<Recipe>> recipeListConsumer, Consumer<Integer> networkErrorConsumer) {
                    InputStream inputStream = getClass().getResourceAsStream("mockJson.txt");
                    String result = new BufferedReader(new InputStreamReader(inputStream))
                            .lines().collect(Collectors.joining("\n"));
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<Recipe>>(){}.getType();
                    ArrayList<Recipe> recipes = (ArrayList<Recipe>) gson.fromJson(result, type);
                    recipeListConsumer.accept(recipes);
                }
            };
        }
        return  instance;
    }
}
