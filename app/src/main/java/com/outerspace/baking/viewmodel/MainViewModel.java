package com.outerspace.baking.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.outerspace.baking.api.Recipe;
import com.outerspace.baking.helper.DetailItem;
import com.outerspace.baking.view.IMainView;

import java.util.List;

public class MainViewModel extends ViewModel {
    private MutableLiveData<Recipe> mutableRecipe = new MutableLiveData<>();
    public MutableLiveData<Recipe> getMutableRecipe() { return mutableRecipe; }

    private MutableLiveData<List<Recipe>> mutableRecipeList = new MutableLiveData<>();
    public MutableLiveData<List<Recipe>> getMutableRecipeList() { return mutableRecipeList; }

    private MutableLiveData<Integer> mutableDetailOffset = new MutableLiveData<>();
    public MutableLiveData<Integer> getMutableDetailOffset() { return mutableDetailOffset; }

    private MutableLiveData<DetailItem> mutableDetailItem = new MutableLiveData<>();
    public MutableLiveData<DetailItem> getMutableDetailItem() { return mutableDetailItem; }

    private MutableLiveData<Boolean> mutableOnProgress = new MutableLiveData<>();
    public MutableLiveData<Boolean> getMutableOnProgress() { return mutableOnProgress; }

    private MutableLiveData<Integer> mutableViewPagerPage = null; // LateInit: new MutableLiveData<>();
    public MutableLiveData<Integer> getMutableViewPagerPage() { return mutableViewPagerPage; }
    public void setMutableViewPagerPage(MutableLiveData<Integer> mutableViewPagerPage) { this.mutableViewPagerPage = mutableViewPagerPage; }

    private MutableLiveData<Integer> mutableNetworkError = new MutableLiveData<>();
    public MutableLiveData<Integer> getMutableNetworkError() { return mutableNetworkError; }

    private boolean isSmallScreen;

    public boolean isSmallScreen() { return isSmallScreen; }

    public void setSmallScreen(boolean smallScreen) { isSmallScreen = smallScreen; }
}
