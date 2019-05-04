package org.jacobvv.mediapicker.data.loader;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContentResolverCompat;
import android.support.v4.os.CancellationSignal;
import android.support.v4.os.OperationCanceledException;
import android.util.Log;

import org.jacobvv.mediapicker.data.AlbumComparator;
import org.jacobvv.mediapicker.data.QueryConfig;
import org.jacobvv.mediapicker.data.model.Album;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Jacob
 * @date 18-3-12
 */
public class AlbumLoader extends AsyncTaskLoader<List<Album>> {

    private static final String TAG = "AlbumLoader";

    private static final String[] PROJECTION = new String[]{
            QueryConfig.COLUMN_BUCKET_ID,
            QueryConfig.COLUMN_BUCKET_DISPLAY_NAME,
            QueryConfig.COLUMN_PATH,
            "COUNT(*) AS " + QueryConfig.COLUMN_COUNT
    };

    private static final String SELECTION_TYPE = QueryConfig.COLUMN_MEDIA_TYPE + "=?";
    private static final String SELECTION_SIZE = QueryConfig.COLUMN_SIZE + ">? OR " +
            QueryConfig.COLUMN_SIZE + " IS NULL";
    private static final String SELECTION_GROUP = ") GROUP BY (" + QueryConfig.COLUMN_BUCKET_ID;
    private static final String ORDER = "MAX(" + QueryConfig.COLUMN_DATE_TAKEN + ") DESC";

    private CancellationSignal mCancellationSignal;
    private Uri mUri;
    private String[] mProjection;
    private String mSelection;
    private String[] mSelectionArgs;
    private String mSortOrder;
    private QueryConfig mConfig;
    private List<Album> mAlbums;

    /**
     * Creates a fully-specified CursorLoader.  See
     * {@link ContentResolver#query(Uri, String[], String, String[], String)
     * ContentResolver.query()} for documentation on the meaning of the
     * parameters.  These will be passed as-is to that call.
     */
    private AlbumLoader(Context context, QueryConfig config,
                        String selection, String[] selectionArgs) {
        super(context);
        mConfig = config;
        mUri = QueryConfig.EXTERNAL_CONTENT_URI;
        mProjection = PROJECTION;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mSortOrder = ORDER;
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

    public static AlbumLoader newInstance(Context context, QueryConfig config) {
        String selection;
        String[] selectionArgs = new String[]{};
        List<String> args = new ArrayList<>(1);
        if (config.showAllType()) {
            selection = SELECTION_SIZE + SELECTION_GROUP;
            args.add(String.valueOf(config.getMediaMinSize()));
        } else {
            selection = generateSelectionType(config, args) +
                    " AND (" + SELECTION_SIZE + ")" + SELECTION_GROUP;
            args.add(String.valueOf(config.getMediaMinSize()));
        }
        Log.d(TAG, "SELECTION: " + selection);
        Log.d(TAG, "SELECTION_ARGS: " + args);
        return new AlbumLoader(context, config, selection, args.toArray(selectionArgs));
    }

    @WorkerThread
    @Override
    public List<Album> loadInBackground() {
        synchronized (this) {
            if (isLoadInBackgroundCanceled()) {
                throw new OperationCanceledException();
            }
            mCancellationSignal = new CancellationSignal();
        }
        Cursor cursor = null;
        try {
            cursor = ContentResolverCompat.query(getContext().getContentResolver(),
                    mUri, mProjection, mSelection, mSelectionArgs, mSortOrder,
                    mCancellationSignal);
            List<Album> albums = new ArrayList<>();
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                String coverAll = cursor.getString(cursor.getColumnIndexOrThrow(QueryConfig.COLUMN_PATH));
                int countAll = 0;
                do {
                    albums.add(Album.fromCursor(cursor));
                    countAll += cursor.getInt(cursor.getColumnIndexOrThrow(QueryConfig.COLUMN_COUNT));
                } while (cursor.moveToNext());
                albums.add(0, Album.fromAll(coverAll, countAll));
                Collections.sort(albums, new AlbumComparator());
                if (!albums.isEmpty()) {
                    albums.get(0).setContainCapture(mConfig.isEnableCapture());
                }
            } else {
                Album allAlbum = Album.fromAll(null, 0);
                allAlbum.setContainCapture(mConfig.isEnableCapture());
                albums.add(0, allAlbum);
            }
            return albums;
        } finally {
            synchronized (this) {
                mCancellationSignal = null;
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    @Override
    public void cancelLoadInBackground() {
        super.cancelLoadInBackground();

        synchronized (this) {
            if (mCancellationSignal != null) {
                mCancellationSignal.cancel();
            }
        }
    }

    @MainThread
    @Override
    public void deliverResult(List<Album> list) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            return;
        }
        mAlbums = list;
        if (isStarted()) {
            super.deliverResult(list);
        }
    }

    /**
     * Starts an asynchronous load of the contacts list data. When the result is ready the callbacks
     * will be called on the UI thread. If a previous load has been completed and is still valid
     * the result may be passed to the callbacks immediately.
     * <p>
     * Must be called from the UI thread
     */
    @Override
    protected void onStartLoading() {
        if (mAlbums != null) {
            deliverResult(mAlbums);
        }
        if (takeContentChanged() || mAlbums == null) {
            forceLoad();
        }
    }

    /**
     * Must be called from the UI thread
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();
        mAlbums = null;
    }

    @Override
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
        writer.print(prefix);
        writer.print("mUri=");
        writer.println(mUri);
        writer.print(prefix);
        writer.print("mProjection=");
        writer.println(Arrays.toString(mProjection));
        writer.print(prefix);
        writer.print("mSelection=");
        writer.println(mSelection);
        writer.print(prefix);
        writer.print("mSelectionArgs=");
        writer.println(Arrays.toString(mSelectionArgs));
        writer.print(prefix);
        writer.print("mSortOrder=");
        writer.println(mSortOrder);
    }

}
