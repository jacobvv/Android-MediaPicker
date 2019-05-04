package org.jacobvv.mediapicker.data.model;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import org.jacobvv.mediapicker.common.recycler.BaseItemModel;
import org.jacobvv.mediapicker.data.QueryConfig;
import org.jacobvv.mediapicker.data.loader.MediaItemLoader;
import org.jacobvv.mediapicker.util.Utils;

import java.io.File;

/**
 * @author Jacob
 * @date 17-11-24
 */

public class Image implements BaseItemModel, Parcelable {

    public static final int FROM_NONE = -1;
    public static final int FROM_PHOTO = 1;
    public static final int FROM_PATH = 2;
    public static final int FROM_ALBUM = 3;

    private int from;
    private long id;
    private String name;
    private String mimeType;
    private int mediaType;
    private long size;
    private long duration;
    private Uri uri;
    private String path;
    private Uri compressed;
    private String remote;
    private String thumbnail;
    private boolean isChecked;
    private boolean isModified;
    private boolean isCompressed;

    private long lastModified;

    private Image(int from, Uri uri, String path, long id, String name,
                  String mimeType, int mediaType, long size, long duration, long lastModified) {
        this.from = from;
        this.uri = uri;
        this.path = path;
        this.id = id;
        this.name = name;
        this.mimeType = mimeType;
        this.mediaType = mediaType;
        this.size = size;
        this.duration = duration;
        this.lastModified = lastModified;
    }

    private Image(int from, Uri uri, String path, String name, long lastModified) {
        this.from = from;
        this.name = name;
        this.uri = uri;
        this.path = path;
        this.lastModified = lastModified;
    }

    public static Image fromNone() {
        return new Image(FROM_NONE, null, null, null, 0);
    }

    public static Image fromPhoto(File file) {
        return new Image(FROM_PHOTO, Uri.fromFile(file), file.getPath(), file.getName(), file.lastModified());
    }

    public static Image fromPath(Uri uri) {
        String name = null;
        String path = null;
        long lastModified = 0;
        if (Utils.isFileUri(uri)) {
            path = uri.getPath();
            File file = new File(path);
            name = file.getName();
            lastModified = file.lastModified();
        }
        return new Image(FROM_PATH, uri, path, name, lastModified);
    }

    public static Image fromCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(QueryConfig.COLUMN_ID));
        if (id == QueryConfig.ITEM_CAPTURE_ID) {
            return Image.fromNone();
        } else {
            String path = cursor.getString(cursor.getColumnIndexOrThrow(QueryConfig.COLUMN_PATH));
            return new Image(
                    FROM_ALBUM,
                    QueryConfig.getFileUri(id, path),
                    path,
                    id,
                    cursor.getString(cursor.getColumnIndexOrThrow(QueryConfig.COLUMN_DISPLAY_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(QueryConfig.COLUMN_MIME_TYPE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(QueryConfig.COLUMN_MEDIA_TYPE)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(QueryConfig.COLUMN_SIZE)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(QueryConfig.COLUMN_DURATION)),
                    new File(path).lastModified()
            );
        }
    }

    @Override
    public int getType() {
        if (from == FROM_NONE) {
            return 0;
        } else {
            return 1;
        }
    }

    public int getFrom() {
        return from;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public int getMediaType() {
        return mediaType;
    }

    public String getMediaTypeName() {
        switch (mediaType) {
            case MediaStore.Files.FileColumns.MEDIA_TYPE_NONE:
                return "not media";
            case MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE:
                return "image";
            case MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO:
                return "audio";
            case MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO:
                return "video";
            case MediaStore.Files.FileColumns.MEDIA_TYPE_PLAYLIST:
                return "play list";
            default:
                return "unknown";
        }
    }

    public long getSize() {
        return size;
    }

    public long getDuration() {
        return duration;
    }

    public Uri getUri() {
        return uri;
    }

    public String getPath() {
        return path;
    }

    public Uri getCompressed() {
        return compressed;
    }

    public Uri getAvailable() {
        if (isCompressed) {
            return compressed;
        }
        return uri;
    }

    public String getRemote() {
        return remote;
    }

    public void setRemote(String remote) {
        this.remote = remote;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean modified) {
        isModified = modified;
    }

    public boolean isCompressed() {
        return isCompressed;
    }

    public void setCompressed(boolean compressed) {
        isCompressed = compressed;
    }

    public void setCompressed(Uri compressed) {
        this.isCompressed = true;
        this.compressed = compressed;
    }

    public String getSignature() {
        return uri.toString() + lastModified;
    }

    public Cursor toCursor() {
        MatrixCursor cursor = new MatrixCursor(MediaItemLoader.PROJECTION);
        cursor.addRow(new Object[]{id, name, mimeType, mediaType, path, size, duration});
        return cursor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Image)) {
            return false;
        }

        Image image = (Image) o;
        return getPath() != null && getPath().equals(image.getPath())
                || getUri() != null && getUri().equals(image.getUri());
    }

    @Override
    public int hashCode() {
        int result = 0;
        if (getPath() != null) {
            result = getPath().hashCode();
        } else if (getUri() != null) {
            result = getUri().hashCode() * 256;
        }
        return result;
    }

    @Override
    public String toString() {
        return "Image{" +
                "uri=" + uri +
                ", path='" + path + '\'' +
                ", compressed=" + compressed +
                ", isChecked=" + isChecked +
                '}';
    }

    // Parcelable implementation

    protected Image(Parcel in) {
        from = in.readInt();
        id = in.readLong();
        name = in.readString();
        mimeType = in.readString();
        mediaType = in.readInt();
        size = in.readLong();
        duration = in.readLong();
        uri = in.readParcelable(Uri.class.getClassLoader());
        path = in.readString();
        compressed = in.readParcelable(Uri.class.getClassLoader());
        remote = in.readString();
        thumbnail = in.readString();
        isChecked = in.readByte() != 0;
        isModified = in.readByte() != 0;
        isCompressed = in.readByte() != 0;
        lastModified = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(from);
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(mimeType);
        dest.writeInt(mediaType);
        dest.writeLong(size);
        dest.writeLong(duration);
        dest.writeParcelable(uri, flags);
        dest.writeString(path);
        dest.writeParcelable(compressed, flags);
        dest.writeString(remote);
        dest.writeString(thumbnail);
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeByte((byte) (isModified ? 1 : 0));
        dest.writeByte((byte) (isCompressed ? 1 : 0));
        dest.writeLong(lastModified);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

}
