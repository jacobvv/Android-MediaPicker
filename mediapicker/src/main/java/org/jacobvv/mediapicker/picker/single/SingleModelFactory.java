package org.jacobvv.mediapicker.picker.single;

import android.database.Cursor;

import org.jacobvv.mediapicker.common.recycler.CommonCursorModelFactory;
import org.jacobvv.mediapicker.data.model.Image;

/**
 * Created by Jacob on 18-3-23.
 */

public class SingleModelFactory implements CommonCursorModelFactory<Image> {
    @Override
    public Image fromCursor(Cursor cursor) {
        return Image.fromCursor(cursor);
    }
}
