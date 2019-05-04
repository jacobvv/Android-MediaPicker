package org.jacobvv.mediapicker.common.recycler;

import android.database.Cursor;

/**
 * @author Jacob
 * @date 18-4-3
 */

public interface CommonCursorModelFactory<T> {

    /**
     * Create item model by cursor.
     *
     * @param cursor cursor which provide data.
     * @return item model from cursor.
     */
    T fromCursor(Cursor cursor);
}
