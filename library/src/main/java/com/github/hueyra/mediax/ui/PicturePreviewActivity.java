package com.github.hueyra.mediax.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.github.hueyra.mediax.R;
import com.github.hueyra.mediax.adapter.PictureSimpleFragmentAdapter;
import com.github.hueyra.mediax.app.Config;
import com.github.hueyra.mediax.config.PictureMimeType;
import com.github.hueyra.mediax.config.PictureSelectionConfig;
import com.github.hueyra.mediax.entity.LocalMedia;
import com.github.hueyra.mediax.listener.OnQueryDataResultListener;
import com.github.hueyra.mediax.model.LocalMediaPageLoader;
import com.github.hueyra.mediax.observable.ImagesObservable;
import com.github.hueyra.mediax.tools.AttrsUtils;
import com.github.hueyra.mediax.tools.MediaUtils;
import com.github.hueyra.mediax.tools.ScreenUtils;
import com.github.hueyra.mediax.tools.StringUtils;
import com.github.hueyra.mediax.tools.ToastUtils;
import com.github.hueyra.mediax.tools.ValueOf;
import com.github.hueyra.mediax.tools.VoiceUtils;
import com.github.hueyra.mediax.widget.PreviewViewPager;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PicturePreviewActivity extends PictureBaseActivity implements
        View.OnClickListener, PictureSimpleFragmentAdapter.OnCallBackActivity {
    private static final String TAG = PicturePreviewActivity.class.getSimpleName();
    protected ViewGroup mTitleBar;
    protected ImageView pictureLeftBack;
    protected TextView mTvPictureRight, tvMediaNum, tvTitle, mTvPictureOk;
    protected ImageView mIvArrow;
    protected PreviewViewPager viewPager;
    protected TextView mPicturePreview;
    protected int position;
    protected boolean isBottomPreview;
    private int totalNumber;
    protected List<LocalMedia> selectData = new ArrayList<>();
    protected PictureSimpleFragmentAdapter adapter;
    protected Animation animation;
    protected TextView check;
    protected View btnCheck;
    protected boolean refresh;
    protected int index;
    protected int screenWidth;
    protected Handler mHandler;
    protected RelativeLayout selectBarLayout;
    protected CheckBox mCbOriginal;
    protected boolean isShowCamera;
    protected String currentDirectory;
    /**
     * 是否已完成选择
     */
    protected boolean isCompleteOrSelected;
    /**
     * 是否改变已选的数据
     */
    protected boolean isChangeSelectedData;

    /**
     * 分页码
     */
    private int mPage = 0;


    @Override
    public int getResourceId() {
        return R.layout.picture_preview;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            // 防止内存不足时activity被回收，导致图片未选中
            List<LocalMedia> cacheData = com.github.hueyra.mediax.PictureSelector.obtainSelectorList(savedInstanceState);
            selectData = cacheData != null ? cacheData : selectData;
            isCompleteOrSelected = savedInstanceState.getBoolean(Config.EXTRA_COMPLETE_SELECTED, false);
            isChangeSelectedData = savedInstanceState.getBoolean(Config.EXTRA_CHANGE_SELECTED_DATA, false);
            onImageChecked(position);
            onSelectNumChange(false);
        }
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        mHandler = new Handler();
        mTitleBar = findViewById(R.id.titleBar);
        screenWidth = ScreenUtils.getScreenWidth(this);
        animation = AnimationUtils.loadAnimation(this, R.anim.mx_anim_modal_in);
        pictureLeftBack = findViewById(R.id.pictureLeftBack);
        mTvPictureRight = findViewById(R.id.picture_right);
        mIvArrow = findViewById(R.id.ivArrow);
        viewPager = findViewById(R.id.preview_pager);
        mPicturePreview = findViewById(R.id.picture_id_preview);
        btnCheck = findViewById(R.id.btnCheck);
        check = findViewById(R.id.check);
        pictureLeftBack.setOnClickListener(this);
        pictureLeftBack.setImageResource(R.mipmap.picture_icon_back);
        mTvPictureOk = findViewById(R.id.picture_tv_ok);
        mCbOriginal = findViewById(R.id.cb_original);
        tvMediaNum = findViewById(R.id.tv_media_num);
        selectBarLayout = findViewById(R.id.select_bar_layout);
        mTvPictureOk.setOnClickListener(this);
        tvMediaNum.setOnClickListener(this);
        tvTitle = findViewById(R.id.picture_title);
        mPicturePreview.setVisibility(View.GONE);
        mIvArrow.setVisibility(View.GONE);
        mTvPictureRight.setVisibility(View.GONE);

        check.setVisibility(View.VISIBLE);
        btnCheck.setVisibility(View.VISIBLE);

        position = getIntent().getIntExtra(Config.EXTRA_POSITION, 0);
        if (numComplete) {
            initCompleteText(0);
        }
        tvMediaNum.setSelected(config.checkNumMode);
        btnCheck.setOnClickListener(this);
        if (getIntent().getParcelableArrayListExtra(Config.EXTRA_SELECT_LIST) != null) {
            selectData = getIntent().getParcelableArrayListExtra(Config.EXTRA_SELECT_LIST);
        }
        isBottomPreview = getIntent().getBooleanExtra(Config.EXTRA_BOTTOM_PREVIEW, false);
        isShowCamera = getIntent().getBooleanExtra(Config.EXTRA_SHOW_CAMERA, config.isCamera);
        // 当前目录
        currentDirectory = getIntent().getStringExtra(Config.EXTRA_IS_CURRENT_DIRECTORY);
        if (isBottomPreview) {
            // 底部预览模式
            List<LocalMedia> data = getIntent().getParcelableArrayListExtra(Config.EXTRA_PREVIEW_SELECT_LIST);
            initViewPageAdapterData(data);
        } else {
            List<LocalMedia> data = new ArrayList<>(ImagesObservable.getInstance().readPreviewMediaData());
            boolean isEmpty = data.size() == 0;
            totalNumber = getIntent().getIntExtra(Config.EXTRA_DATA_COUNT, 0);
            if (config.isPageStrategy) {
                if (isEmpty) {
                    // 这种情况有可能是单例被回收了导致readPreviewMediaData();返回的数据为0，那就从第一页开始加载吧
                    setNewTitle();
                } else {
                    mPage = getIntent().getIntExtra(Config.EXTRA_PAGE, 0);
                }
                initViewPageAdapterData(data);
                loadData();
                setTitle();
            } else {
                initViewPageAdapterData(data);
                if (isEmpty) {
                    // 这种情况有可能是单例被回收了导致readPreviewMediaData();返回的数据为0，暂时自动切换成分页模式去获取数据
                    config.isPageStrategy = true;
                    setNewTitle();
                    loadData();
                }
            }
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                isPreviewEggs(config.previewEggs, position, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int i) {
                position = i;
                setTitle();
                LocalMedia media = adapter.getItem(position);
                if (media == null) {
                    return;
                }
                index = media.getPosition();
                if (!config.previewEggs) {
                    if (config.checkNumMode) {
                        check.setText(ValueOf.toString(media.getNum()));
                        notifyCheckChanged(media);
                    }
                    onImageChecked(position);
                }
                if (config.isOriginalControl) {
                    boolean isHasVideo = PictureMimeType.isHasVideo(media.getMimeType());
                    mCbOriginal.setVisibility(isHasVideo ? View.GONE : View.VISIBLE);
                    mCbOriginal.setChecked(config.isCheckOriginalImage);
                }
                if (config.enableCrop
                        //&& !media.isLongImage
                        && !TextUtils.isEmpty(media.getMimeType())
                        && PictureMimeType.isIMG(media.getMimeType())) {
                    //mPicturePreview.setVisibility(View.VISIBLE);
                } else {
                    mPicturePreview.setVisibility(View.GONE);
                }
                //次APP不需要编辑图片，暂时隐藏
                mPicturePreview.setVisibility(View.GONE);

                onPageSelectedChange(media);

                if (config.isPageStrategy && !isBottomPreview) {
                    if (isHasMore) {
                        // 滑到adapter.getSize() - Config.MIN_PAGE_SIZE时或最后一条时预加载
                        if (position == (adapter.getSize() - 1) - Config.MIN_PAGE_SIZE || position == adapter.getSize() - 1) {
                            loadMoreData();
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        // 原图
        if (config.isOriginalControl) {
            boolean isCheckOriginal = getIntent()
                    .getBooleanExtra(Config.EXTRA_CHANGE_ORIGINAL, config.isCheckOriginalImage);
            mCbOriginal.setVisibility(View.VISIBLE);
            config.isCheckOriginalImage = isCheckOriginal;
            mCbOriginal.setChecked(config.isCheckOriginalImage);
            mCbOriginal.setOnCheckedChangeListener((buttonView, isChecked) -> {
                config.isCheckOriginalImage = isChecked;
            });
        }
    }

    /**
     * 从本地获取数据
     */
    private void loadData() {
        long bucketId = getIntent().getLongExtra(Config.EXTRA_BUCKET_ID, -1);
        mPage++;
        LocalMediaPageLoader.getInstance(getContext()).loadPageMediaData(bucketId, mPage, config.pageSize,
                (OnQueryDataResultListener<LocalMedia>) (result, currentPage, isHasMore) -> {
                    if (!isFinishing()) {
                        this.isHasMore = isHasMore;
                        if (isHasMore) {
                            int size = result.size();
                            if (size > 0 && adapter != null) {
                                adapter.getData().addAll(result);
                                adapter.notifyDataSetChanged();
                            } else {
                                // 这种情况就是开启过滤损坏文件刚好导致某一页全是损坏的虽然result为0，但还要请求下一页数据
                                loadMoreData();
                            }
                        }
                    }
                });
    }

    /**
     * 加载更多
     */
    private void loadMoreData() {
        long bucketId = getIntent().getLongExtra(Config.EXTRA_BUCKET_ID, -1);
        mPage++;
        LocalMediaPageLoader.getInstance(getContext()).loadPageMediaData(bucketId, mPage, config.pageSize,
                (OnQueryDataResultListener<LocalMedia>) (result, currentPage, isHasMore) -> {
                    if (!isFinishing()) {
                        this.isHasMore = isHasMore;
                        if (isHasMore) {
                            int size = result.size();
                            if (size > 0 && adapter != null) {
                                adapter.getData().addAll(result);
                                adapter.notifyDataSetChanged();
                            } else {
                                // 这种情况就是开启过滤损坏文件刚好导致某一页全是损坏的虽然result为0，但还要请求下一页数据
                                loadMoreData();
                            }
                        }
                    }
                });
    }

    @Override
    protected void initCompleteText(int startCount) {
        if (config.selectionMode == Config.SINGLE) {
            if (startCount <= 0) {
                // 未选择任何图片
                if (PictureSelectionConfig.uiStyle != null) {
                    mTvPictureOk.setText(!TextUtils.isEmpty(PictureSelectionConfig.uiStyle.picture_bottom_completeDefaultText)
                            ? PictureSelectionConfig.uiStyle.picture_bottom_completeDefaultText : getString(R.string.picture_please_select));
                } else if (PictureSelectionConfig.style != null) {
                    mTvPictureOk.setText(!TextUtils.isEmpty(PictureSelectionConfig.style.pictureUnCompleteText)
                            ? PictureSelectionConfig.style.pictureUnCompleteText : getString(R.string.picture_please_select));
                }
            } else {
                // 已选择
                if (PictureSelectionConfig.uiStyle != null) {
                    if (PictureSelectionConfig.uiStyle.isCompleteReplaceNum && !TextUtils.isEmpty(PictureSelectionConfig.uiStyle.picture_bottom_completeNormalText)) {
                        mTvPictureOk.setText(String.format(PictureSelectionConfig.uiStyle.picture_bottom_completeNormalText, startCount, 1));
                    } else {
                        mTvPictureOk.setText(!TextUtils.isEmpty(PictureSelectionConfig.uiStyle.picture_bottom_completeNormalText)
                                ? PictureSelectionConfig.uiStyle.picture_bottom_completeNormalText : getString(R.string.picture_done));
                    }
                } else if (PictureSelectionConfig.style != null) {
                    if (PictureSelectionConfig.style.isCompleteReplaceNum && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureCompleteText)) {
                        mTvPictureOk.setText(String.format(PictureSelectionConfig.style.pictureCompleteText, startCount, 1));
                    } else {
                        mTvPictureOk.setText(!TextUtils.isEmpty(PictureSelectionConfig.style.pictureCompleteText)
                                ? PictureSelectionConfig.style.pictureCompleteText : getString(R.string.picture_done));
                    }
                }
            }
        } else {
            if (startCount <= 0) {
                // 未选择任何图片
                if (PictureSelectionConfig.uiStyle != null) {
                    mTvPictureOk.setText(PictureSelectionConfig.uiStyle.isCompleteReplaceNum && !TextUtils.isEmpty(PictureSelectionConfig.uiStyle.picture_bottom_completeDefaultText)
                            ? String.format(PictureSelectionConfig.uiStyle.picture_bottom_completeDefaultText, startCount, config.maxSelectNum) : getString(R.string.picture_done_front_num,
                            startCount, config.maxSelectNum));
                } else if (PictureSelectionConfig.style != null) {
                    mTvPictureOk.setText(PictureSelectionConfig.style.isCompleteReplaceNum && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureUnCompleteText)
                            ? PictureSelectionConfig.style.pictureUnCompleteText : getString(R.string.picture_done_front_num,
                            startCount, config.maxSelectNum));
                }
            } else {
                // 已选择
                if (PictureSelectionConfig.uiStyle != null) {
                    if (PictureSelectionConfig.uiStyle.isCompleteReplaceNum && !TextUtils.isEmpty(PictureSelectionConfig.uiStyle.picture_bottom_completeNormalText)) {
                        mTvPictureOk.setText(String.format(PictureSelectionConfig.uiStyle.picture_bottom_completeNormalText, startCount, config.maxSelectNum));
                    } else {
                        mTvPictureOk.setText(getString(R.string.picture_done_front_num, startCount, config.maxSelectNum));
                    }
                } else if (PictureSelectionConfig.style != null) {
                    if (PictureSelectionConfig.style.isCompleteReplaceNum && !TextUtils.isEmpty(PictureSelectionConfig.style.pictureCompleteText)) {
                        mTvPictureOk.setText(String.format(PictureSelectionConfig.style.pictureCompleteText, startCount, config.maxSelectNum));
                    } else {
                        mTvPictureOk.setText(getString(R.string.picture_done_front_num, startCount, config.maxSelectNum));
                    }
                }
            }
        }
    }

    /**
     * ViewPage滑动数据变化回调
     *
     * @param media
     */
    protected void onPageSelectedChange(LocalMedia media) {

    }

    /**
     * 动态设置相册主题
     */
    @Override
    public void initPictureSelectorStyle() {
        if (PictureSelectionConfig.uiStyle != null) {
            if (PictureSelectionConfig.uiStyle.picture_top_titleTextColor != 0) {
                tvTitle.setTextColor(PictureSelectionConfig.uiStyle.picture_top_titleTextColor);
            }
            if (PictureSelectionConfig.uiStyle.picture_top_titleTextSize != 0) {
                tvTitle.setTextSize(PictureSelectionConfig.uiStyle.picture_top_titleTextSize);
            }
//            if (PictureSelectionConfig.uiStyle.picture_top_leftBack != 0) {
//                pictureLeftBack.setImageResource(PictureSelectionConfig.uiStyle.picture_top_leftBack);
//            }
            if (PictureSelectionConfig.uiStyle.picture_bottom_barBackgroundColor != 0) {
                selectBarLayout.setBackgroundColor(PictureSelectionConfig.uiStyle.picture_bottom_barBackgroundColor);
            }
            if (PictureSelectionConfig.uiStyle.picture_bottom_completeRedDotBackground != 0) {
                tvMediaNum.setBackgroundResource(PictureSelectionConfig.uiStyle.picture_bottom_completeRedDotBackground);
            }
            if (PictureSelectionConfig.uiStyle.picture_check_style != 0) {
                check.setBackgroundResource(PictureSelectionConfig.uiStyle.picture_check_style);
            }
            if (PictureSelectionConfig.uiStyle.picture_bottom_completeTextColor.length > 0) {
                ColorStateList colorStateList = AttrsUtils.getColorStateList(PictureSelectionConfig.uiStyle.picture_bottom_completeTextColor);
                if (colorStateList != null) {
                    mTvPictureOk.setTextColor(colorStateList);
                }
            }
            if (!TextUtils.isEmpty(PictureSelectionConfig.uiStyle.picture_bottom_completeDefaultText)) {
                mTvPictureOk.setText(PictureSelectionConfig.uiStyle.picture_bottom_completeDefaultText);
            }
            if (PictureSelectionConfig.uiStyle.picture_top_titleBarHeight > 0) {
                ViewGroup.LayoutParams params = mTitleBar.getLayoutParams();
                params.height = PictureSelectionConfig.uiStyle.picture_top_titleBarHeight;
            }
            if (PictureSelectionConfig.uiStyle.picture_bottom_barHeight > 0) {
                ViewGroup.LayoutParams params = selectBarLayout.getLayoutParams();
                params.height = PictureSelectionConfig.uiStyle.picture_bottom_barHeight;
            }

            if (config.isOriginalControl) {
                if (PictureSelectionConfig.uiStyle.picture_bottom_originalPictureCheckStyle != 0) {
                    mCbOriginal.setButtonDrawable(PictureSelectionConfig.uiStyle.picture_bottom_originalPictureCheckStyle);
                } else {
                    mCbOriginal.setButtonDrawable(ContextCompat.getDrawable(this, R.drawable.picture_original_checkbox));
                }
                if (PictureSelectionConfig.uiStyle.picture_bottom_originalPictureTextColor != 0) {
                    mCbOriginal.setTextColor(PictureSelectionConfig.uiStyle.picture_bottom_originalPictureTextColor);
                } else {
                    mCbOriginal.setTextColor(ContextCompat.getColor(this, R.color.picture_color_53575e));
                }
                if (PictureSelectionConfig.uiStyle.picture_bottom_originalPictureTextSize != 0) {
                    mCbOriginal.setTextSize(PictureSelectionConfig.uiStyle.picture_bottom_originalPictureTextSize);
                }
            } else {
                mCbOriginal.setButtonDrawable(ContextCompat.getDrawable(this, R.drawable.picture_original_checkbox));
                mCbOriginal.setTextColor(ContextCompat.getColor(this, R.color.picture_color_53575e));
            }
        } else if (PictureSelectionConfig.style != null) {
            if (PictureSelectionConfig.style.pictureTitleTextColor != 0) {
                tvTitle.setTextColor(PictureSelectionConfig.style.pictureTitleTextColor);
            }
            if (PictureSelectionConfig.style.pictureTitleTextSize != 0) {
                tvTitle.setTextSize(PictureSelectionConfig.style.pictureTitleTextSize);
            }
//            if (PictureSelectionConfig.style.pictureLeftBackIcon != 0) {
//                pictureLeftBack.setImageResource(PictureSelectionConfig.style.pictureLeftBackIcon);
//            }
            if (PictureSelectionConfig.style.picturePreviewBottomBgColor != 0) {
                selectBarLayout.setBackgroundColor(PictureSelectionConfig.style.picturePreviewBottomBgColor);
            }
            if (PictureSelectionConfig.style.pictureCheckNumBgStyle != 0) {
                tvMediaNum.setBackgroundResource(PictureSelectionConfig.style.pictureCheckNumBgStyle);
            }
            if (PictureSelectionConfig.style.pictureCheckedStyle != 0) {
                check.setBackgroundResource(PictureSelectionConfig.style.pictureCheckedStyle);
            }
            if (PictureSelectionConfig.style.pictureUnCompleteTextColor != 0) {
                mTvPictureOk.setTextColor(PictureSelectionConfig.style.pictureUnCompleteTextColor);
            }
            if (!TextUtils.isEmpty(PictureSelectionConfig.style.pictureUnCompleteText)) {
                mTvPictureOk.setText(PictureSelectionConfig.style.pictureUnCompleteText);
            }
            if (PictureSelectionConfig.style.pictureTitleBarHeight > 0) {
                ViewGroup.LayoutParams params = mTitleBar.getLayoutParams();
                params.height = PictureSelectionConfig.style.pictureTitleBarHeight;
            }
            if (config.isOriginalControl) {
                if (PictureSelectionConfig.style.pictureOriginalControlStyle != 0) {
                    mCbOriginal.setButtonDrawable(PictureSelectionConfig.style.pictureOriginalControlStyle);
                } else {
                    mCbOriginal.setButtonDrawable(ContextCompat.getDrawable(this, R.drawable.picture_original_checkbox));
                }
                if (PictureSelectionConfig.style.pictureOriginalFontColor != 0) {
                    mCbOriginal.setTextColor(PictureSelectionConfig.style.pictureOriginalFontColor);
                } else {
                    mCbOriginal.setTextColor(ContextCompat.getColor(this, R.color.picture_color_53575e));
                }
                if (PictureSelectionConfig.style.pictureOriginalTextSize != 0) {
                    mCbOriginal.setTextSize(PictureSelectionConfig.style.pictureOriginalTextSize);
                }
            } else {
                mCbOriginal.setButtonDrawable(ContextCompat.getDrawable(this, R.drawable.picture_original_checkbox));
                mCbOriginal.setTextColor(ContextCompat.getColor(this, R.color.picture_color_53575e));
            }
        } else {
            Drawable pictureCheckedStyle = AttrsUtils.getTypeValueDrawable(getContext(), R.attr.picture_checked_style, R.drawable.picture_checkbox_selector);
            check.setBackground(pictureCheckedStyle);
            ColorStateList completeColorStateList = AttrsUtils.getTypeValueColorStateList(getContext(), R.attr.picture_ac_preview_complete_textColor);
            if (completeColorStateList != null) {
                mTvPictureOk.setTextColor(completeColorStateList);
            }
//            Drawable leftDrawable = AttrsUtils.getTypeValueDrawable(getContext(), R.attr.picture_preview_leftBack_icon, R.drawable.picture_icon_back);
//            pictureLeftBack.setImageDrawable(leftDrawable);

            Drawable ovalBgDrawable = AttrsUtils.getTypeValueDrawable(getContext(), R.attr.picture_num_style, R.drawable.picture_num_oval);
            tvMediaNum.setBackground(ovalBgDrawable);

            int previewBottomBgColor = AttrsUtils.getTypeValueColor(getContext(), R.attr.picture_ac_preview_bottom_bg);
            if (previewBottomBgColor != 0) {
                selectBarLayout.setBackgroundColor(previewBottomBgColor);
            }
            int titleBarHeight = AttrsUtils.getTypeValueSizeForInt(getContext(), R.attr.picture_titleBar_height);
            if (titleBarHeight > 0) {
                ViewGroup.LayoutParams params = mTitleBar.getLayoutParams();
                params.height = titleBarHeight;
            }
            if (config.isOriginalControl) {
                Drawable originalDrawable = AttrsUtils.getTypeValueDrawable(getContext(), R.attr.picture_original_check_style, R.drawable.picture_original_wechat_checkbox);
                mCbOriginal.setButtonDrawable(originalDrawable);
                int originalTextColor = AttrsUtils.getTypeValueColor(getContext(), R.attr.picture_original_text_color);
                if (originalTextColor != 0) {
                    mCbOriginal.setTextColor(originalTextColor);
                }
            }
        }
        mTitleBar.setBackgroundColor(colorPrimary);
        onSelectNumChange(false);
    }

    /**
     * 这里没实际意义，好处是预览图片时 滑动到屏幕一半以上可看到下一张图片是否选中了
     *
     * @param previewEggs          是否显示预览友好体验
     * @param positionOffsetPixels 滑动偏移量
     */
    private void isPreviewEggs(boolean previewEggs, int position, int positionOffsetPixels) {
        if (previewEggs) {
            if (adapter.getSize() > 0) {
                LocalMedia media;
                int num;
                if (positionOffsetPixels < screenWidth / 2) {
                    media = adapter.getItem(position);
                    if (media != null) {
                        check.setSelected(isSelected(media));
                        if (config.isWeChatStyle) {
                            onUpdateSelectedChange(media);
                        } else {
                            if (config.checkNumMode) {
                                num = media.getNum();
                                check.setText(ValueOf.toString(num));
                                notifyCheckChanged(media);
                                onImageChecked(position);
                            }
                        }
                    }
                } else {
                    media = adapter.getItem(position + 1);
                    if (media != null) {
                        check.setSelected(isSelected(media));
                        if (config.isWeChatStyle) {
                            onUpdateSelectedChange(media);
                        } else {
                            if (config.checkNumMode) {
                                num = media.getNum();
                                check.setText(ValueOf.toString(num));
                                notifyCheckChanged(media);
                                onImageChecked(position + 1);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 初始化ViewPage数据
     *
     * @param list
     */
    private void initViewPageAdapterData(List<LocalMedia> list) {
        adapter = new PictureSimpleFragmentAdapter(config, this);
        adapter.bindData(list);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        setTitle();
        onImageChecked(position);
        LocalMedia media = adapter.getItem(position);
        if (media != null) {
            index = media.getPosition();
            if (config.checkNumMode) {
                tvMediaNum.setSelected(true);
                check.setText(ValueOf.toString(media.getNum()));
                notifyCheckChanged(media);
            }
        }
    }

    /**
     * 重置标题栏和分页码
     */
    private void setNewTitle() {
        mPage = 0;
        position = 0;
        setTitle();
    }

    /**
     * 设置标题
     */
    private void setTitle() {
        if (config.isPageStrategy && !isBottomPreview) {
            tvTitle.setText(getString(R.string.picture_preview_image_num,
                    position + 1, totalNumber));
        } else {
            tvTitle.setText(getString(R.string.picture_preview_image_num,
                    position + 1, adapter.getSize()));
        }
    }

    /**
     * 选择按钮更新
     */
    private void notifyCheckChanged(LocalMedia imageBean) {
        if (config.checkNumMode) {
            check.setText("");
            int size = selectData.size();
            for (int i = 0; i < size; i++) {
                LocalMedia media = selectData.get(i);
                if (media.getPath().equals(imageBean.getPath())
                        || media.getId() == imageBean.getId()) {
                    imageBean.setNum(media.getNum());
                    check.setText(String.valueOf(imageBean.getNum()));
                }
            }
        }
    }

    /**
     * 更新选择的顺序
     */
    private void subSelectPosition() {
        for (int index = 0, len = selectData.size(); index < len; index++) {
            LocalMedia media = selectData.get(index);
            media.setNum(index + 1);
        }
    }

    /**
     * 判断当前图片是否选中
     *
     * @param position
     */
    public void onImageChecked(int position) {
        if (adapter.getSize() > 0) {
            LocalMedia media = adapter.getItem(position);
            if (media != null) {
                check.setSelected(isSelected(media));
            }
        } else {
            check.setSelected(false);
        }
    }

    /**
     * 当前图片是否选中
     *
     * @param image
     * @return
     */
    protected boolean isSelected(LocalMedia image) {
        int size = selectData.size();
        for (int i = 0; i < size; i++) {
            LocalMedia media = selectData.get(i);
            if (media.getPath().equals(image.getPath()) || media.getId() == image.getId()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 更新图片选择数量
     */

    protected void onSelectNumChange(boolean isRefresh) {
        this.refresh = isRefresh;
        boolean enable = selectData != null && selectData.size() != 0;
        if (enable) {
            mTvPictureOk.setEnabled(true);
            mTvPictureOk.setSelected(true);
            if (PictureSelectionConfig.style != null) {
                if (PictureSelectionConfig.style.pictureCompleteTextColor != 0) {
                    mTvPictureOk.setTextColor(PictureSelectionConfig.style.pictureCompleteTextColor);
                } else {
                    mTvPictureOk.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_fa632d));
                }
            }
            if (numComplete) {
                initCompleteText(selectData.size());
            } else {
                if (refresh) {
                    tvMediaNum.startAnimation(animation);
                }
                tvMediaNum.setVisibility(View.VISIBLE);
                tvMediaNum.setText(String.valueOf(selectData.size()));
                if (PictureSelectionConfig.uiStyle != null) {
                    if (!TextUtils.isEmpty(PictureSelectionConfig.uiStyle.picture_bottom_completeNormalText)) {
                        mTvPictureOk.setText(PictureSelectionConfig.uiStyle.picture_bottom_completeNormalText);
                    }
                } else if (PictureSelectionConfig.style != null) {
                    if (!TextUtils.isEmpty(PictureSelectionConfig.style.pictureCompleteText)) {
                        mTvPictureOk.setText(PictureSelectionConfig.style.pictureCompleteText);
                    }
                } else {
                    mTvPictureOk.setText(getString(R.string.picture_completed));
                }
            }
        } else {
            mTvPictureOk.setEnabled(false);
            mTvPictureOk.setSelected(false);
            if (PictureSelectionConfig.style != null) {
                if (PictureSelectionConfig.style.pictureUnCompleteTextColor != 0) {
                    mTvPictureOk.setTextColor(PictureSelectionConfig.style.pictureUnCompleteTextColor);
                } else {
                    mTvPictureOk.setTextColor(ContextCompat.getColor(getContext(), R.color.picture_color_9b));
                }
            }
            if (numComplete) {
                initCompleteText(0);
            } else {
                tvMediaNum.setVisibility(View.INVISIBLE);
                if (PictureSelectionConfig.uiStyle != null) {
                    if (!TextUtils.isEmpty(PictureSelectionConfig.uiStyle.picture_bottom_completeDefaultText)) {
                        mTvPictureOk.setText(PictureSelectionConfig.uiStyle.picture_bottom_completeDefaultText);
                    }
                } else if (PictureSelectionConfig.style != null) {
                    if (!TextUtils.isEmpty(PictureSelectionConfig.style.pictureUnCompleteText)) {
                        mTvPictureOk.setText(PictureSelectionConfig.style.pictureUnCompleteText);
                    }
                } else {
                    mTvPictureOk.setText(getString(R.string.picture_please_select));
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.pictureLeftBack) {
            onBackPressed();
        } else if (id == R.id.picture_tv_ok || id == R.id.tv_media_num) {
            onComplete();
        } else if (id == R.id.btnCheck) {
            onCheckedComplete();
        }
    }

    @SuppressLint("StringFormatMatches")
    protected void onCheckedComplete() {
        if (adapter.getSize() > 0) {
            LocalMedia image = adapter.getItem(viewPager.getCurrentItem());
            // If the original path does not exist or the path does exist but the file does not exist
            String newPath = image.getRealPath();
            if (!TextUtils.isEmpty(newPath) && !new File(newPath).exists()) {
                ToastUtils.s(getContext(), PictureMimeType.s(getContext(), image.getMimeType()));
                return;
            }
            String mimeType = selectData.size() > 0 ?
                    selectData.get(0).getMimeType() : "";
            int currentSize = selectData.size();
            if (config.isWithVideoImage) {
                // 混选模式
                int videoSize = 0;
                for (int i = 0; i < currentSize; i++) {
                    LocalMedia media = selectData.get(i);
                    if (PictureMimeType.isHasVideo(media.getMimeType())) {
                        videoSize++;
                    }
                }
                if (PictureMimeType.isHasVideo(image.getMimeType())) {
                    if (config.maxVideoSelectNum <= 0) {
                        // 如果视频可选数量是0
                        showPromptDialog(getString(R.string.picture_rule));
                        return;
                    }

                    if (currentSize >= config.maxSelectNum && !check.isSelected()) {
                        showPromptDialog(getString(R.string.picture_message_max_num, config.maxSelectNum));
                        showPromptDialog(getString(R.string.picture_message_max_num, config.maxSelectNum));
                        return;
                    }

                    if (videoSize >= config.maxVideoSelectNum && !check.isSelected()) {
                        // 如果选择的是视频
                        showPromptDialog(StringUtils.getMsg(getContext(), image.getMimeType(), config.maxVideoSelectNum));
                        return;
                    }

                    if (!check.isSelected() && config.videoMinSecond > 0 && image.getDuration() < config.videoMinSecond) {
                        // 视频小于最低指定的长度
                        showPromptDialog(getContext().getString(R.string.picture_choose_min_seconds, config.videoMinSecond / 1000));
                        return;
                    }

                    if (!check.isSelected() && config.videoMaxSecond > 0 && image.getDuration() > config.videoMaxSecond) {
                        // 视频时长超过了指定的长度
                        showPromptDialog(getContext().getString(R.string.picture_choose_max_seconds, config.videoMaxSecond / 1000));
                        return;
                    }
                } else {
                    if (currentSize >= config.maxSelectNum && !check.isSelected()) {
                        showPromptDialog(getString(R.string.picture_message_max_num, config.maxSelectNum));
                        return;
                    }
                }
            } else {
                // 非混选模式
                if (!TextUtils.isEmpty(mimeType)) {
                    boolean mimeTypeSame = PictureMimeType.isMimeTypeSame(mimeType, image.getMimeType());
                    if (!mimeTypeSame) {
                        showPromptDialog(getString(R.string.picture_rule));
                        return;
                    }
                }
                if (PictureMimeType.isHasVideo(mimeType) && config.maxVideoSelectNum > 0) {
                    if (currentSize >= config.maxVideoSelectNum && !check.isSelected()) {
                        // 如果先选择的是视频
                        showPromptDialog(StringUtils.getMsg(getContext(), mimeType, config.maxVideoSelectNum));
                        return;
                    }

                    if (!check.isSelected() && config.videoMinSecond > 0 && image.getDuration() < config.videoMinSecond) {
                        // 视频小于最低指定的长度
                        showPromptDialog(getContext().getString(R.string.picture_choose_min_seconds, config.videoMinSecond / 1000));
                        return;
                    }

                    if (!check.isSelected() && config.videoMaxSecond > 0 && image.getDuration() > config.videoMaxSecond) {
                        // 视频时长超过了指定的长度
                        showPromptDialog(getContext().getString(R.string.picture_choose_max_seconds, config.videoMaxSecond / 1000));
                        return;
                    }
                } else {
                    if (currentSize >= config.maxSelectNum && !check.isSelected()) {
                        showPromptDialog(StringUtils.getMsg(getContext(), mimeType, config.maxSelectNum));
                        return;
                    }
                    if (PictureMimeType.isHasVideo(image.getMimeType())) {
                        if (!check.isSelected() && config.videoMinSecond > 0 && image.getDuration() < config.videoMinSecond) {
                            // 视频小于最低指定的长度
                            showPromptDialog(getContext().getString(R.string.picture_choose_min_seconds, config.videoMinSecond / 1000));
                            return;
                        }

                        if (!check.isSelected() && config.videoMaxSecond > 0 && image.getDuration() > config.videoMaxSecond) {
                            // 视频时长超过了指定的长度
                            showPromptDialog(getContext().getString(R.string.picture_choose_max_seconds, config.videoMaxSecond / 1000));
                            return;
                        }
                    }
                }
            }
            // 刷新图片列表中图片状态
            boolean isChecked;
            if (!check.isSelected()) {
                isChecked = true;
                check.setSelected(true);
                check.startAnimation(animation);
            } else {
                isChecked = false;
                check.setSelected(false);
            }
            isChangeSelectedData = true;
            if (isChecked) {
                VoiceUtils.getInstance().play();
                // 如果是单选，则清空已选中的并刷新列表(作单一选择)
                if (config.selectionMode == Config.SINGLE) {
                    selectData.clear();
                }

                // 如果宽高为0，重新获取宽高
                if (image.getWidth() == 0 || image.getHeight() == 0) {
                    image.setOrientation(-1);
                    if (PictureMimeType.isContent(image.getPath())) {
                        if (PictureMimeType.isHasVideo(image.getMimeType())) {
                            MediaUtils.getVideoSizeForUri(getContext(), Uri.parse(image.getPath()), image);
                        } else if (PictureMimeType.isHasImage(image.getMimeType())) {
                            int[] size = MediaUtils.getImageSizeForUri(getContext(), Uri.parse(image.getPath()));
                            image.setWidth(size[0]);
                            image.setHeight(size[1]);
                        }
                    } else {
                        if (PictureMimeType.isHasVideo(image.getMimeType())) {
                            int[] size = MediaUtils.getVideoSizeForUrl(image.getPath());
                            image.setWidth(size[0]);
                            image.setHeight(size[1]);
                        } else if (PictureMimeType.isHasImage(image.getMimeType())) {
                            int[] size = MediaUtils.getImageSizeForUrl(image.getPath());
                            image.setWidth(size[0]);
                            image.setHeight(size[1]);
                        }
                    }
                }

                // 如果有旋转信息图片宽高则是相反
                MediaUtils.setOrientationAsynchronous(getContext(), image, config.isAndroidQChangeWH, config.isAndroidQChangeVideoWH, null);
                selectData.add(image);
                onSelectedChange(true, image);
                image.setNum(selectData.size());
                if (config.checkNumMode) {
                    check.setText(String.valueOf(image.getNum()));
                }
            } else {
                int size = selectData.size();
                for (int i = 0; i < size; i++) {
                    LocalMedia media = selectData.get(i);
                    if (media.getPath().equals(image.getPath())
                            || media.getId() == image.getId()) {
                        selectData.remove(media);
                        onSelectedChange(false, image);
                        subSelectPosition();
                        notifyCheckChanged(media);
                        break;
                    }
                }
            }
            onSelectNumChange(true);
        }
    }

    /**
     * 选中或是移除
     *
     * @param isAddRemove
     * @param media
     */
    protected void onSelectedChange(boolean isAddRemove, LocalMedia media) {

    }

    /**
     * 更新选中或是移除状态
     *
     * @param media
     */
    protected void onUpdateSelectedChange(LocalMedia media) {

    }

    @SuppressLint("StringFormatMatches")
    protected void onComplete() {
        // 如果设置了图片最小选择数量，则判断是否满足条件
        int size = selectData.size();
        LocalMedia image = selectData.size() > 0 ? selectData.get(0) : null;
        String mimeType = image != null ? image.getMimeType() : "";
        if (config.isWithVideoImage) {
            // 混选模式
            int videoSize = 0;
            int imageSize = 0;
            int currentSize = selectData.size();
            for (int i = 0; i < currentSize; i++) {
                LocalMedia media = selectData.get(i);
                if (PictureMimeType.isHasVideo(media.getMimeType())) {
                    videoSize++;
                } else {
                    imageSize++;
                }
            }
            if (config.selectionMode == Config.MULTIPLE) {
                if (config.minSelectNum > 0) {
                    if (imageSize < config.minSelectNum) {
                        showPromptDialog(getString(R.string.picture_min_img_num, config.minSelectNum));
                        return;
                    }
                }
                if (config.minVideoSelectNum > 0) {
                    if (videoSize < config.minVideoSelectNum) {
                        showPromptDialog(getString(R.string.picture_min_video_num, config.minVideoSelectNum));
                        return;
                    }
                }
            }
        } else {
            // 单选模式(同类型)
            if (config.selectionMode == Config.MULTIPLE) {
                if (PictureMimeType.isHasImage(mimeType) && config.minSelectNum > 0 && size < config.minSelectNum) {
                    String str = getString(R.string.picture_min_img_num, config.minSelectNum);
                    showPromptDialog(str);
                    return;
                }
                if (PictureMimeType.isHasVideo(mimeType) && config.minVideoSelectNum > 0 && size < config.minVideoSelectNum) {
                    String str = getString(R.string.picture_min_video_num, config.minVideoSelectNum);
                    showPromptDialog(str);
                    return;
                }
            }
        }
        isCompleteOrSelected = true;
        isChangeSelectedData = true;

        onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Config.REQUEST_CROP) {
                Uri uri = data.getData();
                if (uri != null) {
                    LocalMedia media1 = adapter.getItem(viewPager.getCurrentItem());
                    if (isInSelectList(media1)) {
                        LocalMedia media2 = selectData.get(viewPager.getCurrentItem());
                        media1.setCut(true);
                        media1.setCutPath(uri.getPath());
                        media2.setCut(true);
                        media2.setCutPath(uri.getPath());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            //&& !Environment.isExternalStorageLegacy()
                            media1.setAndroidQToPath(uri.getPath());
                            media2.setAndroidQToPath(uri.getPath());
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        media1.setCut(true);
                        media1.setCutPath(uri.getPath());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            //&& !Environment.isExternalStorageLegacy()
                            media1.setAndroidQToPath(uri.getPath());
                        }
                        adapter.notifyDataSetChanged();
                        onCheckedComplete();
                    }
                }
            }

        }
    }

    private boolean isInSelectList(LocalMedia media) {
        if (selectData != null && !selectData.isEmpty()) {
            for (LocalMedia m : selectData) {
                if (media.getId() == m.getId()) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        updateResult();
        finish();
        //overridePendingTransition(0, PictureSelectionConfig.windowAnimationStyle.activityPreviewExitAnimation);

    }

    /**
     * 更新选中数据
     */
    private void updateResult() {
        Intent intent = new Intent();
        if (isChangeSelectedData) {
            intent.putExtra(Config.EXTRA_COMPLETE_SELECTED, isCompleteOrSelected);
            intent.putParcelableArrayListExtra(Config.EXTRA_SELECT_LIST,
                    (ArrayList<? extends Parcelable>) selectData);
        }
        // 把是否原图标识返回，主要用于开启了开发者选项不保留活动或内存不足时 原图选中状态没有全局同步问题
        if (config.isOriginalControl) {
            intent.putExtra(Config.EXTRA_CHANGE_ORIGINAL, config.isCheckOriginalImage);
        }
        setResult(RESULT_CANCELED, intent);
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Config.EXTRA_COMPLETE_SELECTED, isCompleteOrSelected);
        outState.putBoolean(Config.EXTRA_CHANGE_SELECTED_DATA, isChangeSelectedData);
        com.github.hueyra.mediax.PictureSelector.saveSelectorList(outState, selectData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isOnSaveInstanceState) {
            ImagesObservable.getInstance().clearPreviewMediaData();
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (animation != null) {
            animation.cancel();
            animation = null;
        }
        if (adapter != null) {
            adapter.clear();
        }
    }

    @Override
    public void onActivityBackPressed() {
        //onBackPressed();
    }
}
