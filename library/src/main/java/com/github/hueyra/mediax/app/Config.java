package com.github.hueyra.mediax.app;

public class Config {

    public final static int TYPE_ALL = 0;
    public final static int TYPE_IMAGE = 1;
    public final static int TYPE_VIDEO = 2;
    @Deprecated
    public final static int TYPE_AUDIO = 3;

    public static final int BUTTON_STATE_BOTH = TYPE_ALL;
    public static final int BUTTON_STATE_ONLY_CAPTURE = TYPE_IMAGE;
    public static final int BUTTON_STATE_ONLY_RECORDER = TYPE_VIDEO;

    public static final int CHECK_PERMISSION_STATE_RECORDING = -1;
    public static final int CHECK_PERMISSION_STATE_NO_PERMISSION = -2;
    public static final int CHECK_PERMISSION_STATE_SUCCESS = 1;

    public final static int APPLY_STORAGE_PERMISSIONS_CODE = 1;
    public final static int APPLY_CAMERA_PERMISSIONS_CODE = 2;
    public final static int APPLY_AUDIO_PERMISSIONS_CODE = 3;
    public final static int APPLY_RECORD_AUDIO_PERMISSIONS_CODE = 4;
    public final static int APPLY_CAMERA_STORAGE_PERMISSIONS_CODE = 5;

    public final static String EXTRA_MEDIA_KEY = "mediaKey";
    public final static String EXTRA_MEDIA_PATH = "mediaPath";
    public final static String EXTRA_AUDIO_PATH = "audioPath";
    public final static String EXTRA_VIDEO_PATH = "videoPath";
    public final static String EXTRA_PREVIEW_VIDEO = "isExternalPreviewVideo";
    public final static String EXTRA_PREVIEW_DELETE_POSITION = "position";
    public final static String EXTRA_FC_TAG = "picture";
    public final static String EXTRA_PREVIEW_SELECT_LIST = "previewSelectList";
    public final static String EXTRA_SELECT_LIST = "selectList";
    public final static String EXTRA_RESULT_ENTITY = "result";
    public final static String EXTRA_RESULT_LIST = "resultList";
    public final static String EXTRA_COMPLETE_SELECTED = "isCompleteOrSelected";
    public final static String EXTRA_CHANGE_SELECTED_DATA = "isChangeSelectedData";
    public final static String EXTRA_CHANGE_ORIGINAL = "isOriginal";
    public final static String EXTRA_POSITION = "position";
    public final static String EXTRA_OLD_CURRENT_LIST_SIZE = "oldCurrentListSize";
    public final static String EXTRA_DIRECTORY_PATH = "directory_path";
    public final static String EXTRA_BOTTOM_PREVIEW = "bottom_preview";
    public final static String EXTRA_CONFIG = "PictureSelectorConfig";
    public final static String EXTRA_SHOW_CAMERA = "isShowCamera";
    public final static String EXTRA_IS_CURRENT_DIRECTORY = "currentDirectory";
    public final static String EXTRA_BUCKET_ID = "bucket_id";
    public final static String EXTRA_PAGE = "page";
    public final static String EXTRA_DATA_COUNT = "count";
    public final static String CAMERA_FACING = "android.intent.extras.CAMERA_FACING";

    public final static String EXTRA_ALL_FOLDER_SIZE = "all_folder_size";
    public final static String EXTRA_QUICK_CAPTURE = "android.intent.extra.quickCapture";

    public final static String STORAGE_PATH_IMAGE_NAME = "image";
    public final static String STORAGE_PATH_VIDEO_NAME = "video";
    public final static String STORAGE_PATH_AUDIO_NAME = "audio";

    public static final String EXTERNAL_FILES_DIR_NAME = "MediaX";

    public final static String FILE_NAME_IMAGE_PREFIX = "IMG_";
    public final static String FILE_NAME_IMAGE_CROP_PREFIX = "IMG_CROP_";
    public final static String FILE_NAME_IMAGE_EDIT_PREFIX = "IMG_EDIT_";
    public final static String FILE_NAME_VIDEO_PREFIX = "VDO_";
    public final static String FILE_NAME_VIDEO_CROP_PREFIX = "VDO_CROP_";
    public final static String FILE_NAME_VIDEO_EDIT_PREFIX = "VDO_EDIT_";

    public final static String FILE_SAVE_TYPE_IMAGE = ".jpg";
    public final static String FILE_SAVE_TYPE_VIDEO = ".mp4";


    public final static String MIME_TYPE_JPG = "image/jpg";
    public final static String MIME_TYPE_MP4 = "video/mp4";

    public final static int MAX_PAGE_SIZE = 60;

    public final static int MIN_PAGE_SIZE = 10;

    public final static int LOADED = 0;

    public final static int NORMAL = -1;

    public final static int CAMERA_BEFORE = 1;

    public final static int MAX_COMPRESS_SIZE = 100;

    public final static int DEFAULT_SPAN_COUNT = 4;

    public final static int TYPE_CAMERA = 11;
    public final static int TYPE_PICTURE = 12;

    public final static int SINGLE = 1;
    public final static int MULTIPLE = 2;

    public final static int PREVIEW_VIDEO_CODE = 166;
    public final static int REQUEST_OPEN_ALBUM = 910;
    public final static int REQUEST_OPEN_CAMERA = 909;
    public final static int REQUEST_CROP = 69;
    public final static int REQUEST_MULTI_CROP = 609;

}
