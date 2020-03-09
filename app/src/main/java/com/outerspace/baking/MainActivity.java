package com.outerspace.baking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.outerspace.baking.databinding.ActivityMainBinding;
import com.outerspace.baking.view.IMainView;
import com.outerspace.baking.view.RecipeDetailFragment;
import com.outerspace.baking.view.RecipeListFragment;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class MainActivity extends AppCompatActivity implements IMainView {
    private ActivityMainBinding binding;
    private boolean singleScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        singleScreen = binding.viewPager != null;

        RecipeListFragment listFragment = new RecipeListFragment();
        RecipeDetailFragment detailFragment = new RecipeDetailFragment();

        if(singleScreen) {
            MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(),
                    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                    new Fragment[] {listFragment, detailFragment});
            binding.viewPager.setAdapter(adapter);
        } else {
            appendFragment(binding.listLayout, listFragment);
            appendFragment(binding.detailLayout, detailFragment);
        }
    }

    private void appendFragment(FrameLayout layout, Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(layout.getId(), fragment)
                .commit();
    }

    private class MainPagerAdapter extends FragmentStatePagerAdapter {
        private Fragment[] fragments;

        public MainPagerAdapter(@NonNull FragmentManager fm, int behavior, Fragment[] fragments) {
            super(fm, behavior);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) { return fragments[position]; }

        @Override
        public int getCount() { return fragments.length; }
    }
}
