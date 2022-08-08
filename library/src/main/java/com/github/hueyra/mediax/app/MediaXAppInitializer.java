package com.github.hueyra.mediax.app;

import android.content.Context;

import com.github.hueyra.mediax.engine.PictureSelectorEngine;

public class MediaXAppInitializer implements IMediaXApp {

    private IMediaXApp mediaXApp;

    @Override
    public Context getAppContext() {
        if (mediaXApp == null) {
            return null;
        }
        return mediaXApp.getAppContext();
    }

    @Override
    public PictureSelectorEngine getPictureSelectorEngine() {
        if (mediaXApp == null) {
            return null;
        }
        return mediaXApp.getPictureSelectorEngine();
    }

    private MediaXAppInitializer() {
    }

    private static MediaXAppInitializer mInstance;

    public static MediaXAppInitializer getInstance() {
        if (mInstance == null) {
            synchronized (MediaXAppInitializer.class) {
                if (mInstance == null) {
                    mInstance = new MediaXAppInitializer();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化IMediaXApp
     */
    public void init(IMediaXApp app) {
        this.mediaXApp = app;
    }

    public IMediaXApp getApp() {
        return mediaXApp;
    }
}
