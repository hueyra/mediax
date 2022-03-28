package com.github.hueyra.mediax.listener;

import android.widget.ImageView;

import java.io.File;

public interface ImageCallbackListener {
    /**
     * 加载图片回调
     *
     * @param file
     * @param imageView
     */
    void onLoadImage(File file, ImageView imageView);
}
