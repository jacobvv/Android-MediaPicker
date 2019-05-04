package org.jacobvv.mediapicker.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.jacobvv.mediapicker.R;
import org.jacobvv.mediapicker.data.model.Image;
import org.jacobvv.mediapicker.util.luban.Luban;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Jacob
 * @date 18-4-2
 */
public class CompressAsyncTask extends BaseAsyncTask<Image, Float, List<Image>> {

    private static final String TAG = "CompressAsyncTask";

    private Context mContext;
    private Exception mException;
    private boolean isError = false;
    private OnCompressListener mCallback;

    public CompressAsyncTask(Context context, OnCompressListener listener) {
        this.mContext = context.getApplicationContext();
        this.mCallback = listener;
    }

    @Override
    protected void onPreExecute() {
        mCallback.onStart();
        final File compressCache = new File(mContext.getExternalCacheDir(), "compress");
        if (!compressCache.exists()) {
            if (!compressCache.mkdirs()) {
                Log.e(TAG, "Create directory FAILED!");
                Toast.makeText(mContext, mContext.getString(R.string.error_create_dir),
                        Toast.LENGTH_SHORT).show();
                mException = new IOException("Create directory FAILED!");
            }
        } else if (compressCache.isFile()) {
            Log.e(TAG, "Path is exist and NOT is a directory!");
            Toast.makeText(mContext, mContext.getString(R.string.error_path),
                    Toast.LENGTH_SHORT).show();
            mException = new IOException("Path is exist and NOT is a directory!");
        }
    }

    @Override
    protected List<Image> doInBackground(Image... images) {
        if (mException != null) {
            return null;
        }
        List<Image> result = Arrays.asList(images);
        try {
            int size = result.size();
            List<String> path = new ArrayList<>(size);
            for (Image image : result) {
                path.add(image.getPath());
            }
            List<File> files = Luban.with(mContext).load(path).compress();
            if (size != files.size()) {
                Log.e(TAG, "Compress file count ERROR.");
                throw new IOException("Compress file count ERROR.");
            }
            for (int i = 0; i < files.size(); i++) {
                File compressedFile = files.get(i);
                Image image = result.get(i);
                image.setCompressed(Uri.fromFile(compressedFile));
            }
        } catch (Exception e) {
            mException = e;
            return null;
        }
        return result;
    }

    @Override
    protected void onPostExecute(List<Image> images) {
        if (images == null && mException != null) {
            mCallback.onError(mException);
        } else {
            mCallback.onSuccess(images);
        }
    }

}
