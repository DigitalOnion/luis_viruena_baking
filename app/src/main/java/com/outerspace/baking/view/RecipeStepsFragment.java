package com.outerspace.baking.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.outerspace.baking.R;
import com.outerspace.baking.databinding.FragmentRecipeDetailBinding;
import com.outerspace.baking.viewmodel.MainViewModel;

public class RecipeStepsFragment extends Fragment {
    private MainViewModel mainViewModel;
    private FragmentRecipeDetailBinding binding;
    private RecipeStepAdapter adapter;

    public RecipeStepsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_steps, container, false);

        mainViewModel.getMutableRecipe().observe(getActivity(), recipe -> {
            adapter.setRecipe(getActivity().getApplicationContext(), recipe);
        });

        MutableLiveData<Integer> mutableOffset = new MutableLiveData<>();
        mutableOffset.observe(getActivity(), offset -> adapter.moveDetailRelative(offset));
        mainViewModel.setMutableDetailOffset(mutableOffset);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.detailRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecipeStepAdapter(mainViewModel);
        binding.detailRecycler.setAdapter(adapter);
    }
}
