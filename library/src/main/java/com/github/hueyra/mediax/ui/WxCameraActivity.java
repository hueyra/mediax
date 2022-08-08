package com.github.hueyra.mediax.ui;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.FocusMeteringResult;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.core.Preview;
import androidx.camera.core.SurfaceOrientedMeteringPointFactory;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.hueyra.mediax.R;
import com.github.hueyra.mediax.app.Config;
import com.github.hueyra.mediax.entity.LocalMedia;
import com.github.hueyra.mediax.listener.CaptureListener;
import com.github.hueyra.mediax.listener.TypeListener;
import com.github.hueyra.mediax.tools.UriUtils;
import com.github.hueyra.mediax.tools.Utils;
import com.github.hueyra.mediax.widget.CaptureLayout;
import com.github.hueyra.mediax.widget.FocusView;
import com.github.hueyra.mediax.widget.MediaPrevView;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WxCameraActivity extends AppCompatActivity {

    private static final String TAG = "WxCameraActivity";

    private int mWindowWidth;
    private int mCurrentLensFacing;
    private ImageCapture mImageCapture;
    private VideoCapture mVideoCapture;
    private ExecutorService mExecutorService;
    private ProcessCameraProvider mProcessCameraProvider;
    private ListenableFuture<ProcessCameraProvider> mCameraProviderFuture;

    private FocusView mFocusView;
    private ImageView mCameraSwitch;
    private PreviewView mPreviewView;
    private TextView mCameraErrorInfo;
    private CaptureLayout mCaptureLayout;
    private MediaPrevView mMediaPrevView;

    //当前cameraX的use case mode
    private int mCurrentUseCaseMode;

    //用户需要的camera类别
    private int mUserOpenCameraType;

    private boolean mImageNeedCrop;

    private Uri mImageSaveUri;

    private Uri mVideoSaveUri;

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

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(Color.BLACK);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_camera);
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        mWindowWidth = metric.widthPixels;
        //
        mPreviewView = findViewById(R.id.pv_preview);
        mCameraSwitch = findViewById(R.id.iv_switch);
        mMediaPrevView = findViewById(R.id.mpv_prev);
        mCameraErrorInfo = findViewById(R.id.tv_error);
        mCaptureLayout = findViewById(R.id.cl_capture);
        mFocusView = findViewById(R.id.fv_focus);
        //
        mCameraSwitch.setOnClickListener(v -> switchCamera());
        mUserOpenCameraType = getIntent().getIntExtra("type", Config.BUTTON_STATE_BOTH);
        mImageNeedCrop = getIntent().getBooleanExtra("crop", false);
        //
        Utils.createMediaDirs(this);
        //
        setupCameraX();
        setupCaptureLayout();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.mx_anim_fade_in, R.anim.mx_anim_down_out);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaPrevView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaPrevView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPrevView.stopPrev();
        if (mProcessCameraProvider != null) {
            mProcessCameraProvider.unbindAll();
        }
        if (mExecutorService != null) {
            mExecutorService.shutdown();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Config.REQUEST_CROP) {
            if (data != null) {
                setResultCrop(data);
            } else {
                setResult();
            }
        }
    }

    private void setResultCrop(Intent data) {
        Uri resultUri = Uri.parse(data.getStringExtra("path"));
        if (resultUri == null) {
            return;
        }
        String cutPath = resultUri.getPath();
        LocalMedia localMedia = new LocalMedia();
        localMedia.setRealPath(mImageSaveUri.getPath());
        localMedia.setMimeType(Config.MIME_TYPE_JPG);
        localMedia.setCutPath(cutPath);
        exit(localMedia);
    }

    private void setResult() {
        if (mImageSaveUri != null) {
            LocalMedia localMedia = new LocalMedia();
            localMedia.setRealPath(mImageSaveUri.getPath());
            localMedia.setMimeType(Config.MIME_TYPE_JPG);
            exit(localMedia);
        } else if (mVideoSaveUri != null) {
            LocalMedia localMedia = new LocalMedia();
            localMedia.setRealPath(mVideoSaveUri.getPath());
            localMedia.setMimeType(Config.MIME_TYPE_MP4);
            exit(localMedia);
        }

    }

    private void exit(LocalMedia result) {
        Intent intent = new Intent();
        intent.putExtra(Config.EXTRA_RESULT_ENTITY, result);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setupCameraX() {
        mExecutorService = Executors.newSingleThreadExecutor();
        mCameraProviderFuture = ProcessCameraProvider.getInstance(this);
        mCameraProviderFuture.addListener(() -> {
            try {
                mProcessCameraProvider = mCameraProviderFuture.get();
                if (hasBackCamera()) {
                    mCurrentLensFacing = CameraSelector.LENS_FACING_BACK;
                } else {
                    if (hasFrontCamera()) {
                        mCurrentLensFacing = CameraSelector.LENS_FACING_FRONT;
                    } else {
                        return;
                    }
                }
                bindCameraUseCases();
            } catch (Exception e) {
                Log.d(TAG, "setupCameraX Exception -> " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @SuppressLint("RestrictedApi")
    private void setupCaptureLayout() {
        mCaptureLayout.setButtonFeatures(mUserOpenCameraType);
        mCaptureLayout.setLeftClickListener(this::finish);
        mCaptureLayout.setTypeListener(new TypeListener() {
            @Override
            public void cancel() {
                mImageSaveUri = null;
                mVideoSaveUri = null;
                mMediaPrevView.stopPrev();
                mCaptureLayout.resetCaptureLayout();
            }

            @Override
            public void confirm() {
                if (mImageNeedCrop && mImageSaveUri != null) {
                    Intent intent = new Intent(WxCameraActivity.this, ImageCropActivity.class);
                    intent.setData(mImageSaveUri);
                    startActivityForResult(intent, Config.REQUEST_CROP);
                } else {
                    setResult();
                }
            }
        });
        mCaptureLayout.setCaptureListener(new CaptureListener() {
            @Override
            public void takePictures() {
                if (mCurrentUseCaseMode == Config.BUTTON_STATE_ONLY_RECORDER) {
                    //当前绑定的是video mode
                    mCurrentUseCaseMode = Config.BUTTON_STATE_ONLY_CAPTURE;
                    bindCameraUseCases();
                }

                final File file = new File(UriUtils.getSimpleImageName(WxCameraActivity.this));

                ImageCapture.Metadata metadata = new ImageCapture.Metadata();
                metadata.setReversedHorizontal(mCurrentLensFacing == CameraSelector.LENS_FACING_FRONT);
                ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).setMetadata(metadata).build();
                //
                mImageCapture.takePicture(outputFileOptions, mExecutorService, new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        runOnUiThread(() -> {
                            mImageSaveUri = Uri.parse(file.getAbsolutePath());
                            mCaptureLayout.startTypeBtnAnimator();
                            mMediaPrevView.startPrevImage(mImageSaveUri);
                        });
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                    }
                });
            }

            @Override
            public void recordShort(long time) {

            }

            @Override
            public void recordStart() {
                if (mCurrentUseCaseMode == Config.BUTTON_STATE_ONLY_CAPTURE) {
                    //当前绑定的是video mode
                    mCurrentUseCaseMode = Config.BUTTON_STATE_ONLY_RECORDER;
                    bindCameraUseCases();
                }
                //
                String switchViewRotation = "0";
                if (mCameraSwitch.getTag() != null) {
                    switchViewRotation = mCameraSwitch.getTag().toString();
                }
                final boolean isHorizontal = switchViewRotation.equals("90") || switchViewRotation.equals("270");

                final File file = new File(UriUtils.getSimpleVideoName(WxCameraActivity.this));

                VideoCapture.OutputFileOptions outputFileOptions = new VideoCapture.OutputFileOptions.Builder(file).build();
                if (ActivityCompat.checkSelfPermission(WxCameraActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mVideoCapture.startRecording(outputFileOptions, mExecutorService, new VideoCapture.OnVideoSavedCallback() {
                    @Override
                    public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
                        runOnUiThread(() -> {
                            mVideoSaveUri = Uri.parse(file.getAbsolutePath());
                            mMediaPrevView.startPrevVideo(mVideoSaveUri, isHorizontal);
                        });
                    }

                    @Override
                    public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                    }
                });
            }

            @Override
            public void recordEnd(long time) {
                mVideoCapture.stopRecording();
            }

            @Override
            public void recordZoom(float zoom) {

            }

            @Override
            public void recordError() {

            }
        });

    }

    @SuppressLint({"RestrictedApi", "ClickableViewAccessibility"})
    private void bindCameraUseCases() {

        Preview preview = new Preview.Builder()
                //.setTargetAspectRatio()//设置宽高比
                //.setTargetRotation(mPreviewView.getDisplay().getRotation())//设置当前屏幕的旋转
                .build();

        mImageCapture = new ImageCapture.Builder()
                //优化捕获速度，可能降低图片质量
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                //.setTargetRotation(mPreviewView.getDisplay().getRotation())
                .build();
        mVideoCapture = new VideoCapture.Builder()
                //.setTargetRotation(mPreviewView.getDisplay().getRotation())
                .build();

        OrientationEventListener orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                int rotation;
                int switch_view_rotation;
                // Monitors orientation values to determine the target rotation value
                if (orientation >= 45 && orientation < 135) {
                    rotation = Surface.ROTATION_270;
                    switch_view_rotation = 270;  //横
                } else if (orientation >= 135 && orientation < 225) {
                    rotation = Surface.ROTATION_180;
                    switch_view_rotation = 180;  //竖
                } else if (orientation >= 225 && orientation < 315) {
                    rotation = Surface.ROTATION_90;
                    switch_view_rotation = 90;  //横
                } else {
                    rotation = Surface.ROTATION_0;
                    switch_view_rotation = 0;   //竖
                }

                if (!(mCameraSwitch.getTag() != null && Integer.parseInt(mCameraSwitch.getTag().toString()) == switch_view_rotation)) {
                    runOnUiThread(() -> {
                        mCameraSwitch.animate().rotation(switch_view_rotation).setDuration(200).start();
                        mCameraSwitch.setTag(String.valueOf(switch_view_rotation));
                        //Log.d(TAG, "switch_view_rotation -> " + switch_view_rotation);
                    });
                }
                mImageCapture.setTargetRotation(rotation);
                mVideoCapture.setTargetRotation(rotation);
            }
        };
        orientationEventListener.enable();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(mCurrentLensFacing)
                .build();

        mProcessCameraProvider.unbindAll();

        try {
            Camera camera;
            if (mCurrentUseCaseMode == Config.BUTTON_STATE_BOTH) {
                camera = mProcessCameraProvider.bindToLifecycle(this, cameraSelector, preview, mImageCapture, mVideoCapture);
            } else if (mCurrentUseCaseMode == Config.BUTTON_STATE_ONLY_CAPTURE) {
                camera = mProcessCameraProvider.bindToLifecycle(this, cameraSelector, preview, mImageCapture);
            } else {
                camera = mProcessCameraProvider.bindToLifecycle(this, cameraSelector, preview, mVideoCapture);
            }
            preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());

            //CameraControl cameraControl = camera.getCameraControl();
            mPreviewView.setOnTouchListener((view, motionEvent) -> {
                float x = motionEvent.getX();
                float y = motionEvent.getY();

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    runOnUiThread(() -> handlerFocus(x, y));

                    float width = mPreviewView.getWidth();
                    float height = mPreviewView.getHeight();

                    MeteringPointFactory factory = new SurfaceOrientedMeteringPointFactory(width, height);
                    MeteringPoint point = factory.createPoint(x, y);
                    //MeteringPoint point = mPreviewView.getMeteringPointFactory().createPoint(width, height);
                    FocusMeteringAction action = new FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                            .setAutoCancelDuration(3, TimeUnit.SECONDS)
                            .build();

                    CameraControl cameraControl = camera.getCameraControl();

                    ListenableFuture<FocusMeteringResult> future = cameraControl.startFocusAndMetering(action);
                    future.addListener(() -> {
                        try {
                            FocusMeteringResult result = future.get();
                            if (result.isFocusSuccessful()) {
                                runOnUiThread(() -> mFocusView.setVisibility(View.INVISIBLE));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, ContextCompat.getMainExecutor(WxCameraActivity.this));
                }
                return true;
            });

        } catch (Exception e) {
            //
            if (mCurrentUseCaseMode != Config.BUTTON_STATE_ONLY_CAPTURE) {
                mCurrentUseCaseMode = Config.BUTTON_STATE_ONLY_CAPTURE;
                mCameraErrorInfo.setVisibility(View.GONE);
                bindCameraUseCases();
            } else {
                mCameraErrorInfo.setVisibility(View.VISIBLE);
                //Toast.makeText(WxCameraActivity.this, "摄像头初始化失败", Toast.LENGTH_SHORT).show();
            }
            //Log.e(TAG, "Use case binding failed -> " + e.getMessage());
        }
    }

    private boolean hasBackCamera() {
        try {
            return mProcessCameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean hasFrontCamera() {
        try {
            return mProcessCameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA);
        } catch (Exception e) {
            return false;
        }
    }

    private void switchCamera() {
        if (mCurrentLensFacing == CameraSelector.LENS_FACING_BACK) {
            if (hasFrontCamera()) {
                mCurrentLensFacing = CameraSelector.LENS_FACING_FRONT;
                bindCameraUseCases();
            }
        } else {
            if (hasBackCamera()) {
                mCurrentLensFacing = CameraSelector.LENS_FACING_BACK;
                bindCameraUseCases();
            }
        }
    }

    private void handlerFocus(float x, float y) {
        if (y > mCaptureLayout.getTop()) {
            return;
        }
        mFocusView.setVisibility(View.VISIBLE);
        if (x < mFocusView.getWidth() / 2f) {
            x = mFocusView.getWidth() / 2f;
        }
        if (x > mWindowWidth - mFocusView.getWidth() / 2f) {
            x = mWindowWidth - mFocusView.getWidth() / 2f;
        }
        if (y < mFocusView.getWidth() / 2f) {
            y = mFocusView.getWidth() / 2f;
        }
        if (y > mCaptureLayout.getTop() - mFocusView.getWidth() / 2f) {
            y = mCaptureLayout.getTop() - mFocusView.getWidth() / 2f;
        }
        mFocusView.setX(x - mFocusView.getWidth() / 2f);
        mFocusView.setY(y - mFocusView.getHeight() / 2f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mFocusView, "scaleX", 1, 0.6f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mFocusView, "scaleY", 1, 0.6f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mFocusView, "alpha", 1f, 0.4f, 1f, 0.4f, 1f, 0.4f, 1f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(scaleX).with(scaleY).before(alpha);
        animSet.setDuration(400);
        animSet.start();
    }

}
