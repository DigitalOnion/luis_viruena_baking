package com.outerspace.baking.view;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import com.outerspace.baking.R;
import com.outerspace.baking.api.Recipe;
import com.outerspace.baking.databinding.FragmentRecipeListBinding;
import com.outerspace.baking.databinding.ItemRecipeListBinding;
import com.outerspace.baking.model.RecipeModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class RecipeListFragment extends Fragment {
    private IMainView mainView;
    private FragmentRecipeListBinding binding;

    public RecipeListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.recipeRecycler.setLayoutManager(
                mainView.isSmallScreen() ? new LinearLayoutManager(getContext()) :
                        new GridLayoutManager(getContext(), getResources().getInteger(R.integer.column_count_on_tablet))
        );
        RecipeListAdapter adapter = new RecipeListAdapter();
        adapter.setViewModel(mainView.getViewModel());
        binding.recipeRecycler.setAdapter(adapter);

        mainView.onProgress(true);
        RecipeModel.fetchRecipeList(recipes -> {
                    adapter.setRecipeList(recipes);
                    mainView.onProgress(false);
                },
                mainView::onNetworkError);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IMainView) {
            mainView = (IMainView) context;
        } else {
            throw new ClassCastException("must implement IMainView");
        }
    }
}
