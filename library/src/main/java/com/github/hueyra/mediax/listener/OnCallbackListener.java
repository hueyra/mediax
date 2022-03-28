package com.github.hueyra.mediax.listener;

public interface OnCallbackListener<T> {
    /**
     * @param data
     */
    void onCall(T data);
}
