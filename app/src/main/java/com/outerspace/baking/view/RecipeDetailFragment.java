package com.outerspace.baking.view;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import com.outerspace.baking.R;
import com.outerspace.baking.databinding.FragmentRecipeStepsBinding;
import com.outerspace.baking.helper.StepIngredients;
import com.outerspace.baking.helper.StepDescription;
import com.outerspace.baking.helper.OnSwipeGestureListener;
import com.outerspace.baking.viewmodel.MainViewModel;

public class RecipeDetailFragment extends Fragment implements OnSwipeGestureListener {
    private FragmentRecipeStepsBinding binding;
    private MainViewModel mainViewModel;
    private GestureDetectorCompat gestureDetector;

    public RecipeDetailFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_detail, container, false);

        mainViewModel.getMutableStep().observe(getActivity(), step -> {
            if(step == null) {
                clearUpStepsScreen();
            } else if (step instanceof StepDescription)
                presentDetailStep((StepDescription) step);
            else
                presentDetailIngredients((StepIngredients) step);
        });

        gestureDetector = new GestureDetectorCompat(getActivity(), this);

        binding.getRoot().setOnTouchListener((view, event) -> {
            view.performClick();
            return gestureDetector.onTouchEvent(event);
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.player.resume();

        View toastLayout = getLayoutInflater().inflate(R.layout.steps_toast,
                (ViewGroup) getActivity().findViewById(R.id.toast_layout));
        Toast toast = new Toast(getActivity().getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastLayout);
        toast.show();
    }

    @Override
    public void onPause() {
        binding.player.pause();
        super.onPause();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // rate = |velocityY/VelocityX|; it tells the direction of the swipe.
        // rate > 1 ==> greater than 45 degrees => swipe up or down
        // the test is in case velocityX equal or close to zero
        float rate = Math.abs(velocityX) < 0.01f ? 100f : Math.abs(velocityY / velocityX);
        if (rate > 1.0f) {
            if(Math.abs(velocityY) > (float) getResources().getInteger(R.integer.min_swipe_velocity)) {
                // above the speed limit to consider it a swipe
                if (velocityY < 0.0f) {
                    binding.player.stop();
                    mainViewModel.getMutableDetailOffset().setValue(+1);
                } else {
                    binding.player.stop();
                    mainViewModel.getMutableDetailOffset().setValue(-1);
                }
            }
            return true;
        }
        return false;
    }

    private void presentDetailStep(StepDescription stepDescription) {
        binding.stepName.setVisibility(View.VISIBLE);
        binding.fullDescription.setVisibility(View.VISIBLE);
        binding.ingredientTable.setVisibility(View.GONE);

        binding.stepName.setText(stepDescription.title);
        binding.fullDescription.setText(stepDescription.step.description);

        String videoUrl = stepDescription.step.videoURL;
        if (videoUrl.isEmpty())  {
            binding.player.setVisibility(View.GONE);
            binding.player.stop();
        } else {
            binding.player.setVisibility(View.VISIBLE);
            binding.player.start(videoUrl);
        }
    }

    private void presentDetailIngredients(StepIngredients stepIngredients) {
        binding.stepName.setVisibility(View.VISIBLE);
        binding.fullDescription.setVisibility(View.GONE);
        binding.ingredientTable.setVisibility(View.VISIBLE);

        binding.stepName.setText(stepIngredients.title);
        binding.ingredientTable.setBackgroundColor(Color.TRANSPARENT);
        binding.ingredientTable.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        binding.ingredientTable.loadData(stepIngredients.ingredients, "text/html", "utf-8");
        binding.player.setVisibility(View.GONE);
        binding.player.stop();
    }

    private void clearUpStepsScreen() {
        binding.stepName.setVisibility(View.GONE);
        binding.fullDescription.setVisibility(View.GONE);
        binding.ingredientTable.setVisibility(View.GONE);
        binding.player.setVisibility(View.GONE);
        binding.player.stop();
    }
}
