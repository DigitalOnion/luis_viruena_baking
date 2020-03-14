package com.outerspace.baking.model;

import android.accounts.NetworkErrorException;

import androidx.core.util.Consumer;
import androidx.lifecycle.MutableLiveData;

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

    public static void fetchRecipeList(MutableLiveData<List<Recipe>> mutableRecipeList, MutableLiveData<Integer> mutableErrorCode) {
        fetchRecipeList(mutableRecipeList::setValue,  mutableErrorCode::setValue);
    }

    public static void fetchRecipeList(Consumer<List<Recipe>> recipeListConsumer, Consumer<Integer> networkErrorConsumer) {
        Gson gson = new GsonBuilder().setLenient().excludeFieldsWithoutExposeAnnotation().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IRecipeService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        IRecipeService.RecipeApi apiRecipeList = retrofit.create(IRecipeService.RecipeApi.class);

        Call<List<Recipe>> recipeListCall = apiRecipeList.callRecipeList();

        recipeListCall.enqueue(getCallback(recipeListConsumer, networkErrorConsumer));
    }

    private static Callback<List<Recipe>> getCallback(Consumer<List<Recipe>> recipeListConsumer, Consumer<Integer> networkErrorConsumer) {
        return new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    recipeListConsumer.accept(response.body());
                } else {
                    networkErrorConsumer.accept(response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                networkErrorConsumer.accept(HttpURLConnection.HTTP_BAD_REQUEST);
            }
        };
    }

}
