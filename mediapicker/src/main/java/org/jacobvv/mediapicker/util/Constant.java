package org.jacobvv.mediapicker.util;

import android.Manifest;

/**
 * @author Jacob
 * @date 17-11-27
 */

public final class Constant {
    public static final String PER_CAMERA = Manifest.permission.CAMERA;
    public static final String PER_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    static final String IMAGE_EXTERNAL_PATH = "jacob/files/jacob_photos";
    static final String IMAGE_INTERNAL_PATH = "photos";

    public static final String INTENT_EXTRA_CONFIG = "extra_config";
    public static final String INTENT_EXTRA_ALBUM = "extra_album";
    public static final String INTENT_EXTRA_IMAGE = "extra_image";
    public static final String INTENT_EXTRA_IMAGES = "extra_images";
    public static final String INTENT_EXTRA_POSITION = "extra_position";
    public static final String INTENT_EXTRA_MESSAGE = "extra_message";
    public static final String INTENT_EXTRA_CODE = "extra_code";

    public static final int REQUEST_TAKE_PHOTO = 21000;
    public static final int REQUEST_PICK_PHOTO = 22000;
    public static final int REQUEST_TAKE_PHOTO_PREVIEW = 21010;
    public static final int REQUEST_SINGLE_PREVIEW = 22010;
    public static final int REQUEST_MULTI_PREVIEW = 22020;

    public static final int RESULT_ERROR = 11;

    public static final String STATE_BUNDLE_CONFIG = "state_config";
    public static final String STATE_BUNDLE_ALBUM = "state_album";
    public static final String STATE_BUNDLE_IMAGE_POSITION = "state_image_position";
    public static final String STATE_BUNDLE_IMAGE = "state_image";
    public static final String STATE_BUNDLE_IMAGES_SELECTED = "state_images_selected";

    public static final int ERROR_CODE_UNKNOWN = 1000;
    public static final int ERROR_CODE_CREATE_FAIL = 1001;
}
