package com.github.hueyra.mediax.listener;

public interface OnVideoSelectedPlayCallback<T> {
    /**
     * Play the video
     *
     * @param data
     */
    void startPlayVideo(T data);
}
