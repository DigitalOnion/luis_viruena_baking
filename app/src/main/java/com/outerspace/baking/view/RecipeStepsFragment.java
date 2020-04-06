package com.outerspace.baking.view;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
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
import com.outerspace.baking.helper.DetailIngredients;
import com.outerspace.baking.helper.DetailItem;
import com.outerspace.baking.helper.DetailStep;
import com.outerspace.baking.helper.OnSwipeGestureListener;
import com.outerspace.baking.viewmodel.MainViewModel;

public class RecipeStepsFragment extends Fragment implements OnSwipeGestureListener {
    private FragmentRecipeStepsBinding binding;
    private MainViewModel mainViewModel;
    private GestureDetectorCompat gestureDetector;

    public RecipeStepsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_steps, container, false);

        mainViewModel.getMutableDetailItem().observe(getActivity(), detailItem -> {
            if(detailItem == null) {
                clearUpStepsScreen();
            } else if (detailItem instanceof DetailStep)
                presentDetailStep((DetailStep) detailItem);
            else
                presentDetailIngredients((DetailIngredients) detailItem);
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

    private void presentDetailStep(DetailStep detailStep) {
        binding.stepName.setVisibility(View.VISIBLE);
        binding.fullDescription.setVisibility(View.VISIBLE);
        binding.ingredientTable.setVisibility(View.GONE);

        binding.stepName.setText(detailStep.title);
        binding.fullDescription.setText(detailStep.step.description);

        String videoUrl = detailStep.step.videoURL;
        if (videoUrl.isEmpty())  {
            binding.player.setVisibility(View.GONE);
            binding.player.stop();
        } else {
            binding.player.setVisibility(View.VISIBLE);
            binding.player.start(videoUrl);
        }
    }

    private void presentDetailIngredients(DetailIngredients detailIngredients) {
        binding.stepName.setVisibility(View.VISIBLE);
        binding.fullDescription.setVisibility(View.GONE);
        binding.ingredientTable.setVisibility(View.VISIBLE);

        binding.stepName.setText(detailIngredients.title);
        binding.ingredientTable.setBackgroundColor(Color.TRANSPARENT);
        binding.ingredientTable.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        binding.ingredientTable.loadData(detailIngredients.ingredients, "text/html", "utf-8");
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
