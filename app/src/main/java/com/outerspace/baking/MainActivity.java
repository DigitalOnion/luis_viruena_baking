package com.outerspace.baking;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;

import com.outerspace.baking.api.Recipe;
import com.outerspace.baking.databinding.ActivityMainBinding;
import com.outerspace.baking.helper.OnSwipeGestureListener;
import com.outerspace.baking.helper.StepAbstract;
import com.outerspace.baking.model.IRecipeModel;
import com.outerspace.baking.model.ModelBehavior;
import com.outerspace.baking.model.RecipeModelFactory;
import com.outerspace.baking.view.IMainView;
import com.outerspace.baking.view.RecipeStepsFragment;
import com.outerspace.baking.view.RecipeStepXDetailFragment;
import com.outerspace.baking.view.RecipeListFragment;
import com.outerspace.baking.view.RecipeDetailFragment;
import com.outerspace.baking.viewmodel.MainViewModel;

import java.net.HttpURLConnection;
import java.util.List;

import timber.log.Timber;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class MainActivity extends AppCompatActivity implements IMainView, OnSwipeGestureListener
{
    public static final String EXTRA_BEHAVIOR = "behavior";

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

        mainViewModel.getMutableOnProgress().observe(this, showProgress ->
                binding.progress.setVisibility(showProgress ? View.VISIBLE : View.GONE));

        mainViewModel.getMutableNetworkError().observe(this, httpErrorCode -> {
            mainViewModel.getMutableOnProgress().setValue(false);
            @StringRes int title = httpErrorCode == HttpURLConnection.HTTP_NO_CONTENT ?
                    R.string.empty_response_title : R.string.network_error_title;
            @StringRes int message = httpErrorCode == HttpURLConnection.HTTP_NO_CONTENT ?
                    R.string.empty_response_message : R.string.network_error_message;
            new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setNegativeButton(R.string.exit_app, (dialog, which) -> super.onBackPressed())
                    .setPositiveButton(R.string.try_again, (dialog, which) -> fetchRecipeListFromModel())
                    .create().show();
        });

        List<Recipe> recipeList = mainViewModel.getMutableRecipeList().getValue();
        if( recipeList == null) {
            mainViewModel.getMutableOnProgress().setValue(true);
            fetchRecipeListFromModel();
        } else {
            mainViewModel.getMutableRecipeList().setValue(recipeList);
        }
    }

    private void fetchRecipeListFromModel() {
        ModelBehavior behavior = (ModelBehavior) getIntent().getSerializableExtra(EXTRA_BEHAVIOR);
        IRecipeModel model = new RecipeModelFactory.Builder().setBehavior(behavior).build();
        model.fetchRecipeList(
                mainViewModel.getMutableRecipeList(),
                mainViewModel.getMutableNetworkError());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Recipe recipe = mainViewModel.getMutableRecipe().getValue();
        if(recipe != null) {
            mainViewModel.getMutableRecipe().setValue(recipe);
            StepAbstract step = mainViewModel.getMutableStep().getValue();
            if(step != null) {
                mainViewModel.getMutableStep().setValue(step);
            }
        }
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
}
