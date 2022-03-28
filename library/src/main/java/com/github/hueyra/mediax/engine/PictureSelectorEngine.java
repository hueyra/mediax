package com.github.hueyra.mediax.engine;

import com.github.hueyra.mediax.entity.LocalMedia;
import com.github.hueyra.mediax.listener.OnResultCallbackListener;

public interface PictureSelectorEngine {

    /**
     * Create ImageLoad Engine
     *
     * @return
     */
    ImageEngine createEngine();

    /**
     * Create Result Listener
     *
     * @return
     */
    OnResultCallbackListener<LocalMedia> getResultCallbackListener();
}
