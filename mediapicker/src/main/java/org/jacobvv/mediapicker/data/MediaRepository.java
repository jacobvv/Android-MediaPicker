/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jacobvv.mediapicker.data;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import org.jacobvv.mediapicker.data.loader.AlbumLoader;
import org.jacobvv.mediapicker.data.loader.MediaItemLoader;
import org.jacobvv.mediapicker.data.model.Album;

import java.util.List;

/**
 * @author Jacob
 * @date 18-3-12
 */
public class MediaRepository {

    public static final String TAG = "MediaRepository";

    private static final int LOADER_ID_ALBUM = 1;
    private static final int LOADER_ID_MEDIA = 2;

    private static final String ARGS_CONFIG = "args_config";
    private static final String ARGS_BUCKET_ID = "args_bucket_id";

    private static final int CACHE_MAX_SIZE = 10000;

    private Context mContext;
    private LoaderManager mLoaderManager;
    private QueryConfig mConfig;

    public MediaRepository(FragmentActivity activity, QueryConfig config) {
        mContext = activity.getApplicationContext();
        mLoaderManager = activity.getSupportLoaderManager();
        mConfig = config;
    }

    public void loadAlbums(@NonNull AlbumCallbacks callbacks) {
        Bundle args = new Bundle();
        args.putParcelable(ARGS_CONFIG, mConfig);
        if (mLoaderManager.getLoader(LOADER_ID_ALBUM) != null) {
            mLoaderManager.restartLoader(LOADER_ID_ALBUM, args,
                    new AlbumLoaderCallbacks(callbacks));
        } else {
            mLoaderManager.initLoader(LOADER_ID_ALBUM, args,
                    new AlbumLoaderCallbacks(callbacks));
        }
    }

    public void loadMedia(long bucketId, @NonNull MediaCallbacks callbacks) {
        Bundle args = new Bundle();
        args.putParcelable(ARGS_CONFIG, mConfig);
        args.putLong(ARGS_BUCKET_ID, bucketId);
        if (mLoaderManager.getLoader(LOADER_ID_MEDIA) != null) {
            mLoaderManager.restartLoader(LOADER_ID_MEDIA, args,
                    new MediaLoaderCallbacks(bucketId, callbacks));
        } else {
            mLoaderManager.initLoader(LOADER_ID_MEDIA, args,
                    new MediaLoaderCallbacks(bucketId, callbacks));
        }
    }

    public interface AlbumCallbacks {
        /**
         * Called when album load complete and return albums data.
         *
         * @param albums all albums load from db.
         */
        void onAlbumLoaded(List<Album> albums);
    }

    public interface MediaCallbacks {
        /**
         * Called when media load complete and return media item data.
         *
         * @param bucketId bucket id of media loaded.
         * @param cursor   media items cursor of given bucket id load from db.
         */
        void onMediaLoaded(long bucketId, Cursor cursor);

        /**
         * Called when a previously data loaded is being reset, and thus
         * making its data unavailable.  The application should at this point
         * remove any references it has to the Loader's data.
         *
         * @param bucketId bucket id of media reset.
         */
        void onMediaReset(long bucketId);
    }

    private class AlbumLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Album>> {

        private AlbumCallbacks mCallbacks;

        private AlbumLoaderCallbacks(AlbumCallbacks callbacks) {
            mCallbacks = callbacks;
        }

        @Override
        public Loader<List<Album>> onCreateLoader(int id, Bundle args) {
            QueryConfig config = args.getParcelable(ARGS_CONFIG);
            return AlbumLoader.newInstance(mContext, config);
        }

        @Override
        public void onLoadFinished(Loader<List<Album>> loader, List<Album> data) {
            mCallbacks.onAlbumLoaded(data);
        }

        @Override
        public void onLoaderReset(Loader<List<Album>> loader) {
        }
    }

    private class MediaLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        private long mBucketId;
        private MediaCallbacks mCallbacks;

        private MediaLoaderCallbacks(long bucketId, MediaCallbacks callbacks) {
            mBucketId = bucketId;
            mCallbacks = callbacks;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            QueryConfig config = args.getParcelable(ARGS_CONFIG);
            long bucketId = args.getLong(ARGS_BUCKET_ID);
            return MediaItemLoader.newInstance(mContext, config, bucketId);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mCallbacks.onMediaLoaded(mBucketId, data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mCallbacks.onMediaReset(mBucketId);
        }
    }

}
