package com.outerspace.baking.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.outerspace.baking.api.Recipe;
import com.outerspace.baking.helper.StepAbstract;

import java.util.List;

public class MainViewModel extends ViewModel {
    // mutableRecipeList and MutableNetworkError do not need to have a setter. They work on the network call.
    private MutableLiveData<List<Recipe>> mutableRecipeList = new MutableLiveData<>();
    public MutableLiveData<List<Recipe>> getMutableRecipeList() { return mutableRecipeList; }

    private MutableLiveData<Integer> mutableNetworkError = new MutableLiveData<>();
    public MutableLiveData<Integer> getMutableNetworkError() { return mutableNetworkError; }

    private MutableLiveData<Boolean> mutableOnProgress = new MutableLiveData<>();
    public MutableLiveData<Boolean> getMutableOnProgress() { return mutableOnProgress; }

    private MutableLiveData<Recipe> mutableRecipe = new MutableLiveData<>();
    public MutableLiveData<Recipe> getMutableRecipe() { return mutableRecipe; }

    private MutableLiveData<StepAbstract> mutableStep = new MutableLiveData<>();
    public MutableLiveData<StepAbstract> getMutableStep() { return mutableStep; }

    // other mutable data are presenting a bug if the mutable is assigned before the observer is subscribed
    private MutableLiveData<Integer> mutableDetailOffset = new MutableLiveData<>();
    public MutableLiveData<Integer> getMutableDetailOffset() { return mutableDetailOffset; }
    public void setMutableDetailOffset(MutableLiveData<Integer> mutableDetailOffset) { this.mutableDetailOffset = mutableDetailOffset; }

    private MutableLiveData<Integer> mutableViewPagerPage = null; // LateInit: new MutableLiveData<>();
    public MutableLiveData<Integer> getMutableViewPagerPage() { return mutableViewPagerPage; }
    public void setMutableViewPagerPage(MutableLiveData<Integer> mutableViewPagerPage) { this.mutableViewPagerPage = mutableViewPagerPage; }

    private boolean isSmallScreen;

    public boolean isSmallScreen() { return isSmallScreen; }

    public void setSmallScreen(boolean smallScreen) { isSmallScreen = smallScreen; }

}
