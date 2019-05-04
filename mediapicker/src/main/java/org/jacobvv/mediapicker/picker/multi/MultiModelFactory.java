package org.jacobvv.mediapicker.picker.multi;

import android.database.Cursor;

import org.jacobvv.mediapicker.common.recycler.CommonCursorModelFactory;
import org.jacobvv.mediapicker.data.model.Image;

/**
 * Created by Jacob on 18-3-23.
 */

public class MultiModelFactory implements CommonCursorModelFactory<Image> {
    @Override
    public Image fromCursor(Cursor cursor) {
        return Image.fromCursor(cursor);
    }
}
