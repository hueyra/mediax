package com.github.hueyra.mediax.listener;

import android.content.Context;

import java.util.List;

public interface OnCustomImagePreviewCallback<T> {
    /**
     * Custom Preview Callback
     *
     * @param context
     * @param previewData
     * @param currentPosition
     */
    void onCustomPreviewCallback(Context context, List<T> previewData, int currentPosition);
}
