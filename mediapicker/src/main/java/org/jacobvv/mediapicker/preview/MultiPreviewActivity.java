package org.jacobvv.mediapicker.preview;

import android.content.Intent;
import android.database.Cursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jacobvv.mediapicker.Config;
import org.jacobvv.mediapicker.R;
import org.jacobvv.mediapicker.data.MediaRepository;
import org.jacobvv.mediapicker.data.QueryConfig;
import org.jacobvv.mediapicker.data.model.Album;
import org.jacobvv.mediapicker.data.model.Image;
import org.jacobvv.mediapicker.util.BarUtils;
import org.jacobvv.mediapicker.util.Constant;
import org.jacobvv.mediapicker.util.Utils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author Jacob
 */
public class MultiPreviewActivity extends AppCompatActivity {

    private static final String TAG = "MultiPreviewActivity";

    private Toolbar mToolbar;
    private TextView mBtnDown;

    private ViewPager mViewPager;
    private ProgressBar mProgressBar;

    private MediaRepository mRepo;
    private Config mConfig;
    private Album mAlbum;
    private Image mImage;
    private int mPosition;

    private LinkedHashSet<Image> mSelectImages = new LinkedHashSet<>();
    private MultiPreviewAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init data
        List<Image> selected;
        if (savedInstanceState != null) {
            mConfig = savedInstanceState.getParcelable(Constant.STATE_BUNDLE_CONFIG);
            mAlbum = savedInstanceState.getParcelable(Constant.STATE_BUNDLE_ALBUM);
            mImage = savedInstanceState.getParcelable(Constant.STATE_BUNDLE_IMAGE);
            mPosition = savedInstanceState.getInt(Constant.STATE_BUNDLE_IMAGE_POSITION, 0);
            selected = savedInstanceState.getParcelableArrayList(Constant.STATE_BUNDLE_IMAGES_SELECTED);
        } else {
            Intent intent = getIntent();
            mConfig = intent.getParcelableExtra(Constant.INTENT_EXTRA_CONFIG);
            mAlbum = intent.getParcelableExtra(Constant.INTENT_EXTRA_ALBUM);
            mImage = intent.getParcelableExtra(Constant.INTENT_EXTRA_IMAGE);
            mPosition = intent.getIntExtra(Constant.INTENT_EXTRA_POSITION, 0);
            selected = intent.getParcelableArrayListExtra(Constant.INTENT_EXTRA_IMAGES);
        }
        if (mConfig.getThemeId() != 0) {
            setTheme(mConfig.getThemeId());
        }
        mSelectImages.addAll(selected);

        setContentView(R.layout.preview_multi_act);

        // Init view
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mBtnDown = (TextView) findViewById(R.id.toolbar_btn_ok);
        mViewPager = (ViewPager) findViewById(R.id.preview_pager);
        mProgressBar = (ProgressBar) findViewById(R.id.preview_progress_bar);

        mRepo = new MediaRepository(this, mConfig.makeQueryConfig());
        initToolBar();
        loadData();
    }

    private void initToolBar() {
        BarUtils.setStatusBarLightMode(this, true);
        BarUtils.setStatusBarColor(this, ContextCompat.getColor(this, android.R.color.white));
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                down();
            }
        });
        mBtnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                down();
            }
        });
        mBtnDown.setText(getString(R.string.btn_text_down_count, Utils.getCheckedNum(
                mSelectImages.size(), mConfig.getPickLimit())));
    }

    private void loadData() {
        mRepo.loadMedia(mAlbum.getId(), new LoadMediaCallbacks());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constant.STATE_BUNDLE_CONFIG, mConfig);
        outState.putParcelable(Constant.STATE_BUNDLE_ALBUM, mAlbum);
        outState.putParcelable(Constant.STATE_BUNDLE_IMAGE, mImage);
        outState.putInt(Constant.STATE_BUNDLE_IMAGE_POSITION, mPosition);
        outState.putParcelableArrayList(Constant.STATE_BUNDLE_IMAGES_SELECTED, new ArrayList<>(mSelectImages));
    }

    @Override
    public void onBackPressed() {
        down();
    }

    private void down() {
        Intent i = new Intent();
        i.putParcelableArrayListExtra(Constant.INTENT_EXTRA_IMAGES, new ArrayList<>(mSelectImages));
        setResult(RESULT_OK, i);
        finish();
    }

    private class OnImageCheckListener implements MultiPreviewAdapter.OnItemCheckListener {
        @Override
        public void onCheck(CheckBox checkBox, Image image) {
            if (!image.isChecked() && mSelectImages.size() == mConfig.getPickLimit()) {
                Log.w(TAG, "Image selection reached the limit! Choose up to "
                        + mConfig.getPickLimit() + " pictures.");
                Toast.makeText(MultiPreviewActivity.this,
                        getString(R.string.error_limit, mConfig.getPickLimit()),
                        Toast.LENGTH_SHORT).show();
                checkBox.setChecked(false);
            } else {
                image.setChecked(!image.isChecked());
                if (image.isChecked()) {
                    mSelectImages.add(image);
                } else {
                    mSelectImages.remove(image);
                }
                mBtnDown.setText(getString(R.string.btn_text_down_count, Utils.getCheckedNum(
                        mSelectImages.size(), mConfig.getPickLimit())));
            }
        }
    }

    private class LoadMediaCallbacks implements MediaRepository.MediaCallbacks {

        @Override
        public void onMediaLoaded(long bucketId, Cursor cursor) {
            if (mImage != null) {
                if (cursor == null || !cursor.moveToFirst()) {
                    cursor = mImage.toCursor();
                } else {
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(QueryConfig.COLUMN_PATH));
                    if (!mImage.getPath().equals(path)) {
                        cursor = new MergeCursor(new Cursor[]{mImage.toCursor(), cursor});
                    }
                }
            }

            mAdapter = new MultiPreviewAdapter(mConfig, new OnImageCheckListener(),
                    cursor, mSelectImages);
            mViewPager.setAdapter(mAdapter);
            mViewPager.setCurrentItem(mAlbum.isContainCapture() ? mPosition - 1 : mPosition);
            mViewPager.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onMediaReset(long bucketId) {
            mAdapter.setCursor(null);
        }

    }

}
