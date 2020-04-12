package com.outerspace.baking.view;

import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModel;

import com.outerspace.baking.viewmodel.MainViewModel;

public interface IMainView {
    int RECIPE_LIST_PAGE = 0;
    int RECIPE_DETAIL_PAGE = 1;
    int RECIPE_STEPS_PAGE = 2;

    public void handleNetworkError(int httpResponseCode);
}
