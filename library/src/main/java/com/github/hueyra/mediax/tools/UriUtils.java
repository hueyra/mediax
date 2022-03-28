package com.github.hueyra.mediax.tools;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.github.hueyra.mediax.app.Config;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@SuppressLint("SimpleDateFormat")
public class UriUtils {
    /**
     * 根据Uri返回文件绝对路径
     * 兼容了file:///开头的 和 content://开头的情况
     */
    public static String getRealFilePathFromUri(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public static String getImgOrientationFromUri(final Context context, final Uri uri) {
        String data = "0";
        if (null == uri) return data;
        final String scheme = uri.getScheme();
        if (scheme == null) {
            return data;
        } else if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(scheme)) {
            return data;
        } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int oi = -1;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        oi = cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION);
                    }
                    if (oi > -1) {
                        String temp = cursor.getString(oi);
                        if (!TextUtils.isEmpty(temp)) {
                            data = temp;
                        }
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public static void createMediaDirs(Context context) {
        try {
            File file1 = new File(context.getExternalFilesDir(null).getAbsolutePath(), Config.STORAGE_PATH_IMAGE_NAME);
            File file2 = new File(context.getExternalFilesDir(null).getAbsolutePath(), Config.STORAGE_PATH_VIDEO_NAME);
            //
            if (!file1.exists()) {
                file1.mkdirs();
            }
            if (!file2.exists()) {
                file2.mkdirs();
            }
        } catch (Exception w) {
            //
        }
    }

    public static String getSimpleImageCropName(Context context) {
        return context.getExternalFilesDir(null).getAbsolutePath() + File.separator
                + Config.EXTERNAL_FILES_DIR_NAME + File.separator
                + Config.FILE_NAME_IMAGE_CROP_PREFIX
                + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime())
                + Config.FILE_SAVE_TYPE_IMAGE;
    }

    public static String getSimpleImageName(Context context) {
        return context.getExternalFilesDir(null).getAbsolutePath() + File.separator
                + Config.EXTERNAL_FILES_DIR_NAME + File.separator
                + Config.FILE_NAME_IMAGE_PREFIX
                + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime())
                + Config.FILE_SAVE_TYPE_IMAGE;
    }

    public static String getSimpleVideoName(Context context) {
        return context.getExternalFilesDir(null).getAbsolutePath() + File.separator
                + Config.EXTERNAL_FILES_DIR_NAME + File.separator
                + Config.FILE_NAME_VIDEO_PREFIX
                + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime())
                + Config.FILE_SAVE_TYPE_VIDEO;
    }

}
