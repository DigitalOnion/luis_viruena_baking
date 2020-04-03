package com.outerspace.baking.view;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.outerspace.baking.R;
import com.outerspace.baking.api.Recipe;
import com.outerspace.baking.databinding.FragmentRecipeDetailBinding;
import com.outerspace.baking.viewmodel.MainViewModel;

public class RecipeDetailFragment extends Fragment {
    private MainViewModel mainViewModel;
    private FragmentRecipeDetailBinding binding;
    private RecipeDetailAdapter adapter;

    public RecipeDetailFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_detail, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.detailRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecipeDetailAdapter(mainViewModel);
        binding.detailRecycler.setAdapter(adapter);
    }

    public void moveDetailRelative(int offset) {
        adapter.moveDetailRelative(offset);
    }

    public Observer<Recipe> getRecipeObserver() {
        return recipe -> {
            mainViewModel.getMutableViewPagerPage().setValue(IMainView.RECIPE_DETAIL_PAGE);
            adapter.setRecipe(getContext(), recipe);
        };
    }
}
