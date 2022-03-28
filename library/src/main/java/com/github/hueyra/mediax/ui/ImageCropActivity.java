package com.github.hueyra.mediax.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.hueyra.mediax.R;
import com.github.hueyra.mediax.imgcrop.CropViewLayout;
import com.github.hueyra.mediax.tools.UriUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by zhujun.
 * Date: 12/18/20
 * Info: __
 */
public class ImageCropActivity extends AppCompatActivity {

    private CropViewLayout mCropViewLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imge_crop);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);
        mCropViewLayout = findViewById(R.id.ica_cvl_crop);
        findViewById(R.id.ica_tv_cancel).setOnClickListener(v -> finish());
        findViewById(R.id.ica_tv_ok).setOnClickListener(v -> generateUriAndReturn());
        mCropViewLayout.setImageSrc(getIntent().getData());
    }

    /**
     * 生成Uri并且通过setResult返回给打开的activity
     */
    private void generateUriAndReturn() {
        //调用返回剪切图 u
        Bitmap zoomedCropBitmap = mCropViewLayout.crop();
        if (zoomedCropBitmap == null) {
            //Log.e("android", "zoomedCropBitmap == null");
            return;
        }
        String saveToPath = UriUtils.getSimpleImageCropName(this);
        OutputStream outputStream = null;
        try {
            File f = new File(saveToPath);
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }
            if (!f.exists()) {
                f.createNewFile();
            }
            outputStream = new FileOutputStream(f);
            zoomedCropBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        } catch (IOException ex) {
            // Log.e("android", "Cannot open file: " + saveToPath, ex);
            ex.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Intent intent = new Intent();
        intent.putExtra("path", saveToPath);
        setResult(RESULT_OK, intent);
        finish();
    }
}
