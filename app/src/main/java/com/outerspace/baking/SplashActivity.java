package com.outerspace.baking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;

import com.outerspace.baking.databinding.ActivitySplashBinding;
import com.outerspace.baking.model.ModelBehavior;

public class SplashActivity extends AppCompatActivity {
    ActivitySplashBinding binding;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(this::launchMain, getResources().getInteger(R.integer.half_a_second));
    }

    private void launchMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_BEHAVIOR, ModelBehavior.NETWORK_REQUEST);
        startActivity(intent);
    }
}
