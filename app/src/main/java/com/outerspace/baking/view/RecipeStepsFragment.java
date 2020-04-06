package com.outerspace.baking.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Spanned;
import android.text.SpannedString;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.outerspace.baking.R;
import com.outerspace.baking.databinding.FragmentRecipeStepsBinding;
import com.outerspace.baking.helper.DetailIngredients;
import com.outerspace.baking.helper.DetailItem;
import com.outerspace.baking.helper.DetailStep;
import com.outerspace.baking.helper.OnSwipeGestureListener;
import com.outerspace.baking.viewmodel.MainViewModel;
import com.outerspace.baking.viewmodel.RecipeStepsViewModel;

import timber.log.Timber;


public class RecipeStepsFragment extends Fragment implements OnSwipeGestureListener {
    private FragmentRecipeStepsBinding binding;
    private MainViewModel mainViewModel;
    private RecipeStepsViewModel stepsViewModel;
    private GestureDetectorCompat gestureDetector;
    private static SimpleExoPlayer simpleExo;

    public RecipeStepsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        stepsViewModel = new ViewModelProvider(this).get(RecipeStepsViewModel.class);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_steps, container, false);
        binding.setViewModel(stepsViewModel);

        mainViewModel.getMutableDetailItem().observe(getActivity(), detailItem -> {
            if (detailItem instanceof DetailStep)
                stepsViewModel.getMutableDetailStep().setValue((DetailStep) detailItem);
            else
                stepsViewModel.getMutableDetailInstructions().setValue((DetailIngredients) detailItem);
        });

        stepsViewModel.getMutableDetailStep().observe(this, detailStep -> {
            binding.stepName.setText(detailStep.title);
            binding.fullDescription.setText(detailStep.step.description);
            binding.ingredientTable.setVisibility(View.GONE);
            binding.fullDescription.setVisibility(View.VISIBLE);
            String videoUrl = detailStep.step.videoURL;
            if (!videoUrl.isEmpty()) {
                binding.player.start(videoUrl);
            } else {
                binding.player.stop();
            }
        });

        stepsViewModel.getMutableDetailInstructions().observe(this, detailIngredients -> {
            binding.fullDescription.setVisibility(View.GONE);
            binding.player.stop();
            binding.ingredientTable.setVisibility(View.VISIBLE);
            binding.ingredientTable.setBackgroundColor(Color.TRANSPARENT);
            binding.ingredientTable.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);

            binding.stepName.setText(detailIngredients.title);
            binding.ingredientTable.loadData(detailIngredients.ingredients, "text/html", "utf-8");
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
        float rate = Math.abs(velocityX) < 0.01f ? 100f : Math.abs(velocityY / velocityX);
        if (rate > 1.0f) {
            if (velocityY < 0.0f) {
                binding.player.stop();
                mainViewModel.getMutableDetailOffset().setValue(+1);
            } else {
                binding.player.stop();
                mainViewModel.getMutableDetailOffset().setValue(-1);
            }
            return true;
        }
        return false;
    }
}
