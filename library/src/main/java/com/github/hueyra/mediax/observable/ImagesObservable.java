package com.github.hueyra.mediax.observable;

import com.github.hueyra.mediax.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

public class ImagesObservable {
    private List<LocalMedia> mData = new ArrayList<>();
    private static ImagesObservable sObserver;

    public static ImagesObservable getInstance() {
        if (sObserver == null) {
            synchronized (ImagesObservable.class) {
                if (sObserver == null) {
                    sObserver = new ImagesObservable();
                }
            }
        }
        return sObserver;
    }

    /**
     * 存储图片用于预览时用
     *
     * @param data
     */
    public void savePreviewMediaData(List<LocalMedia> data) {
        this.mData = data;
    }

    /**
     * 读取预览的图片
     */
    public List<LocalMedia> readPreviewMediaData() {
        return mData;
    }

    /**
     * 清空预览的图片
     */
    public void clearPreviewMediaData() {
        mData.clear();
    }
}