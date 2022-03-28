package com.github.hueyra.mediax;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.hueyra.mediax.app.Config;
import com.github.hueyra.mediax.config.PictureMimeType;
import com.github.hueyra.mediax.entity.LocalMedia;
import com.github.hueyra.mediax.model.PictureSelectionModel;
import com.github.hueyra.mediax.style.PictureParameterStyle;
import com.github.hueyra.mediax.tools.DoubleUtils;
import com.github.hueyra.mediax.ui.PictureExternalPreviewActivity;
import com.github.hueyra.mediax.ui.PicturePlayAudioActivity;
import com.github.hueyra.mediax.ui.PictureVideoPlayActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PictureSelector {

    private final WeakReference<Activity> mActivity;
    private final WeakReference<Fragment> mFragment;

    private PictureSelector(Activity activity) {
        this(activity, null);
    }

    private PictureSelector(Fragment fragment) {
        this(fragment.getActivity(), fragment);
    }

    private PictureSelector(Activity activity, Fragment fragment) {
        mActivity = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
    }

    /**
     * Start PictureSelector for Activity.
     *
     * @param activity
     * @return PictureSelector instance.
     */
    public static PictureSelector create(Activity activity) {
        return new PictureSelector(activity);
    }

    /**
     * Start PictureSelector for Fragment.
     *
     * @param fragment
     * @return PictureSelector instance.
     */
    public static PictureSelector create(Fragment fragment) {
        return new PictureSelector(fragment);
    }

    /**
     * @param chooseMode Select the type of picture you want，all or Picture or Video .
     * @return LocalMedia PictureSelectionModel
     */
    public PictureSelectionModel openGallery(int chooseMode) {
        return new PictureSelectionModel(this, chooseMode);
    }

    /**
     * @param chooseMode Select the type of picture you want，Picture or Video.
     * @return LocalMedia PictureSelectionModel
     */
    public PictureSelectionModel openCamera(int chooseMode) {
        return new PictureSelectionModel(this, chooseMode, true);
    }

    /**
     * 外部预览时设置样式
     *
     * @param themeStyle
     * @return
     */
    public PictureSelectionModel themeStyle(int themeStyle) {
        return new PictureSelectionModel(this, PictureMimeType.ofImage())
                .theme(themeStyle);
    }

    /**
     * 外部预览时动态代码设置样式
     *
     * @param style
     * @return
     */
    public PictureSelectionModel setPictureStyle(PictureParameterStyle style) {
        return new PictureSelectionModel(this, PictureMimeType.ofImage())
                .setPictureStyle(style);
    }

    /**
     * @param data
     * @return Selector Multiple LocalMedia
     */
    public static List<LocalMedia> obtainMultipleResult(Intent data) {
        if (data != null) {
            List<LocalMedia> result = data.getParcelableArrayListExtra(Config.EXTRA_RESULT_LIST);
            return result == null ? new ArrayList<>() : result;
        }
        return new ArrayList<>();
    }

    /**
     * @param data
     * @return Put image Intent Data
     */
    public static Intent putIntentResult(List<LocalMedia> data) {
        return new Intent().putParcelableArrayListExtra(Config.EXTRA_RESULT_LIST,
                (ArrayList<? extends Parcelable>) data);
    }

    /**
     * @param bundle
     * @return get Selector  LocalMedia
     */
    public static List<LocalMedia> obtainSelectorList(Bundle bundle) {
        if (bundle != null) {
            return bundle.getParcelableArrayList(Config.EXTRA_SELECT_LIST);
        }
        return null;
    }

    /**
     * @param selectedImages
     * @return put Selector  LocalMedia
     */
    public static void saveSelectorList(Bundle outState, List<LocalMedia> selectedImages) {
        outState.putParcelableArrayList(Config.EXTRA_SELECT_LIST,
                (ArrayList<? extends Parcelable>) selectedImages);
    }

    /**
     * set preview image
     *
     * @param position
     * @param medias
     */
    public void externalPicturePreview(int position, List<LocalMedia> medias, int enterAnimation) {
        if (!DoubleUtils.isFastDoubleClick()) {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), PictureExternalPreviewActivity.class);
                intent.putParcelableArrayListExtra(Config.EXTRA_PREVIEW_SELECT_LIST,
                        (ArrayList<? extends Parcelable>) medias);
                intent.putExtra(Config.EXTRA_POSITION, position);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(enterAnimation != 0
                        ? enterAnimation : R.anim.mx_anim_enter, R.anim.mx_anim_fade_in);
            } else {
                throw new NullPointerException("Starting the PictureSelector Activity cannot be empty ");
            }
        }
    }

    /**
     * set preview image
     *
     * @param position
     * @param medias
     * @param directory_path
     */
    public void externalPicturePreview(int position, String directory_path, List<LocalMedia> medias, int enterAnimation) {
        if (!DoubleUtils.isFastDoubleClick()) {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), PictureExternalPreviewActivity.class);
                intent.putParcelableArrayListExtra(Config.EXTRA_PREVIEW_SELECT_LIST, (ArrayList<? extends Parcelable>) medias);
                intent.putExtra(Config.EXTRA_POSITION, position);
                intent.putExtra(Config.EXTRA_DIRECTORY_PATH, directory_path);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(enterAnimation != 0
                        ? enterAnimation : R.anim.mx_anim_enter, R.anim.mx_anim_fade_in);
            } else {
                throw new NullPointerException("Starting the PictureSelector Activity cannot be empty ");
            }
        }
    }

    /**
     * set preview video
     *
     * @param path
     */
    public void externalPictureVideo(String path) {
        if (!DoubleUtils.isFastDoubleClick()) {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), PictureVideoPlayActivity.class);
                intent.putExtra(Config.EXTRA_VIDEO_PATH, path);
                intent.putExtra(Config.EXTRA_PREVIEW_VIDEO, true);
                getActivity().startActivity(intent);
            } else {
                throw new NullPointerException("Starting the PictureSelector Activity cannot be empty ");
            }
        }
    }

    /**
     * set preview audio
     *
     * @param path
     */
    public void externalPictureAudio(String path) {
        if (!DoubleUtils.isFastDoubleClick()) {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), PicturePlayAudioActivity.class);
                intent.putExtra(Config.EXTRA_AUDIO_PATH, path);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.mx_anim_enter, 0);
            } else {
                throw new NullPointerException("Starting the PictureSelector Activity cannot be empty ");
            }
        }
    }

    /**
     * @return Activity.
     */
    @Nullable
    public Activity getActivity() {
        return mActivity.get();
    }

    /**
     * @return Fragment.
     */
    @Nullable
    public Fragment getFragment() {
        return mFragment != null ? mFragment.get() : null;
    }

}
