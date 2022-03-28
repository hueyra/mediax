package com.github.hueyra.mediax.ui;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;

import com.github.hueyra.mediax.R;
import com.github.hueyra.mediax.app.Config;
import com.github.hueyra.mediax.config.PictureMimeType;
import com.github.hueyra.mediax.config.PictureSelectionConfig;
import com.github.hueyra.mediax.entity.LocalMedia;
import com.github.hueyra.mediax.tools.AttrsUtils;

import java.util.List;

public class PictureSelectorWeChatStyleActivity extends PictureSelectorActivity {
    private RelativeLayout rlAlbum;

    @Override
    public int getResourceId() {
        return R.layout.picture_wechat_style_selector;
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        rlAlbum = findViewById(R.id.rlAlbum);
        mTvPictureOk.setOnClickListener(this);
        mTvPictureOk.setText(getString(R.string.picture_send));
        mTvPicturePreview.setTextSize(16);
        mCbOriginal.setTextSize(16);
        boolean isChooseMode = config.selectionMode ==
                Config.SINGLE && config.isSingleDirectReturn;
        mTvPictureOk.setVisibility(isChooseMode ? View.GONE : View.VISIBLE);
        mTvPictureOk.setOnClickListener(this);
//        if (rlAlbum.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
//            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rlAlbum.getLayoutParams();
//            if (isChooseMode) {
//                lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
//            } else {
//                lp.addRule(RelativeLayout.RIGHT_OF, R.id.pictureLeftBack);
//            }
//        }
    }

    @Override
    public void initPictureSelectorStyle() {
        if (PictureSelectionConfig.uiStyle != null) {
            if (PictureSelectionConfig.uiStyle.picture_top_titleRightTextDefaultBackground != 0) {
                mTvPictureOk.setBackgroundResource(PictureSelectionConfig.uiStyle.picture_top_titleRightTextDefaultBackground);
            } else {
                mTvPictureOk.setBackgroundResource(R.drawable.picture_send_button_default_bg);
            }
            if (PictureSelectionConfig.uiStyle.picture_bottom_barBackgroundColor != 0) {
                mBottomLayout.setBackgroundColor(PictureSelectionConfig.uiStyle.picture_bottom_barBackgroundColor);
            } else {
                mBottomLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.picture_color_grey));
            }
            if (PictureSelectionConfig.uiStyle.picture_top_titleRightTextColor.length > 0) {
                ColorStateList colorStateList = AttrsUtils.getColorStateList(PictureSelectionConfig.uiStyle.picture_top_titleRightTextColor);
                if (colorStateList != null) {
                    mTvPictureOk.setTextColor(colorStateList);
                }
            } else {
                mTvPictureOk.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_53575e));
            }
            if (PictureSelectionConfig.uiStyle.picture_top_titleRightTextSize != 0) {
                mTvPictureOk.setTextSize(PictureSelectionConfig.uiStyle.picture_top_titleRightTextSize);
            }

            if (config.isOriginalControl) {
                if (PictureSelectionConfig.uiStyle.picture_bottom_originalPictureCheckStyle != 0) {
                    mCbOriginal.setButtonDrawable(PictureSelectionConfig.uiStyle.picture_bottom_originalPictureCheckStyle);
                }
                if (PictureSelectionConfig.uiStyle.picture_bottom_originalPictureTextColor != 0) {
                    mCbOriginal.setTextColor(PictureSelectionConfig.uiStyle.picture_bottom_originalPictureTextColor);
                }
                if (PictureSelectionConfig.uiStyle.picture_bottom_originalPictureTextSize != 0) {
                    mCbOriginal.setTextSize(PictureSelectionConfig.uiStyle.picture_bottom_originalPictureTextSize);
                }
            }
            if (PictureSelectionConfig.uiStyle.picture_container_backgroundColor != 0) {
                container.setBackgroundColor(PictureSelectionConfig.uiStyle.picture_container_backgroundColor);
            }
            if (PictureSelectionConfig.uiStyle.picture_top_titleAlbumBackground != 0) {
                rlAlbum.setBackgroundResource(PictureSelectionConfig.uiStyle.picture_top_titleAlbumBackground);
            } else {
                rlAlbum.setBackgroundResource(R.drawable.picture_album_bg);
            }
            if (!TextUtils.isEmpty(PictureSelectionConfig.uiStyle.picture_top_titleRightDefaultText)) {
                mTvPictureOk.setText(PictureSelectionConfig.uiStyle.picture_top_titleRightDefaultText);
            }

        } else if (PictureSelectionConfig.style != null) {
            if (PictureSelectionConfig.style.pictureUnCompleteBackgroundStyle != 0) {
                mTvPictureOk.setBackgroundResource(PictureSelectionConfig.style.pictureUnCompleteBackgroundStyle);
            } else {
                mTvPictureOk.setBackgroundResource(R.drawable.picture_send_button_default_bg);
            }
            if (PictureSelectionConfig.style.pictureBottomBgColor != 0) {
                mBottomLayout.setBackgroundColor(PictureSelectionConfig.style.pictureBottomBgColor);
            } else {
                mBottomLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.picture_color_grey));
            }
            if (PictureSelectionConfig.style.pictureUnCompleteTextColor != 0) {
                mTvPictureOk.setTextColor(PictureSelectionConfig.style.pictureUnCompleteTextColor);
            } else {
                if (PictureSelectionConfig.style.pictureCancelTextColor != 0) {
                    mTvPictureOk.setTextColor(PictureSelectionConfig.style.pictureCancelTextColor);
                } else {
                    mTvPictureOk.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_53575e));
                }
            }
            if (PictureSelectionConfig.style.pictureRightTextSize != 0) {
                mTvPictureOk.setTextSize(PictureSelectionConfig.style.pictureRightTextSize);
            }
            if (PictureSelectionConfig.style.pictureOriginalFontColor == 0) {
                mCbOriginal.setTextColor(ContextCompat
                        .getColor(this, R.color.picture_color_white));
            }
            if (config.isOriginalControl) {
                if (PictureSelectionConfig.style.pictureOriginalControlStyle == 0) {
                    mCbOriginal.setButtonDrawable(ContextCompat
                            .getDrawable(this, R.drawable.picture_original_wechat_checkbox));
                }
            }
            if (PictureSelectionConfig.style.pictureContainerBackgroundColor != 0) {
                container.setBackgroundColor(PictureSelectionConfig.style.pictureContainerBackgroundColor);
            }

            if (PictureSelectionConfig.style.pictureWeChatTitleBackgroundStyle != 0) {
                rlAlbum.setBackgroundResource(PictureSelectionConfig.style.pictureWeChatTitleBackgroundStyle);
            } else {
                rlAlbum.setBackgroundResource(R.drawable.picture_album_bg);
            }

            if (!TextUtils.isEmpty(PictureSelectionConfig.style.pictureUnCompleteText)) {
                mTvPictureOk.setText(PictureSelectionConfig.style.pictureUnCompleteText);
            }

        } else {
            mTvPictureOk.setBackgroundResource(R.drawable.picture_send_button_default_bg);
            rlAlbum.setBackgroundResource(R.drawable.picture_album_bg);
            mTvPictureOk.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_53575e));
            int pictureBottomBgColor = AttrsUtils.getTypeValueColor(getContext(), R.attr.picture_bottom_bg);
            mBottomLayout.setBackgroundColor(pictureBottomBgColor != 0
                    ? pictureBottomBgColor : ContextCompat.getColor(getContext(), R.color.picture_color_grey));

            mCbOriginal.setTextColor(ContextCompat
                    .getColor(this, R.color.picture_color_white));
            Drawable drawable = ContextCompat.getDrawable(this, R.mipmap.picture_icon_wechat_down);
            mIvArrow.setImageDrawable(drawable);
            if (config.isOriginalControl) {
                mCbOriginal.setButtonDrawable(ContextCompat
                        .getDrawable(this, R.drawable.picture_original_wechat_checkbox));
            }
        }
        super.initPictureSelectorStyle();
        goneParentView();
    }

    /**
     * Hide views that are not needed by the parent container
     */
    private void goneParentView() {
        mTvPictureImgNum.setVisibility(View.GONE);
        mTvPictureRight.setVisibility(View.GONE);
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void changeImageNumber(List<LocalMedia> selectData) {
        int size = selectData.size();
        boolean enable = size != 0;
        if (enable) {
            mTvPictureOk.setEnabled(true);
            mTvPictureOk.setTextColor(Color.WHITE);
            mTvPictureOk.setSelected(true);
            mTvPicturePreview.setEnabled(true);
            mTvPicturePreview.setSelected(true);
            initCompleteText(selectData);
            if (PictureSelectionConfig.uiStyle != null) {
                if (PictureSelectionConfig.uiStyle.picture_top_titleRightTextNormalBackground != 0) {
                    mTvPictureOk.setBackgroundResource(PictureSelectionConfig.uiStyle.picture_top_titleRightTextNormalBackground);
                } else {
                    mTvPictureOk.setBackgroundResource(R.drawable.picture_send_button_bg);
                }
                if (PictureSelectionConfig.uiStyle.picture_bottom_previewTextColor.length > 0) {
                    ColorStateList colorStateList = AttrsUtils.getColorStateList(PictureSelectionConfig.uiStyle.picture_bottom_previewTextColor);
                    if (colorStateList != null) {
                        mTvPicturePreview.setTextColor(colorStateList);
                    }
                } else {
                    mTvPicturePreview.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));
                }
                mTvPicturePreview.setText(getString(R.string.picture_preview));
            } else if (PictureSelectionConfig.style != null) {
                if (PictureSelectionConfig.style.pictureCompleteBackgroundStyle != 0) {
                    mTvPictureOk.setBackgroundResource(PictureSelectionConfig.style.pictureCompleteBackgroundStyle);
                } else {
                    mTvPictureOk.setBackgroundResource(R.drawable.picture_send_button_bg);
                }
                mTvPictureOk.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));
                if (PictureSelectionConfig.style.picturePreviewTextColor != 0) {
                    mTvPicturePreview.setTextColor(PictureSelectionConfig.style.picturePreviewTextColor);
                } else {
                    mTvPicturePreview.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));
                }
                mTvPicturePreview.setText(getString(R.string.picture_preview));
            } else {
                mTvPictureOk.setBackgroundResource(R.drawable.picture_send_button_bg);
                mTvPictureOk.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));
                mTvPicturePreview.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));
                mTvPicturePreview.setText(getString(R.string.picture_preview));
            }
        } else {
            mTvPictureOk.setEnabled(false);
            mTvPictureOk.setSelected(false);
            mTvPictureOk.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_53575e));
            mTvPicturePreview.setEnabled(false);
            mTvPicturePreview.setSelected(false);
            if (PictureSelectionConfig.uiStyle != null) {
                if (PictureSelectionConfig.uiStyle.picture_top_titleRightTextDefaultBackground != 0) {
                    mTvPictureOk.setBackgroundResource(PictureSelectionConfig.uiStyle.picture_top_titleRightTextDefaultBackground);
                } else {
                    mTvPictureOk.setBackgroundResource(R.drawable.picture_send_button_default_bg);
                }
                if (!TextUtils.isEmpty(PictureSelectionConfig.uiStyle.picture_top_titleRightDefaultText)) {
                    mTvPictureOk.setText(PictureSelectionConfig.uiStyle.picture_top_titleRightDefaultText);
                } else {
                    mTvPictureOk.setText(getString(R.string.picture_send));
                }
                mTvPicturePreview.setText(getString(R.string.picture_preview));
            } else if (PictureSelectionConfig.style != null) {
                if (PictureSelectionConfig.style.pictureUnCompleteBackgroundStyle != 0) {
                    mTvPictureOk.setBackgroundResource(PictureSelectionConfig.style.pictureUnCompleteBackgroundStyle);
                } else {
                    mTvPictureOk.setBackgroundResource(R.drawable.picture_send_button_default_bg);
                }
                if (PictureSelectionConfig.style.pictureUnCompleteTextColor != 0) {
                    mTvPictureOk.setTextColor(PictureSelectionConfig.style.pictureUnCompleteTextColor);
                } else {
                    mTvPictureOk.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_53575e));
                }
                if (PictureSelectionConfig.style.pictureUnPreviewTextColor != 0) {
                    mTvPicturePreview.setTextColor(PictureSelectionConfig.style.pictureUnPreviewTextColor);
                } else {
                    mTvPicturePreview.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_9b));
                }
                if (!TextUtils.isEmpty(PictureSelectionConfig.style.pictureUnCompleteText)) {
                    mTvPictureOk.setText(PictureSelectionConfig.style.pictureUnCompleteText);
                } else {
                    mTvPictureOk.setText(getString(R.string.picture_send));
                }
                mTvPicturePreview.setText(getString(R.string.picture_preview));
            } else {
                mTvPictureOk.setBackgroundResource(R.drawable.picture_send_button_default_bg);
                mTvPictureOk.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_53575e));
                mTvPicturePreview.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_9b));
                mTvPicturePreview.setText(getString(R.string.picture_preview));
                mTvPictureOk.setText(getString(R.string.picture_send));
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    protected void onChangeData(List<LocalMedia> list) {
        super.onChangeData(list);
        initCompleteText(list);
    }

    @Override
    protected void initCompleteText(List<LocalMedia> list) {
        int size = list.size();
        boolean isNotEmptyStyle = PictureSelectionConfig.style != null;
        if (config.isWithVideoImage) {
            if (config.selectionMode == Config.SINGLE) {
                if (size <= 0) {
                    mTvPictureOk.setText(isNotEmptyStyle && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureUnCompleteText)
                            ? PictureSelectionConfig.style.pictureUnCompleteText : getString(R.string.picture_send));
                } else {
                    boolean isCompleteReplaceNum = isNotEmptyStyle && PictureSelectionConfig.style.isCompleteReplaceNum;
                    if (isCompleteReplaceNum && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureCompleteText)) {
                        mTvPictureOk.setText(String.format(PictureSelectionConfig.style.pictureCompleteText, size, 1));
                    } else {
                        mTvPictureOk.setText(isNotEmptyStyle && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureCompleteText)
                                ? PictureSelectionConfig.style.pictureCompleteText : getString(R.string.picture_send));
                    }
                }
            } else {
                boolean isCompleteReplaceNum = isNotEmptyStyle && PictureSelectionConfig.style.isCompleteReplaceNum;
                if (isCompleteReplaceNum && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureCompleteText)) {
                    mTvPictureOk.setText(String.format(PictureSelectionConfig.style.pictureCompleteText, size, config.maxSelectNum));
                } else {
                    mTvPictureOk.setText(isNotEmptyStyle && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureUnCompleteText)
                            ? PictureSelectionConfig.style.pictureUnCompleteText : getString(R.string.picture_send_num_new, size));
                }
            }
        } else {
            String mimeType = list.get(0).getMimeType();
            int maxSize = PictureMimeType.isHasVideo(mimeType) && config.maxVideoSelectNum > 0 ? config.maxVideoSelectNum : config.maxSelectNum;
            if (config.selectionMode == Config.SINGLE) {
                boolean isCompleteReplaceNum = isNotEmptyStyle && PictureSelectionConfig.style.isCompleteReplaceNum;
                if (isCompleteReplaceNum && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureCompleteText)) {
                    mTvPictureOk.setText(String.format(PictureSelectionConfig.style.pictureCompleteText, size, 1));
                } else {
                    mTvPictureOk.setText(isNotEmptyStyle && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureCompleteText)
                            ? PictureSelectionConfig.style.pictureCompleteText : getString(R.string.picture_send));
                }
            } else {
                boolean isCompleteReplaceNum = isNotEmptyStyle && PictureSelectionConfig.style.isCompleteReplaceNum;
                if (isCompleteReplaceNum && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureCompleteText)) {
                    mTvPictureOk.setText(String.format(PictureSelectionConfig.style.pictureCompleteText, size, maxSize));
                } else {
                    mTvPictureOk.setText(isNotEmptyStyle && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureUnCompleteText)
                            ? PictureSelectionConfig.style.pictureUnCompleteText : getString(R.string.picture_send_num_new, size));
                }
            }
        }
    }
}
