package org.jacobvv.mediapicker.data;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import org.jacobvv.mediapicker.Config;

import java.io.File;

/**
 * @author Jacob
 * @date 18-3-12.
 */

public class QueryConfig implements Parcelable {

    public static final Uri EXTERNAL_CONTENT_URI =
            MediaStore.Files.getContentUri("external");

    public static final String COLUMN_ID = MediaStore.Files.FileColumns._ID;
    public static final String COLUMN_DISPLAY_NAME = MediaStore.MediaColumns.DISPLAY_NAME;
    public static final String COLUMN_BUCKET_ID = "bucket_id";
    public static final String COLUMN_BUCKET_DISPLAY_NAME = "bucket_display_name";
    public static final String COLUMN_PATH = MediaStore.MediaColumns.DATA;
    public static final String COLUMN_DATE_TAKEN = "datetaken";
    public static final String COLUMN_SIZE = MediaStore.MediaColumns.SIZE;
    public static final String COLUMN_MEDIA_TYPE = MediaStore.Files.FileColumns.MEDIA_TYPE;
    public static final String COLUMN_MIME_TYPE = MediaStore.MediaColumns.MIME_TYPE;
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_COUNT = "count";

    public static final long ITEM_CAPTURE_ID = -1;
    public static final String ITEM_CAPTURE_NAME = "Capture";

    private int mediaType = Config.MEDIA_IMAGE;
    private long mediaMinSize = 1000;
    private boolean enableCapture = false;

    public QueryConfig(int mediaType, long size, boolean enableCapture) {
        this.mediaType = mediaType;
        this.mediaMinSize = size;
        this.enableCapture = enableCapture;
    }

    public static Uri getFileUri(long id, String path) {
        if (id == 0) {
            return Uri.fromFile(new File(path));
        } else {
            return ContentUris.withAppendedId(EXTERNAL_CONTENT_URI, id);
        }
    }

    public boolean showImage() {
        return (mediaType & Config.MEDIA_IMAGE) != 0;
    }

    public boolean showVideo() {
        return (mediaType & Config.MEDIA_VIDEO) != 0;
    }

    public boolean showAudio() {
        return (mediaType & Config.MEDIA_AUDIO) != 0;
    }

    public boolean showAllType() {
        return mediaType == Config.MEDIA_ALL;
    }

    public long getMediaMinSize() {
        return mediaMinSize;
    }

    public boolean isEnableCapture() {
        return enableCapture;
    }

    // Parcelable implementation

    protected QueryConfig(Parcel in) {
        mediaType = in.readInt();
        mediaMinSize = in.readLong();
        enableCapture = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mediaType);
        dest.writeLong(mediaMinSize);
        dest.writeByte((byte) (enableCapture ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<QueryConfig> CREATOR = new Creator<QueryConfig>() {
        @Override
        public QueryConfig createFromParcel(Parcel in) {
            return new QueryConfig(in);
        }

        @Override
        public QueryConfig[] newArray(int size) {
            return new QueryConfig[size];
        }
    };

}
