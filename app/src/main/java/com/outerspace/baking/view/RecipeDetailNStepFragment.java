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

public class RecipeDetailNStepFragment extends Fragment {
    private IMainView mainView;
    private FragmentRecipeDetailNStepBinding binding;
    private RecipeDetailFragment detailFragment;
    private RecipeStepsFragment stepsFragment;


    public RecipeDetailNStepFragment() { }

    public FragmentRecipeDetailNStepBinding getBinding() {
        return binding;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof IMainView) {
            mainView = (IMainView) context;
        } else {
            throw new ClassCastException("must implement IMainView");
        }
    }

    public void addComposedFragment(Fragment fragment) {
        if(fragment instanceof RecipeDetailFragment) detailFragment = (RecipeDetailFragment) fragment;
        if(fragment instanceof RecipeStepsFragment) stepsFragment = (RecipeStepsFragment) fragment;
    }

    private void appendFragment(FrameLayout layout, Fragment fragment) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(layout.getId(), fragment)
                .commit();
    }
}
