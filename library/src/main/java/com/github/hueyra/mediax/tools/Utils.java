package com.github.hueyra.mediax.tools;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;


import com.github.hueyra.mediax.app.Config;

import java.io.File;

public class Utils {

    private static long lastClickTime;
    private final static long TIME = 800;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < TIME) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static int getRecordState() {
        int minBuffer = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat
                .ENCODING_PCM_16BIT);
        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, 44100, AudioFormat
                .CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, (minBuffer * 100));
        short[] point = new short[minBuffer];
        int readSize = 0;
        try {
            audioRecord.startRecording();//检测是否可以进入初始化状态
        } catch (Exception e) {
            if (audioRecord != null) {
                audioRecord.release();
                audioRecord = null;
            }
            return Config.CHECK_PERMISSION_STATE_NO_PERMISSION;
        }
        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            //6.0以下机型都会返回此状态，故使用时需要判断bulid版本
            //检测是否在录音中
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
            }
            return Config.CHECK_PERMISSION_STATE_RECORDING;
        } else {
            //检测是否可以获取录音结果
            readSize = audioRecord.read(point, 0, point.length);

            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
            }
            if (readSize <= 0) {
                return Config.CHECK_PERMISSION_STATE_NO_PERMISSION;
            } else {
                return Config.CHECK_PERMISSION_STATE_SUCCESS;
            }
        }
    }

    public static void createMediaDirs(Context context) {
        File file = context.getExternalFilesDir(Config.EXTERNAL_FILES_DIR_NAME);
        if (file != null && !file.exists()) {
            file.mkdirs();
        }
    }

}
