package com.github.hueyra.mediax.widget;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.Nullable;

import com.github.hueyra.mediax.R;

public class MediaPrevView extends FrameLayout {

    private VideoView mVvVideoVertical;
    private VideoView mVvVideoHorizontal;
    private ImageView mIvImage;

    public MediaPrevView(Context context) {
        super(context);
    }

    public MediaPrevView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_media_prev, this);
        mVvVideoVertical = findViewById(R.id.vv_video_vertical);
        mVvVideoHorizontal = findViewById(R.id.vv_video_horizontal);
        mIvImage = findViewById(R.id.iv_image);
    }

    public void startPrevImage(Uri uri) {
        setVisibility(VISIBLE);
        if (uri != null) {
            mIvImage.setImageURI(uri);
            mIvImage.setVisibility(VISIBLE);
        }
    }

    public void startPrevVideo(Uri uri, boolean isHorizontal) {
        setVisibility(VISIBLE);
        if (uri != null) {
            if (isHorizontal) {
                play(mVvVideoHorizontal, uri);
            } else {
                play(mVvVideoVertical, uri);
            }
        }
    }

    private void play(VideoView videoView, Uri uri) {
        videoView.setVisibility(VISIBLE);
        videoView.setOnCompletionListener(mp -> {
            mp.seekTo(0);
            mp.start();
        });
        videoView.setVideoURI(uri);
        videoView.start();
    }


    public void stopPrev() {
        setVisibility(INVISIBLE);
        if (mIvImage.getVisibility() == VISIBLE) {
            mIvImage.setVisibility(INVISIBLE);
        }
        if (mVvVideoVertical.getVisibility() == VISIBLE) {
            mVvVideoVertical.stopPlayback();
            mVvVideoVertical.setVisibility(INVISIBLE);
        }
        if (mVvVideoHorizontal.getVisibility() == VISIBLE) {
            mVvVideoHorizontal.stopPlayback();
            mVvVideoHorizontal.setVisibility(INVISIBLE);
        }
    }

    public void onResume() {
        if (mVvVideoVertical.getVisibility() == VISIBLE) {
            mVvVideoVertical.resume();
        }
        if (mVvVideoHorizontal.getVisibility() == VISIBLE) {
            mVvVideoHorizontal.resume();
        }
    }

    public void onPause() {
        if (mVvVideoVertical.getVisibility() == VISIBLE) {
            mVvVideoVertical.pause();
        }
        if (mVvVideoHorizontal.getVisibility() == VISIBLE) {
            mVvVideoHorizontal.pause();
        }
    }

}
