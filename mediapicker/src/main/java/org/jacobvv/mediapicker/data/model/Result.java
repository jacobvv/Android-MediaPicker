package org.jacobvv.mediapicker.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Jacob
 * @date 17-11-24
 */

public class Result implements Cloneable, Parcelable {

    private List<Image> images;

    public Result(Image image) {
        images = new ArrayList<>(1);
        images.add(image);
    }

    public Result(Collection<Image> images) {
        this.images = new ArrayList<>();
        this.images.addAll(images);
    }

    public Image getImage() {
        if (images != null & !images.isEmpty()) {
            return images.get(0);
        }
        return null;
    }

    public void removeImg(int postion) {
        images.remove(postion);
    }

    public List<Image> getImages() {
        return images;
    }

    @Override
    public Result clone() {
        try {
            return (Result) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Parcelable implementation

    protected Result(Parcel in) {
        images = in.createTypedArrayList(Image.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(images);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };

}
