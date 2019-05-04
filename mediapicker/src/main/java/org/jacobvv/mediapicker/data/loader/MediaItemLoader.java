package org.jacobvv.mediapicker.data.loader;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import org.jacobvv.mediapicker.data.QueryConfig;
import org.jacobvv.mediapicker.data.model.Album;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jacob
 * @date 18-3-12
 */
public class MediaItemLoader extends CursorLoader {

    public static final String[] PROJECTION = {
            QueryConfig.COLUMN_ID,
            QueryConfig.COLUMN_DISPLAY_NAME,
            QueryConfig.COLUMN_MIME_TYPE,
            QueryConfig.COLUMN_MEDIA_TYPE,
            QueryConfig.COLUMN_PATH,
            QueryConfig.COLUMN_SIZE,
            QueryConfig.COLUMN_DURATION
    };
    private static final String TAG = "MediaItemLoader";
    private static final String SELECTION_BUCKET = QueryConfig.COLUMN_BUCKET_ID + "=?";
    private static final String SELECTION_TYPE = QueryConfig.COLUMN_MEDIA_TYPE + "=?";
    private static final String SELECTION_SIZE = QueryConfig.COLUMN_SIZE + ">? OR " +
            QueryConfig.COLUMN_SIZE + " IS NULL";
    private static final String ORDER = QueryConfig.COLUMN_DATE_TAKEN + " DESC";

    private MediaItemLoader(Context context, String selection, String[] selectionArgs) {
        super(context, QueryConfig.EXTERNAL_CONTENT_URI, PROJECTION,
                selection, selectionArgs, ORDER);
    }

    private static String generateSelectionType(QueryConfig config, List<String> args) {
        String selection = "(";
        if (config.showImage()) {
            selection += SELECTION_TYPE;
            args.add(String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE));
        }
        if (config.showVideo()) {
            selection = " OR " + SELECTION_TYPE;
            args.add(String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO));
        }
        if (config.showAudio()) {
            selection = " OR " + SELECTION_TYPE;
            args.add(String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO));
        }
        selection += ")";
        return selection;
    }

    public static CursorLoader newInstance(Context context, QueryConfig config, long bucketId) {
        String selection;
        String[] selectionArgs = new String[]{};
        List<String> args = new ArrayList<>(1);
        if (bucketId == Album.BUCKET_ID_ALL && config.showAllType()) {
            selection = SELECTION_SIZE;
            args.add(String.valueOf(config.getMediaMinSize()));
        } else if (bucketId == Album.BUCKET_ID_ALL && !config.showAllType()) {
            selection = generateSelectionType(config, args) + " AND (" + SELECTION_SIZE + ")";
            args.add(String.valueOf(config.getMediaMinSize()));
        } else if (bucketId != Album.BUCKET_ID_ALL && config.showAllType()) {
            selection = SELECTION_BUCKET + " AND (" + SELECTION_SIZE + ")";
            args.add(String.valueOf(bucketId));
            args.add(String.valueOf(config.getMediaMinSize()));
        } else {
            args.add(String.valueOf(bucketId));
            selection = SELECTION_BUCKET + " AND " +
                    generateSelectionType(config, args) + " AND (" + SELECTION_SIZE + ")";
            args.add(String.valueOf(config.getMediaMinSize()));
        }
        Log.d(TAG, "SELECTION: " + selection);
        Log.d(TAG, "SELECTION_ARGS: " + args);
        return new MediaItemLoader(context, selection, args.toArray(selectionArgs));
    }

    @Override
    public Cursor loadInBackground() {
        return super.loadInBackground();
    }
}
