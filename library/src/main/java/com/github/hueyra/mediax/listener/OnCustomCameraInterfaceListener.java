package com.github.hueyra.mediax.listener;

import android.content.Context;

import com.github.hueyra.mediax.config.PictureSelectionConfig;

public interface OnCustomCameraInterfaceListener {
    /**
     * Camera Menu
     *
     * @param context
     * @param config
     * @param type
     */
    void onCameraClick(Context context, PictureSelectionConfig config, int type);
}
