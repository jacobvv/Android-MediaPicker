package org.jacobvv.mediapicker;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StyleRes;

import org.jacobvv.mediapicker.data.QueryConfig;

/**
 * @author Jacob
 * @date 17-11-24
 */

public class Config implements Parcelable {

    public static final int MEDIA_ALL = 0;
    public static final int MEDIA_IMAGE = 1;
    public static final int MEDIA_VIDEO = 2;
    public static final int MEDIA_AUDIO = 4;

    public static final int CAPTURE_NONE = 0;
    public static final int CAPTURE_ALL_ALBUM = 1;
    public static final int CAPTURE_FIRST = 2;

    public static final int CLIP_NONE = 0;
    public static final int CLIP_RECT = 1;
    public static final int CLIP_CIRCLE = 2;

    @StyleRes
    int themeId;
    boolean isPhotoSave = true;

    int enableMediaType = MEDIA_IMAGE;
    int pickLimit = 9;
    long minSize = 1000;
    boolean enableAlbumSelect = false;
    boolean enableCapture = false;
    boolean enableEdit = false;
    boolean enableCompress = false;
    boolean enableCaptureInPickReturn = false;
    int clipType = CLIP_NONE;
    float clipRatio;
    @DrawableRes
    @Deprecated
    int checkbox = 0;

    public Config() {
    }

    public QueryConfig makeQueryConfig() {
        return new QueryConfig(enableMediaType, minSize, enableCapture);
    }

    public int getThemeId() {
        return themeId;
    }

    public boolean isPhotoSave() {
        return isPhotoSave;
    }

    public int getEnableMediaType() {
        return enableMediaType;
    }

    public int getPickLimit() {
        return pickLimit;
    }

    public long getMinSize() {
        return minSize;
    }

    public boolean isEnableAlbumSelect() {
        return enableAlbumSelect;
    }

    public boolean isEnableCapture() {
        return enableCapture;
    }

    public boolean isEnableEdit() {
        return enableEdit;
    }

    public boolean isEnableCompress() {
        return enableCompress;
    }

    public boolean isEnableCaptureInPickReturn() {
        return enableCaptureInPickReturn;
    }

    public boolean isEnableClip() {
        return clipType != CLIP_NONE;
    }

    public int getClipType() {
        return clipType;
    }

    public float getClipRatio() {
        return clipRatio;
    }

    @Deprecated
    public int getCheckbox() {
        return checkbox;
    }

    // Parcelable implementation

    protected Config(Parcel in) {
        themeId = in.readInt();
        isPhotoSave = in.readByte() != 0;
        enableMediaType = in.readInt();
        pickLimit = in.readInt();
        minSize = in.readLong();
        enableAlbumSelect = in.readByte() != 0;
        enableCapture = in.readByte() != 0;
        enableEdit = in.readByte() != 0;
        enableCompress = in.readByte() != 0;
        enableCaptureInPickReturn = in.readByte() != 0;
        clipType = in.readInt();
        clipRatio = in.readFloat();
        checkbox = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(themeId);
        dest.writeByte((byte) (isPhotoSave ? 1 : 0));
        dest.writeInt(enableMediaType);
        dest.writeInt(pickLimit);
        dest.writeLong(minSize);
        dest.writeByte((byte) (enableAlbumSelect ? 1 : 0));
        dest.writeByte((byte) (enableCapture ? 1 : 0));
        dest.writeByte((byte) (enableEdit ? 1 : 0));
        dest.writeByte((byte) (enableCompress ? 1 : 0));
        dest.writeByte((byte) (enableCaptureInPickReturn ? 1 : 0));
        dest.writeInt(clipType);
        dest.writeFloat(clipRatio);
        dest.writeInt(checkbox);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Config> CREATOR = new Creator<Config>() {
        @Override
        public Config createFromParcel(Parcel in) {
            return new Config(in);
        }

        @Override
        public Config[] newArray(int size) {
            return new Config[size];
        }
    };

}
