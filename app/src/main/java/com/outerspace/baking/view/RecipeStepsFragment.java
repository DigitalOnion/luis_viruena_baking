package com.outerspace.baking.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.outerspace.baking.R;
import com.outerspace.baking.databinding.FragmentRecipeStepsBinding;
import com.outerspace.baking.helper.DetailIngredients;
import com.outerspace.baking.helper.DetailItem;
import com.outerspace.baking.helper.DetailStep;
import com.outerspace.baking.helper.OnSwipeGestureListener;
import com.outerspace.baking.viewmodel.MainViewModel;
import com.outerspace.baking.viewmodel.RecipeStepsViewModel;

public class RecipeStepsFragment extends Fragment implements OnSwipeGestureListener {
    private FragmentRecipeStepsBinding binding;
    private MainViewModel mainViewModel;
    private RecipeStepsViewModel stepsViewModel;
    private GestureDetectorCompat gestureDetector;

    public RecipeStepsFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        stepsViewModel = new ViewModelProvider(this).get(RecipeStepsViewModel.class);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_steps, container, false);
        binding.setViewModel(stepsViewModel);

        stepsViewModel.getMutableDetailStep().observe(this, detailStep -> {
            binding.stepName.setText(detailStep.title);
            binding.fullDescription.setText(detailStep.step.description);
        });
        stepsViewModel.getMutableDetailInstructions().observe(this, detailIngredients -> {
            binding.stepName.setText(detailIngredients.title);
            binding.fullDescription.setText(detailIngredients.ingredients);
        });

        gestureDetector = new GestureDetectorCompat(getActivity(), this);

        binding.getRoot().setOnTouchListener((view, event) -> {
            view.performClick();
            return gestureDetector.onTouchEvent(event);
        });

        return binding.getRoot();
    }

    public Observer<DetailItem> getDetailObserver() {
        return detailItem -> {
            mainViewModel.getMutableViewPagerPage().setValue(IMainView.RECIPE_STEPS_PAGE);
            if(detailItem instanceof DetailStep)
                stepsViewModel.getMutableDetailStep().setValue((DetailStep) detailItem);
            else
                stepsViewModel.getMutableDetailInstructions().setValue((DetailIngredients) detailItem);
        };
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float rate = Math.abs(velocityX) < 0.01f ? 100f : Math.abs(velocityY/ velocityX);
        if(rate > 1.0f) {
            if(velocityY < 0.0f) {
                mainViewModel.getMutableDetailOffset().setValue(+1);
            } else {
                mainViewModel.getMutableDetailOffset().setValue(-1);
            }
            return true;
        }
        return false;
    }
}
