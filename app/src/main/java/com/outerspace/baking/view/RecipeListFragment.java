package com.outerspace.baking.view;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.outerspace.baking.R;
import com.outerspace.baking.databinding.FragmentRecipeListBinding;
import com.outerspace.baking.viewmodel.MainViewModel;

public class RecipeListFragment extends Fragment {
    private IMainView mainView;
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
        mainViewModel.getMutableRecipeList().observe(getActivity(), recipeList -> {
                    mainViewModel.getMutableOnProgress().setValue(false);
                    adapter.setRecipeList(recipeList);
                });
        mainViewModel.getMutableRecipeSelection().observe(getActivity(),
                adapter::selectPosition);

        mainViewModel.getMutableNetworkError().observe(getActivity(), httpErrorCode -> {
            mainViewModel.getMutableOnProgress().setValue(false);
            mainView.handleNetworkError(httpErrorCode);
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainView = (IMainView) context;
    }

}
