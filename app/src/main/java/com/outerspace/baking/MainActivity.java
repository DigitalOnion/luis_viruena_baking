package com.outerspace.baking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.outerspace.baking.databinding.ActivityMainBinding;
import com.outerspace.baking.databinding.FragmentRecipeDetailNStepBinding;
import com.outerspace.baking.helper.OnSwipeGestureListener;
import com.outerspace.baking.view.IMainView;
import com.outerspace.baking.view.RecipeDetailFragment;
import com.outerspace.baking.view.RecipeDetailNStepFragment;
import com.outerspace.baking.view.RecipeListFragment;
import com.outerspace.baking.view.RecipeStepsFragment;
import com.outerspace.baking.viewmodel.MainViewModel;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class MainActivity extends AppCompatActivity implements IMainView
{
    private ActivityMainBinding binding;
    private MainViewModel mainViewModel;
    private int[] arrayPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        RecipeListFragment listFragment = new RecipeListFragment();
        RecipeDetailFragment detailFragment = new RecipeDetailFragment();
        RecipeStepsFragment stepsFragment = new RecipeStepsFragment();

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getMutableRecipe().observe(this, detailFragment.getRecipeObserver());
        mainViewModel.getMutableViewPagerPage().observe(this,
                page -> binding.viewPager.setCurrentItem(arrayPages[page], true));
        mainViewModel.getMutableDetailItem().observe(this, stepsFragment.getDetailObserver());

        mainViewModel.setSmallScreen(binding.phoneScreenLayout != null);

        if (mainViewModel.isSmallScreen()) {
            arrayPages = new int[] {0, 1, 2};
            Fragment[] fragmentArray = new Fragment[]{
                listFragment, detailFragment, stepsFragment
            };
            MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(),
                    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, fragmentArray);
            binding.viewPager.setAdapter(adapter);
        } else {
            RecipeDetailNStepFragment detailNStepFragment = new RecipeDetailNStepFragment();
            detailNStepFragment.addComposedFragment(detailFragment);
            detailNStepFragment.addComposedFragment(stepsFragment);
            arrayPages = new int[] {0, 1, 1};
            Fragment[] fragmentArray = new Fragment[]{ listFragment, detailNStepFragment,};
            MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(),
                    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, fragmentArray);
            binding.viewPager.setAdapter(adapter);
        }
    }

    @Override
    public void onBackPressed() {
        int page = binding.viewPager.getCurrentItem();
        if(page > 0) {
            mainViewModel.getMutableViewPagerPage().setValue(page - 1);
        } else {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.wanna_exit)
                    .setPositiveButton(R.string.exit_app, (dialog, which) -> super.onBackPressed())
                    .setNegativeButton(R.string.back_to_app, null)
                    .create().show();
        }
    }

    private class MainPagerAdapter extends FragmentStatePagerAdapter {
        private Fragment[] fragments;

        public MainPagerAdapter(@NonNull FragmentManager fm, int behavior, Fragment[] fragments) {
            super(fm, behavior);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
    }
}
