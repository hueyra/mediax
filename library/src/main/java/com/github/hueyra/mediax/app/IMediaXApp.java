package com.github.hueyra.mediax.app;

import android.content.Context;

import com.github.hueyra.mediax.engine.PictureSelectorEngine;

public interface IMediaXApp {
    /**
     * Application
     *
     * @return
     */
    Context getAppContext();

    /**
     * PictureSelectorEngine
     *
     * @return
     */
    PictureSelectorEngine getPictureSelectorEngine();
}
