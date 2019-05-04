package org.jacobvv.mediapicker.data.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import org.jacobvv.mediapicker.data.QueryConfig;

import java.io.File;

/**
 * @author Jacob
 * @date 17-12-6
 */
public class Album implements Parcelable {

    public static final long BUCKET_ID_ALL = -1L;
    public static final String BUCKET_NAME_ALL = "All";
    public static final String BUCKET_NAME_CAMERA = "camera";
    public static final String BUCKET_NAME_SCREENSHOTS = "screenshots";

    private long id;
    private String name;
    private String path;
    private String cover;
    private int count;
    private boolean isChecked;
    private boolean containCapture;

    private Album(long id, String name, String cover, String path, int count) {
        this.id = id;
        this.name = name;
        this.cover = cover;
        this.path = path;
        this.count = count;
    }

    public static Album fromMedia(String name, String cover, String path) {
        return new Album(0, name, cover, path, 1);
    }

    public static Album fromAll(String cover, int count) {
        String path = cover == null ? null : new File(cover).getParent();
        return new Album(BUCKET_ID_ALL, BUCKET_NAME_ALL, cover, path, count);
    }

    public static Album fromCursor(Cursor cursor) {
        String cover = cursor.getString(cursor.getColumnIndexOrThrow(QueryConfig.COLUMN_PATH));
        return new Album(
                cursor.getLong(cursor.getColumnIndexOrThrow(QueryConfig.COLUMN_BUCKET_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(QueryConfig.COLUMN_BUCKET_DISPLAY_NAME)),
                cover,
                new File(cover).getParent(),
                cursor.getInt(cursor.getColumnIndexOrThrow(QueryConfig.COLUMN_COUNT))
        );
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getCover() {
        return cover;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isContainCapture() {
        return containCapture;
    }

    public void setContainCapture(boolean containCapture) {
        this.containCapture = containCapture;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Album)) {
            return false;
        }

        Album album = (Album) o;

        return getId() == album.getId();
    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }

    @Override
    public String toString() {
        return "Album{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", count=" + count +
                '}';
    }

    // Parcelable implementation

    protected Album(Parcel in) {
        id = in.readLong();
        name = in.readString();
        path = in.readString();
        cover = in.readString();
        count = in.readInt();
        isChecked = in.readByte() != 0;
        containCapture = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(path);
        dest.writeString(cover);
        dest.writeInt(count);
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeByte((byte) (containCapture ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

}
