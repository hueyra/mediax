package com.github.hueyra.mediax.listener;

import androidx.annotation.NonNull;

import java.io.File;

public interface CameraListener {
    /**
     * 拍照成功返回
     *
     * @param file
     */
    void onPictureSuccess(@NonNull File file);

    /**
     * 录像成功返回
     *
     * @param file
     */
    void onRecordSuccess(@NonNull File file);

    /**
     * 使用相机出错
     *
     * @param file
     */
    void onError(int videoCaptureError, String message, Throwable cause);
}
