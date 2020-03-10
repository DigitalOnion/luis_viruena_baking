package com.outerspace.baking.model;

import androidx.core.util.Consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.outerspace.baking.api.Recipe;

import java.net.HttpURLConnection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipeModel {

    public static void fetchRecipeList(Consumer<List<Recipe>> recipeListConsumer) {
        //GRACE
        Gson gson = new GsonBuilder().setLenient().excludeFieldsWithoutExposeAnnotation().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IRecipeService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        IRecipeService.RecipeApi apiRecipeList = retrofit.create(IRecipeService.RecipeApi.class);

        Call<List<Recipe>> recipeListCall = apiRecipeList.callRecipeList();

        recipeListCall.enqueue(getCallback(recipeListConsumer));
    }

    private static Callback<List<Recipe>> getCallback(Consumer<List<Recipe>> recipeListConsumer) {
        return new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if(response.code() != HttpURLConnection.HTTP_OK) {
                    recipeListConsumer.accept(null);
                } else {
                    recipeListConsumer.accept(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                recipeListConsumer.accept(null);
            }
        };
    }

}