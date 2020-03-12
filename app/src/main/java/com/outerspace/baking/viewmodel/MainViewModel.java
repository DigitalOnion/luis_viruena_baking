package com.outerspace.baking.viewmodel;

import android.view.View;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.outerspace.baking.api.Recipe;
import com.outerspace.baking.helper.DetailItem;

public class MainViewModel extends ViewModel {
    MutableLiveData<Recipe> mutableRecipe = new MutableLiveData<>();
    MutableLiveData<DetailItem> mutableDetailItem = new MutableLiveData<>();

    public MutableLiveData<Recipe> getMutableRecipe() {
        return mutableRecipe;
    }

    public MutableLiveData<DetailItem> getMutableDetailItem() {
        return mutableDetailItem;
    }
}
