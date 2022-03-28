package com.github.hueyra.mediax.ui;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.hueyra.mediax.R;
import com.github.hueyra.mediax.adapter.PictureWeChatPreviewGalleryAdapter;
import com.github.hueyra.mediax.app.Config;
import com.github.hueyra.mediax.config.PictureMimeType;
import com.github.hueyra.mediax.config.PictureSelectionConfig;
import com.github.hueyra.mediax.decoration.GridSpacingItemDecoration;
import com.github.hueyra.mediax.decoration.WrapContentLinearLayoutManager;
import com.github.hueyra.mediax.entity.LocalMedia;
import com.github.hueyra.mediax.tools.ScreenUtils;

public class PictureSelectorPreviewWeChatStyleActivity extends PicturePreviewActivity {
    /**
     * alpha duration
     */
    private final static int ALPHA_DURATION = 300;
    private RecyclerView mRvGallery;
    //private View bottomLine;
    private TextView mTvSelected;
    private PictureWeChatPreviewGalleryAdapter mGalleryAdapter;

    @Override
    public int getResourceId() {
        return R.layout.picture_wechat_style_preview;
    }

    private void goneParent() {
        tvMediaNum.setVisibility(View.GONE);
        mTvPictureRight.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(check.getText())) {
            check.setText("");
        }
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        goneParent();
        mRvGallery = findViewById(R.id.rv_gallery);
        //bottomLine = findViewById(R.id.bottomLine);
        mTvPictureOk.setVisibility(View.VISIBLE);
        mTvPictureOk.setText(getString(R.string.picture_send));
        //mTvPictureOk.setOnClickListener(this);
        mCbOriginal.setTextSize(16);
        mTvSelected = findViewById(R.id.tv_selected);
        mTvSelected.setVisibility(View.VISIBLE);
        try {
            if (config.enableCrop) {
                LocalMedia m = adapter.getItem(viewPager.getCurrentItem());
                if (!TextUtils.isEmpty(m.getMimeType())
                        // && !m.isLongImage
                        && PictureMimeType.isIMG(m.getMimeType())) {
                    mPicturePreview.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            //
        }
        mPicturePreview.setEnabled(true);
        mPicturePreview.setText("编辑");
        mPicturePreview.setTextSize(16f);
        mPicturePreview.setTextColor(Color.WHITE);
        mPicturePreview.setOnClickListener(this);
        mGalleryAdapter = new PictureWeChatPreviewGalleryAdapter(config);
        WrapContentLinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(getContext());
        layoutManager.setOrientation(WrapContentLinearLayoutManager.HORIZONTAL);
        mRvGallery.setLayoutManager(layoutManager);
        mRvGallery.addItemDecoration(new GridSpacingItemDecoration(Integer.MAX_VALUE,
                ScreenUtils.dip2px(this, 8), false));
        mRvGallery.setAdapter(mGalleryAdapter);
        mGalleryAdapter.setItemClickListener((position, media, v) -> {
            if (viewPager != null && media != null) {
                if (isEqualsDirectory(media.getParentFolderName(), currentDirectory)) {
                    int newPosition = isBottomPreview ? position : isShowCamera ? media.position - 1 : media.position;
                    viewPager.setCurrentItem(newPosition);
                    //mPicturePreview.setVisibility(media.getMimeType().startsWith("image") ? View.VISIBLE : View.GONE);
                } else {
                    // TODO The picture is not in the album directory, click invalid
                }
            }
        });
        if (isBottomPreview) {
            if (selectData != null && selectData.size() > position) {
                int size = selectData.size();
                for (int i = 0; i < size; i++) {
                    LocalMedia media = selectData.get(i);
                    media.setChecked(false);
                }
                LocalMedia media = selectData.get(position);
                media.setChecked(true);
            }
        } else {
            int size = selectData != null ? selectData.size() : 0;
            for (int i = 0; i < size; i++) {
                LocalMedia media = selectData.get(i);
                if (isEqualsDirectory(media.getParentFolderName(), currentDirectory)) {
                    media.setChecked(isShowCamera ? media.position - 1 == position : media.position == position);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Config.REQUEST_CROP) {
            mGalleryAdapter.setNewData(selectData);
        }

    }

    /**
     * Is it the same directory
     *
     * @param parentFolderName
     * @param currentDirectory
     * @return
     */
    private boolean isEqualsDirectory(String parentFolderName, String currentDirectory) {
        return isBottomPreview
                || TextUtils.isEmpty(parentFolderName)
                || TextUtils.isEmpty(currentDirectory)
                || currentDirectory.equals(getString(R.string.picture_camera_roll))
                || parentFolderName.equals(currentDirectory);
    }

    @Override
    public void initPictureSelectorStyle() {
        super.initPictureSelectorStyle();
        if (PictureSelectionConfig.uiStyle != null) {
            if (!TextUtils.isEmpty(PictureSelectionConfig.uiStyle.picture_top_titleRightDefaultText)) {
                mTvPictureOk.setText(PictureSelectionConfig.uiStyle.picture_top_titleRightDefaultText);
            }
            if (PictureSelectionConfig.uiStyle.picture_top_titleRightTextNormalBackground != 0) {
                mTvPictureOk.setBackgroundResource(PictureSelectionConfig.uiStyle.picture_top_titleRightTextNormalBackground);
            } else {
                mTvPictureOk.setBackgroundResource(R.drawable.picture_send_button_bg);
            }
            if (PictureSelectionConfig.uiStyle.picture_top_titleRightTextSize != 0) {
                mTvPictureOk.setTextSize(PictureSelectionConfig.uiStyle.picture_top_titleRightTextSize);
            }
            if (!TextUtils.isEmpty(PictureSelectionConfig.uiStyle.picture_bottom_selectedText)) {
                mTvSelected.setText(PictureSelectionConfig.uiStyle.picture_bottom_selectedText);
            }
            if (PictureSelectionConfig.uiStyle.picture_bottom_selectedTextSize != 0) {
                mTvSelected.setTextSize(PictureSelectionConfig.uiStyle.picture_bottom_selectedTextSize);
            }
            if (PictureSelectionConfig.uiStyle.picture_bottom_selectedTextColor != 0) {
                mTvSelected.setTextColor(PictureSelectionConfig.uiStyle.picture_bottom_selectedTextColor);
            }
            if (PictureSelectionConfig.uiStyle.picture_bottom_barBackgroundColor != 0) {
                selectBarLayout.setBackgroundColor(PictureSelectionConfig.uiStyle.picture_bottom_barBackgroundColor);
            } else {
                selectBarLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.picture_color_half_grey));
            }

            mTvPictureOk.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));

            if (PictureSelectionConfig.uiStyle.picture_bottom_selectedCheckStyle != 0) {
                check.setBackgroundResource(PictureSelectionConfig.uiStyle.picture_bottom_selectedCheckStyle);
            } else {
                check.setBackgroundResource(R.drawable.picture_wechat_select_cb);
            }

//            if (PictureSelectionConfig.uiStyle.picture_top_leftBack != 0) {
//                pictureLeftBack.setImageResource(PictureSelectionConfig.uiStyle.picture_top_leftBack);
//            } else {
//                pictureLeftBack.setImageResource(R.drawable.picture_icon_back);
//            }

            if (PictureSelectionConfig.uiStyle.picture_bottom_gallery_backgroundColor != 0) {
                mRvGallery.setBackgroundColor(PictureSelectionConfig.uiStyle.picture_bottom_gallery_backgroundColor);
            }

            if (PictureSelectionConfig.uiStyle.picture_bottom_gallery_height > 0) {
                ViewGroup.LayoutParams params = mRvGallery.getLayoutParams();
                params.height = PictureSelectionConfig.uiStyle.picture_bottom_gallery_height;
            }

            if (config.isOriginalControl) {
                if (!TextUtils.isEmpty(PictureSelectionConfig.uiStyle.picture_bottom_originalPictureText)) {
                    mCbOriginal.setText(PictureSelectionConfig.uiStyle.picture_bottom_originalPictureText);
                } else {
                    mCbOriginal.setText(getString(R.string.picture_original_image));
                }
                if (PictureSelectionConfig.uiStyle.picture_bottom_originalPictureTextSize != 0) {
                    mCbOriginal.setTextSize(PictureSelectionConfig.uiStyle.picture_bottom_originalPictureTextSize);
                } else {
                    mCbOriginal.setTextSize(14);
                }
                if (PictureSelectionConfig.uiStyle.picture_bottom_originalPictureTextColor != 0) {
                    mCbOriginal.setTextColor(PictureSelectionConfig.uiStyle.picture_bottom_originalPictureTextColor);
                } else {
                    mCbOriginal.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if (PictureSelectionConfig.uiStyle.picture_bottom_originalPictureCheckStyle != 0) {
                    mCbOriginal.setButtonDrawable(PictureSelectionConfig.uiStyle.picture_bottom_originalPictureCheckStyle);
                } else {
                    mCbOriginal.setButtonDrawable(R.drawable.picture_original_wechat_checkbox);
                }
            }
        } else if (PictureSelectionConfig.style != null) {
            if (PictureSelectionConfig.style.pictureCompleteBackgroundStyle != 0) {
                mTvPictureOk.setBackgroundResource(PictureSelectionConfig.style.pictureCompleteBackgroundStyle);
            } else {
                mTvPictureOk.setBackgroundResource(R.drawable.picture_send_button_bg);
            }
            if (PictureSelectionConfig.style.pictureRightTextSize != 0) {
                mTvPictureOk.setTextSize(PictureSelectionConfig.style.pictureRightTextSize);
            }
            if (!TextUtils.isEmpty(PictureSelectionConfig.style.pictureWeChatPreviewSelectedText)) {
                mTvSelected.setText(PictureSelectionConfig.style.pictureWeChatPreviewSelectedText);
            }
            if (PictureSelectionConfig.style.pictureWeChatPreviewSelectedTextSize != 0) {
                mTvSelected.setTextSize(PictureSelectionConfig.style.pictureWeChatPreviewSelectedTextSize);
            }
            if (PictureSelectionConfig.style.picturePreviewBottomBgColor != 0) {
                selectBarLayout.setBackgroundColor(PictureSelectionConfig.style.picturePreviewBottomBgColor);
            } else {
                selectBarLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.picture_color_half_grey));
            }
            if (PictureSelectionConfig.style.pictureCompleteTextColor != 0) {
                mTvPictureOk.setTextColor(PictureSelectionConfig.style.pictureCompleteTextColor);
            } else {
                if (PictureSelectionConfig.style.pictureCancelTextColor != 0) {
                    mTvPictureOk.setTextColor(PictureSelectionConfig.style.pictureCancelTextColor);
                } else {
                    mTvPictureOk.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));
                }
            }
            if (PictureSelectionConfig.style.pictureOriginalFontColor == 0) {
                mCbOriginal.setTextColor(ContextCompat
                        .getColor(this, R.color.picture_color_white));
            }
            if (PictureSelectionConfig.style.pictureWeChatChooseStyle != 0) {
                check.setBackgroundResource(PictureSelectionConfig.style.pictureWeChatChooseStyle);
            } else {
                check.setBackgroundResource(R.drawable.picture_wechat_select_cb);
            }
            if (config.isOriginalControl) {
                if (PictureSelectionConfig.style.pictureOriginalControlStyle == 0) {
                    mCbOriginal.setButtonDrawable(ContextCompat
                            .getDrawable(this, R.drawable.picture_original_wechat_checkbox));
                }
            }
//            if (PictureSelectionConfig.style.pictureWeChatLeftBackStyle != 0) {
//                pictureLeftBack.setImageResource(PictureSelectionConfig.style.pictureWeChatLeftBackStyle);
//            } else {
//                pictureLeftBack.setImageResource(R.drawable.picture_icon_back);
//            }
            if (!TextUtils.isEmpty(PictureSelectionConfig.style.pictureUnCompleteText)) {
                mTvPictureOk.setText(PictureSelectionConfig.style.pictureUnCompleteText);
            }
        } else {
            mTvPictureOk.setBackgroundResource(R.drawable.picture_send_button_bg);
            mTvPictureOk.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));
            selectBarLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.picture_color_half_grey));
            check.setBackgroundResource(R.drawable.picture_wechat_select_cb);
//            pictureLeftBack.setImageResource(R.drawable.picture_icon_back);
            mCbOriginal.setTextColor(ContextCompat
                    .getColor(this, R.color.picture_color_white));
            if (config.isOriginalControl) {
                mCbOriginal.setButtonDrawable(ContextCompat
                        .getDrawable(this, R.drawable.picture_original_wechat_checkbox));
            }
        }

        onSelectNumChange(false);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
//        if (v.getId() == R.id.picture_id_preview) {
//            //edit
//            LocalMedia media = adapter.getItem(viewPager.getCurrentItem());
//            if (!TextUtils.isEmpty(media.getMimeType()) && media.getMimeType().startsWith("image")) {
//                Intent intent = new Intent(this, IMGEditActivity.class);
//                String path = media.getPath();
//                if (media.isCut() && !TextUtils.isEmpty(media.getCutPath())) {
//                    path = media.getCutPath();
//                }
//                intent.setData(Uri.parse(path));
//                intent.putExtra(IMGEditActivity.EXTRA_IMAGE_SAVE_PATH, UriUtils.getSimpleImageCropName(this));
//                startActivityForResult(intent, Config.REQUEST_CROP);
//                overridePendingTransition(R.anim.picture_anim_enter, R.anim.picture_anim_fade_out);
//            }
//        }
    }

    @Override
    protected void onUpdateSelectedChange(LocalMedia media) {
        onChangeMediaStatus(media);
    }

    @Override
    protected void onSelectedChange(boolean isAddRemove, LocalMedia media) {
        if (isAddRemove) {
            media.setChecked(true);
            if (config.selectionMode == Config.SINGLE) {
                mGalleryAdapter.addSingleMediaToData(media);
            }
        } else {
            media.setChecked(false);
            mGalleryAdapter.removeMediaToData(media);
            if (isBottomPreview) {
                if (selectData != null && selectData.size() > position) {
                    selectData.get(position).setChecked(true);
                }
                if (mGalleryAdapter.isDataEmpty()) {
                    onActivityBackPressed();
                } else {
                    int currentItem = viewPager.getCurrentItem();
                    adapter.remove(currentItem);
                    adapter.removeCacheView(currentItem);
                    position = currentItem;
                    tvTitle.setText(getString(R.string.picture_preview_image_num,
                            position + 1, adapter.getSize()));
                    check.setSelected(true);
                    adapter.notifyDataSetChanged();
                }
            }
        }
        int itemCount = mGalleryAdapter.getItemCount();
        if (itemCount > 5) {
            mRvGallery.smoothScrollToPosition(itemCount - 1);
        }
    }

    @Override
    protected void onPageSelectedChange(LocalMedia media) {
        super.onPageSelectedChange(media);
        goneParent();
        if (!config.previewEggs) {
            onChangeMediaStatus(media);
        }
    }

    /**
     * onChangeMediaStatus
     *
     * @param media
     */
    private void onChangeMediaStatus(LocalMedia media) {
        if (mGalleryAdapter != null) {
            int itemCount = mGalleryAdapter.getItemCount();
            if (itemCount > 0) {
                boolean isChangeData = false;
                for (int i = 0; i < itemCount; i++) {
                    LocalMedia item = mGalleryAdapter.getItem(i);
                    if (item == null || TextUtils.isEmpty(item.getPath())) {
                        continue;
                    }
                    boolean isOldChecked = item.isChecked();
                    boolean isNewChecked = item.getPath().equals(media.getPath()) || item.getId() == media.getId();
                    if (!isChangeData) {
                        isChangeData = (isOldChecked && !isNewChecked) || (!isOldChecked && isNewChecked);
                    }
                    item.setChecked(isNewChecked);
                }
                if (isChangeData) {
                    mGalleryAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    protected void onSelectNumChange(boolean isRefresh) {
        goneParent();
        boolean enable = selectData != null && selectData.size() != 0;
        if (enable) {
            mTvPictureOk.setEnabled(true);
            mTvPictureOk.setSelected(true);
            initCompleteText(selectData.size());
            if (mRvGallery.getVisibility() == View.GONE) {
                mRvGallery.animate().alpha(1).setDuration(ALPHA_DURATION).setInterpolator(new AccelerateInterpolator());
                mRvGallery.setVisibility(View.VISIBLE);
//                bottomLine.animate().alpha(1).setDuration(ALPHA_DURATION).setInterpolator(new AccelerateInterpolator());
//                bottomLine.setVisibility(View.VISIBLE);
                // 重置一片内存区域 不然在其他地方添加也影响这里的数量
                mGalleryAdapter.setNewData(selectData);
            }
            if (PictureSelectionConfig.style != null) {
                if (PictureSelectionConfig.style.pictureCompleteTextColor != 0) {
                    mTvPictureOk.setTextColor(PictureSelectionConfig.style.pictureCompleteTextColor);
                }
                if (PictureSelectionConfig.style.pictureCompleteBackgroundStyle != 0) {
                    mTvPictureOk.setBackgroundResource(PictureSelectionConfig.style.pictureCompleteBackgroundStyle);
                }
            } else {
                mTvPictureOk.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_white));
                mTvPictureOk.setBackgroundResource(R.drawable.picture_send_button_bg);
            }
        } else {
            mTvPictureOk.setEnabled(false);
            mTvPictureOk.setSelected(false);
            mTvPictureOk.setText(getString(R.string.picture_send));
            mTvPictureOk.setBackgroundResource(R.drawable.picture_send_button_default_bg);
            mTvPictureOk.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_53575e));
            mRvGallery.animate().alpha(0).setDuration(ALPHA_DURATION).setInterpolator(new AccelerateInterpolator());
            mRvGallery.setVisibility(View.GONE);
//            bottomLine.animate().alpha(0).setDuration(ALPHA_DURATION).setInterpolator(new AccelerateInterpolator());
//            bottomLine.setVisibility(View.GONE);
        }
    }


    /**
     * initCompleteText
     */
    @Override
    protected void initCompleteText(int startCount) {
        boolean isNotEmptyStyle = PictureSelectionConfig.style != null;
        if (config.isWithVideoImage) {
            // 混选模式
            if (config.selectionMode == Config.SINGLE) {
                if (startCount <= 0) {
                    mTvPictureOk.setText(isNotEmptyStyle && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureUnCompleteText)
                            ? PictureSelectionConfig.style.pictureUnCompleteText : getString(R.string.picture_send));
                } else {
                    boolean isCompleteReplaceNum = isNotEmptyStyle && PictureSelectionConfig.style.isCompleteReplaceNum;
                    if (isCompleteReplaceNum && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureCompleteText)) {
                        mTvPictureOk.setText(String.format(PictureSelectionConfig.style.pictureCompleteText, selectData.size(), 1));
                    } else {
                        mTvPictureOk.setText(isNotEmptyStyle && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureCompleteText)
                                ? PictureSelectionConfig.style.pictureCompleteText : getString(R.string.picture_send));
                    }
                }
            } else {
                boolean isCompleteReplaceNum = isNotEmptyStyle && PictureSelectionConfig.style.isCompleteReplaceNum;
                if (isCompleteReplaceNum && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureCompleteText)) {
                    mTvPictureOk.setText(String.format(PictureSelectionConfig.style.pictureCompleteText,
                            selectData.size(), config.maxSelectNum));
                } else {
                    mTvPictureOk.setText(isNotEmptyStyle && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureUnCompleteText)
                            ? PictureSelectionConfig.style.pictureUnCompleteText : getString(R.string.picture_send_num_new, selectData.size()));
                }
            }
        } else {
            String mimeType = selectData.get(0).getMimeType();
            int maxSize = PictureMimeType.isHasVideo(mimeType) && config.maxVideoSelectNum > 0 ? config.maxVideoSelectNum : config.maxSelectNum;
            if (config.selectionMode == Config.SINGLE) {
                if (startCount <= 0) {
                    mTvPictureOk.setText(isNotEmptyStyle && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureUnCompleteText)
                            ? PictureSelectionConfig.style.pictureUnCompleteText : getString(R.string.picture_send));
                } else {
                    boolean isCompleteReplaceNum = isNotEmptyStyle && PictureSelectionConfig.style.isCompleteReplaceNum;
                    if (isCompleteReplaceNum && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureCompleteText)) {
                        mTvPictureOk.setText(String.format(PictureSelectionConfig.style.pictureCompleteText, selectData.size(),
                                1));
                    } else {
                        mTvPictureOk.setText(isNotEmptyStyle && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureCompleteText)
                                ? PictureSelectionConfig.style.pictureCompleteText : getString(R.string.picture_send));
                    }
                }
            } else {
                boolean isCompleteReplaceNum = isNotEmptyStyle && PictureSelectionConfig.style.isCompleteReplaceNum;
                if (isCompleteReplaceNum && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureCompleteText)) {
                    mTvPictureOk.setText(String.format(PictureSelectionConfig.style.pictureCompleteText, selectData.size(), maxSize));
                } else {
                    mTvPictureOk.setText(isNotEmptyStyle && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureUnCompleteText)
                            ? PictureSelectionConfig.style.pictureUnCompleteText
                            : getString(R.string.picture_send_num_new, selectData.size()));
                }
            }
        }
    }
}
