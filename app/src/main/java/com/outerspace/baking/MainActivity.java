package com.outerspace.baking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.outerspace.baking.databinding.ActivityMainBinding;
import com.outerspace.baking.helper.OnSwipeGestureListener;
import com.outerspace.baking.model.RecipeModel;
import com.outerspace.baking.view.IMainView;
import com.outerspace.baking.view.RecipeStepsFragment;
import com.outerspace.baking.view.RecipeStepXDetailFragment;
import com.outerspace.baking.view.RecipeListFragment;
import com.outerspace.baking.view.RecipeDetailFragment;
import com.outerspace.baking.viewmodel.MainViewModel;

import timber.log.Timber;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class MainActivity extends AppCompatActivity implements IMainView, OnSwipeGestureListener
{
    private ActivityMainBinding binding;
    private MainViewModel mainViewModel;
    private int[] arrayPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }

        RecipeListFragment listFragment = new RecipeListFragment();
        RecipeStepsFragment detailFragment = new RecipeStepsFragment();
        RecipeDetailFragment stepsFragment = new RecipeDetailFragment();

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.setSmallScreen(binding.phoneScreenLayout != null);

        if (mainViewModel.isSmallScreen()) {
            arrayPages = new int[] {0, 1, 2};
            Fragment[] fragmentArray = new Fragment[]{
                listFragment, detailFragment, stepsFragment
            };
            MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(),
                    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, fragmentArray);
            binding.viewPager.setAdapter(adapter);
            binding.viewPager.setOffscreenPageLimit(2);
        } else {
            RecipeStepXDetailFragment detailNStepFragment = new RecipeStepXDetailFragment();
            detailNStepFragment.addComposedFragment(detailFragment);
            detailNStepFragment.addComposedFragment(stepsFragment);
            arrayPages = new int[] {0, 1, 1};
            Fragment[] fragmentArray = new Fragment[]{ listFragment, detailNStepFragment,};
            MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(),
                    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, fragmentArray);
            binding.viewPager.setAdapter(adapter);
            binding.viewPager.setOffscreenPageLimit(1);
        }

        // this form (add the observer before setting the mutableViewPagerPage)
        // is needed to prevent a weird bug. The viewPager was moving without
        // calling setValue on rotation
        MutableLiveData<Integer> mutable = new MutableLiveData<>();
        mutable.observe(this, this::viewPagerToPage);
        mainViewModel.setMutableViewPagerPage(mutable);

        mainViewModel.getMutableOnProgress().setValue(true);
        RecipeModel.fetchRecipeList(
                mainViewModel.getMutableRecipeList(),
                mainViewModel.getMutableNetworkError());
    }

    private void viewPagerToPage(int page) {
        new Thread(() -> {
            binding.viewPager.postDelayed(
                    () -> binding.viewPager.setCurrentItem(arrayPages[page], true),
                    getResources().getInteger(R.integer.third_of_a_second));
        }).start();
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

    private static class MainPagerAdapter extends FragmentStatePagerAdapter {
        private Fragment[] fragments;

        MainPagerAdapter(@NonNull FragmentManager fm, int behavior, Fragment[] fragments) {
            super(fm, behavior);
            this.fragments = fragments;
        }

        @Override
        @NonNull
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(outState);
    }
}
