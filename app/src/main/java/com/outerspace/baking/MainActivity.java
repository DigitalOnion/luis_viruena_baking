package com.outerspace.baking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.outerspace.baking.databinding.ActivityMainBinding;
import com.outerspace.baking.databinding.FragmentRecipeDetailNStepBinding;
import com.outerspace.baking.view.IMainView;
import com.outerspace.baking.view.RecipeDetailFragment;
import com.outerspace.baking.view.RecipeDetailNStepFragment;
import com.outerspace.baking.view.RecipeListFragment;
import com.outerspace.baking.view.RecipeStepsFragment;
import com.outerspace.baking.viewmodel.MainViewModel;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class MainActivity extends AppCompatActivity implements IMainView {
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
        //mainViewModel.getMutableDetailItem().observe(this, stepsFragment.getDetailObserver());

        if (isSmallScreen()) {
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
    public void onNetworkError(Integer networkError) {
        onProgress(false);
        Toast.makeText(this, R.string.app_name, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProgress(boolean show) {
        binding.progress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean isSmallScreen() {
        return binding.phoneScreenLayout != null;
    }

    @Override
    public MainViewModel getViewModel() {
        return mainViewModel;
    }

    @Override
    public void movePagerTo(int page) {
        binding.viewPager.setCurrentItem(arrayPages[page], true);
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
