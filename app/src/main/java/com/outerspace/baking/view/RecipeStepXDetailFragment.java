package com.outerspace.baking.view;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.outerspace.baking.R;
import com.outerspace.baking.databinding.FragmentRecipeDetailNStepBinding;

import org.jetbrains.annotations.NotNull;

public class RecipeStepXDetailFragment extends Fragment {
    private FragmentRecipeDetailNStepBinding binding;
    private RecipeStepsFragment detailFragment;
    private RecipeDetailFragment stepsFragment;


    public RecipeStepXDetailFragment() { }

    public FragmentRecipeDetailNStepBinding getBinding() {
        return binding;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_detail_n_step, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appendFragment(binding.detailLayout, detailFragment);
        appendFragment(binding.stepsLayout, stepsFragment);
    }

    public void addComposedFragment(Fragment fragment) {
        if(fragment instanceof RecipeStepsFragment) detailFragment = (RecipeStepsFragment) fragment;
        if(fragment instanceof RecipeDetailFragment) stepsFragment = (RecipeDetailFragment) fragment;
    }

    private void appendFragment(FrameLayout layout, Fragment fragment) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(layout.getId(), fragment)
                .commit();
    }
}
