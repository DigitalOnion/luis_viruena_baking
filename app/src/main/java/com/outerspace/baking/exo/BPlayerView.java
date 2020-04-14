package com.outerspace.baking.exo;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.outerspace.baking.R;

import timber.log.Timber;

public class BPlayerView extends PlayerView implements ExoPlayer.EventListener {

    private static ExoPlayer exoPlayerInstance;

    private Context context;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private ProgressBar progress;

    public BPlayerView(Context context) { super(context); init(context); }

    public BPlayerView(Context context, AttributeSet attrs) { super(context, attrs);  init(context);}

    public BPlayerView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr);  init(context);}

    private void init(Context context) {
        this.context = context.getApplicationContext(); // application context does not pose a memory leakl

        progress = new ProgressBar(this.getContext());
        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.getOverlayFrameLayout().getLayoutParams();
        float progressBarSize = context.getResources().getDimension(R.dimen.progress_bar_size);
        params.height = (int) (progressBarSize);
        params.width = (int) (progressBarSize);
        params.gravity = Gravity.CENTER;
        progress.setLayoutParams(params);
        progress.setForegroundGravity(Gravity.CENTER);
        addView(progress);
    }

    public void start(String videoUrl) {
        progress.setVisibility(VISIBLE);
        this.setVisibility(VISIBLE);
        if (exoPlayerInstance == null) {
            exoPlayerInstance = ExoPlayerFactory.newSimpleInstance(context,
                    new DefaultRenderersFactory(context),
                    new DefaultTrackSelector(),
                    new DefaultLoadControl());
        }
        this.setPlayer(exoPlayerInstance);
        exoPlayerInstance.addListener(this);
        exoPlayerInstance.setPlayWhenReady(playWhenReady);
        exoPlayerInstance.seekTo(currentWindow, playbackPosition);

        // build the MediaSource
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(context, getResources().getString(R.string.app_name)); // use app_name as the agent
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(videoUrl));
        exoPlayerInstance.prepare(mediaSource, true, true);
    }

    public void resume() {
        progress.setVisibility(VISIBLE);
        if (exoPlayerInstance != null) {
            exoPlayerInstance.setPlayWhenReady(true);
            exoPlayerInstance.getPlaybackState();
        }
    }

    public void pause() {
        progress.setVisibility(GONE);
        if (exoPlayerInstance != null) {
            exoPlayerInstance.setPlayWhenReady(false);
            exoPlayerInstance.getPlaybackState();
        }
    }

    public void stop() {
        progress.setVisibility(INVISIBLE);
        this.setVisibility(GONE);
        if (exoPlayerInstance != null) {
            playbackPosition = exoPlayerInstance.getCurrentPosition();
            currentWindow = exoPlayerInstance.getCurrentWindowIndex();
            playWhenReady = exoPlayerInstance.getPlayWhenReady();
            exoPlayerInstance.release();
            exoPlayerInstance = null;
        }
    }

    // ExoPlayer EventListener

    private boolean isReady = false;

    @Override
    public void onLoadingChanged(boolean isLoading) {
        Timber.d("Luis ExoPlayer onLoadingChanged:%s", isLoading ? "loading" : "not loading");
        progress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onIsPlayingChanged(boolean isPlaying) {
        if (isReady) {
            Timber.d("Luis ExoPlayer onIsPlayingChanged:%s", isPlaying ? "playing" : "not playing");
            progress.setVisibility(isPlaying ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case ExoPlayer.STATE_READY:
                Timber.d("Luis ExoPlayer onPlayerStateChanged:%s", "STATE_READY");
                progress.setVisibility(View.VISIBLE);
                isReady = true;
                break;
            case ExoPlayer.STATE_ENDED:
                Timber.d("Luis ExoPlayer onPlayerStateChanged:%s", "STATE_ENDED");
                progress.setVisibility(View.GONE);
                isReady = false;
                break;
        }
    }
}
