package com.outerspace.baking.view;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
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
import com.outerspace.baking.viewmodel.MainViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class RecipeListFragment extends Fragment {
    private MainViewModel mainViewModel;
    private FragmentRecipeListBinding binding;
    private RecipeListAdapter adapter;

    public RecipeListFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.recipeRecycler.setLayoutManager(
                mainViewModel.isSmallScreen() ?
                        new LinearLayoutManager(getContext()) :
                        new GridLayoutManager(getContext(), getResources().getInteger(R.integer.column_count_on_tablet))
        );

        adapter = new RecipeListAdapter(mainViewModel);
        binding.recipeRecycler.setAdapter(adapter);
        mainViewModel.getMutableRecipeList().observe(getActivity(),
                adapter::setRecipeList);
        mainViewModel.getMutableRecipeSelection().observe(getActivity(),
                adapter::selectPosition);
    }
}
