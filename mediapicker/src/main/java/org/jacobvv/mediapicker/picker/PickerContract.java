package org.jacobvv.mediapicker.picker;

import android.content.Context;
import android.database.Cursor;

import org.jacobvv.mediapicker.base.IPresenter;
import org.jacobvv.mediapicker.base.IView;
import org.jacobvv.mediapicker.data.model.Album;
import org.jacobvv.mediapicker.data.model.Image;
import org.jacobvv.mediapicker.data.model.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jacob on 17-11-28.
 */

public interface PickerContract {

    interface View extends IView {
        void showContent();

        void showLoading();

        void updateAlbums(List<Album> albums, int currentAlbumIndex);

        void updateMedia(Cursor cursor);

        void returnResult(Result result);
    }

    interface Presenter extends IPresenter {
        Context getContext();

        void start();

        void loadMedia(long bucketId);

        void updateMedia(Image media);

        Album getAlbum();

        void setAlbum(Album album);

        int getSelectSize();

        ArrayList<Image> getSelectItems();

        void setSelectItems(List<Image> items);

        boolean containSelectItem(Image item);

        void addSelectItem(Image item);

        void removeSelectItem(Image item);

        void clearSelectItem();

        void completePick();
    }
}
