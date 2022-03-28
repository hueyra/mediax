package com.github.hueyra.mediax.listener;

import java.util.List;

public interface OnPhotoSelectChangedListener<T> {
    /**
     * Photo callback
     */
    void onTakePhoto();

    /**
     * Selected LocalMedia callback
     *
     * @param data
     */
    void onChange(List<T> data);

    /**
     * Image preview callback
     *
     * @param data
     * @param position
     */
    void onPictureClick(T data, int position);
}
