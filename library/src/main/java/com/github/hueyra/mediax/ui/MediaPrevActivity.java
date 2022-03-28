package com.github.hueyra.mediax.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.github.hueyra.mediax.R;
import com.github.hueyra.mediax.adapter.PicturePrevDotAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhujun
 * Date : 2022-01-15
 * Desc : _
 */
public class MediaPrevActivity extends AppCompatActivity {

    private ViewPager mVpPager;
    private RecyclerView mRvDots;
    private PicturePrevDotAdapter mAdapter;

    private List<Boolean> mMovePositionList;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            setupWindow();
        }
    }

    protected void setupWindow() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);
        setContentView(R.layout.activity_media_prev);
        mVpPager = findViewById(R.id.vp_pager);
        mRvDots = findViewById(R.id.rv_dots);
        List<String> list = getIntent().getStringArrayListExtra("list");
        int position = getIntent().getIntExtra("position", 0);
        setupVp(list, position);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(
                R.anim.mx_anim_fade_in,
                R.anim.mx_anim_exit
        );
    }

    private void setupVp(List<String> list, int position) {

        if (list != null && list.size() > 0) {

            mMovePositionList = new ArrayList<>();
            List<Fragment> fragmentList = new ArrayList<>();

            for (int i = 0; i < list.size(); i++) {
                mMovePositionList.add(i == position);
                fragmentList.add(new ImagePrevFragment(list.get(i)));
            }

            mAdapter = new PicturePrevDotAdapter(mMovePositionList);

            mRvDots.setLayoutManager(new GridLayoutManager(this, mMovePositionList.size()));
            mRvDots.setAdapter(mAdapter);
            mRvDots.setVisibility((mMovePositionList.size() > 1) ? View.VISIBLE : View.GONE);

            mVpPager.setOffscreenPageLimit(mMovePositionList.size() - 1);
            mVpPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
                @Override
                public int getCount() {
                    return fragmentList.size();
                }

                @NonNull
                @Override
                public Fragment getItem(int position) {
                    return fragmentList.get(position);
                }
            });

            mVpPager.setCurrentItem(position);

            mVpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onPageSelected(int position) {
                    for (int i = 0; i < mMovePositionList.size(); i++) {
                        mMovePositionList.set(i, i == position);
                    }
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        }

    }

}
