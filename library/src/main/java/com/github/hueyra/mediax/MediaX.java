package com.github.hueyra.mediax;

import static android.app.Activity.RESULT_OK;

import static com.github.hueyra.mediax.app.Config.REQUEST_OPEN_ALBUM;
import static com.github.hueyra.mediax.app.Config.REQUEST_OPEN_CAMERA;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.github.hueyra.mediax.app.Config;
import com.github.hueyra.mediax.entity.LocalMedia;
import com.github.hueyra.mediax.model.PictureSelectionModel;
import com.github.hueyra.mediax.ui.WxCameraActivity;

import java.util.ArrayList;
import java.util.List;

public class MediaX {
    private int mType;
    private boolean mCrop;
    private int mMaxSelect;

    protected MediaX() {
        //
    }

    protected MediaX(Builder builder) {
        mType = builder.type;
        mCrop = builder.crop;
        mMaxSelect = builder.maxSelect;
    }

    public static final class Builder {
        private int type;
        private boolean crop;
        private int maxSelect;

        public Builder() {
            type = Config.TYPE_ALL;
            crop = false;
        }

        public Builder both() {
            type = Config.TYPE_ALL;
            return this;
        }

        public Builder onlyImage() {
            type = Config.TYPE_IMAGE;
            return this;
        }

        public Builder maxSelect(int num) {
            maxSelect = num;
            return this;
        }

        public Builder onlyVideo() {
            type = Config.TYPE_VIDEO;
            return this;
        }

        public Builder cropImage() {
            crop = true;
            return this;
        }

        public Builder singleCropIMG() {
            type = Config.TYPE_IMAGE;
            crop = true;
            maxSelect = 1;
            return this;
        }

        public MediaX build() {
            return new MediaX(this);
        }
    }

    public void openCamera(Activity activity) {
        activity.startActivityForResult(newIntent4Camera(activity), REQUEST_OPEN_CAMERA);
        activity.overridePendingTransition(R.anim.mx_anim_up_in, R.anim.mx_anim_fade_out);
    }

    public void openAlbum(Activity activity) {
        if (mCrop && mMaxSelect == 1) {
            SimplePictureSelector.openAlbumSingleCrop(activity);
        } else {
            SimplePictureSelector.openAlbum(activity, mType, mMaxSelect, null);
        }
    }

    public Intent newIntent4Camera(Context context) {
        Intent intent = new Intent(context, WxCameraActivity.class);
        intent.putExtra("type", mType);
        intent.putExtra("crop", mCrop);
        return intent;
    }

    public Intent newIntent4Album(Activity activity) {
        PictureSelectionModel pictureSelectionModel;
        if (mCrop && mMaxSelect == 1) {
            pictureSelectionModel = SimplePictureSelector.createGallerySingleCropPictureSelector(activity);
        } else {
            pictureSelectionModel = SimplePictureSelector.createGalleryPictureSelector(activity, mType, mMaxSelect, null);
        }
        return pictureSelectionModel.newIntent();
    }

    public void openWithDefaultAnim(Activity activity) {
        activity.overridePendingTransition(R.anim.mx_anim_up_in, R.anim.mx_anim_fade_out);
    }

    public static List<LocalMedia> obtainResult(Intent data) {
        if (data != null) {
            if (data.hasExtra(Config.EXTRA_RESULT_LIST)) {
                return data.getParcelableArrayListExtra(Config.EXTRA_RESULT_LIST);
            } else if (data.hasExtra(Config.EXTRA_RESULT_ENTITY)) {
                List<LocalMedia> list = new ArrayList<>();
                list.add(data.getParcelableExtra(Config.EXTRA_RESULT_ENTITY));
                return list;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static List<LocalMedia> obtainResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            return obtainResult(data);
        } else {
            return null;
        }
    }

    public static List<LocalMedia> obtainResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OPEN_CAMERA || requestCode == REQUEST_OPEN_ALBUM) {
            return obtainResult(resultCode, data);
        } else {
            return null;
        }
    }

}
