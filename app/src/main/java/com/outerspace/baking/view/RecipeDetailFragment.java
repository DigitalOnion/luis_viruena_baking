package com.outerspace.baking.view;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
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
    private final static String SELECTED_POSITION = "selected_position";

    private MainViewModel mainViewModel;
    private FragmentRecipeDetailBinding binding;
    private RecipeDetailAdapter adapter;

    public RecipeDetailFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_detail, container, false);

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
        int selectedPosition = savedInstanceState == null ? -1 : savedInstanceState.getInt(SELECTED_POSITION, -1);
        adapter = new RecipeDetailAdapter(mainViewModel);
        adapter.setSelectedPosition(selectedPosition);
        binding.detailRecycler.setAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(SELECTED_POSITION, adapter.getSelectedPosition());
        super.onSaveInstanceState(outState);
    }
}
