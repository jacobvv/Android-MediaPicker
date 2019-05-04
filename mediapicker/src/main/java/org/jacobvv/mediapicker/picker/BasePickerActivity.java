package org.jacobvv.mediapicker.picker;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jacobvv.mediapicker.Config;
import org.jacobvv.mediapicker.R;
import org.jacobvv.mediapicker.common.DropdownMenu;
import org.jacobvv.mediapicker.common.recycler.CommonCursorAdapter;
import org.jacobvv.mediapicker.data.model.Album;
import org.jacobvv.mediapicker.data.model.Image;
import org.jacobvv.mediapicker.data.model.Result;
import org.jacobvv.mediapicker.util.BarUtils;
import org.jacobvv.mediapicker.util.Constant;
import org.jacobvv.mediapicker.util.ContextWrap;
import org.jacobvv.mediapicker.util.PermissionManager;
import org.jacobvv.mediapicker.util.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity for pick pictures, include picture list & checkbox.
 *
 * @author Jacob
 * @date 17-11-23
 */
public abstract class BasePickerActivity extends AppCompatActivity implements PickerContract.View {

    private static final String TAG = "BasePickerActivity";

    protected PickerContract.Presenter mPresenter;
    protected Config mConfig;
    protected Image mCurrentImage;

    private ConstraintLayout mContent;
    private Toolbar mToolbar;
    private View mOverlay;
    private ProgressBar mProgressBar;

    private DropdownMenu mDropdownMenu;
    private DropdownAdapter mDropdownAdapter;
    private DropdownAnimation mDropdownAnimator;

    private RecyclerView mRecyclerView;
    protected CommonCursorAdapter<Image> mAdapter;

    private Runnable waitRunnable;

    private PermissionManager.PermissionRequestCallback callback =
            new PermissionManager.PermissionRequestCallback() {
                @Override
                public void onPermissionGranted() {
                    if (waitRunnable != null) {
                        waitRunnable.run();
                    }
                }

                @Override
                public void onPermissionDenied(List<String> permissions) {
                    Log.d(TAG, "Permission denied!");
                    Toast.makeText(getApplicationContext(), getString(R.string.permission),
                            Toast.LENGTH_SHORT).show();
                    for (String per : permissions) {
                        Log.d(TAG, "Permission: " + per);
                    }
                }
            };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init data & create presenter
        List<Image> selected;
        Album album = null;
        Intent intent = getIntent();
        if (savedInstanceState != null) {
            mConfig = savedInstanceState.getParcelable(Constant.STATE_BUNDLE_CONFIG);
            selected = savedInstanceState.getParcelableArrayList(Constant.STATE_BUNDLE_IMAGES_SELECTED);
            album = savedInstanceState.getParcelable(Constant.STATE_BUNDLE_ALBUM);
        } else {
            mConfig = intent.getParcelableExtra(Constant.INTENT_EXTRA_CONFIG);
            Result picked = intent.getParcelableExtra(Constant.INTENT_EXTRA_IMAGES);
            selected = (picked == null ? new ArrayList<Image>() : picked.getImages());
        }
        if (mConfig.getThemeId() != 0) {
            setTheme(mConfig.getThemeId());
        }
        mPresenter = new PickerPresenter(this, this, mConfig, selected);
        mPresenter.setAlbum(album);

        setContentView(R.layout.picker_act);

        // init view
        mContent = findViewById(R.id.picker_content);
        mToolbar = findViewById(R.id.picker_toolbar);
        mProgressBar = findViewById(R.id.picker_progress_bar);
        mRecyclerView = findViewById(R.id.picker_recycler_view);
        mDropdownMenu = findViewById(R.id.toolbar_dropdown_menu);

        // init dropdown menu
        if (mConfig.isEnableAlbumSelect()) {
            mOverlay = findViewById(R.id.picker_overlay);
            mOverlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDropdownMenu.getPopup().dismiss(true);
                }
            });
            mDropdownAnimator = new DropdownAnimation(getApplicationContext());
            mDropdownAdapter = new DropdownAdapter(getApplicationContext());
            mDropdownAdapter.setOnItemClickListener(new OnAlbumClickListener());
            mDropdownMenu.setVisibility(View.VISIBLE);
            mDropdownMenu.setPopupHelper(new CustomPopupHelper());
            mDropdownMenu.setAdapter(mDropdownAdapter);
        } else {
            findViewById(R.id.toolbar_title).setVisibility(View.VISIBLE);
        }
        // init toolbar
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        initToolBar(mToolbar);
        BarUtils.setStatusBarLightMode(this, true);
        BarUtils.setStatusBarColor(this, ContextCompat.getColor(this, android.R.color.white));
        // Init RecyclerView
        mAdapter = createAdapter();
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 4);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new ImageItemDecoration());
        mRecyclerView.setAdapter(mAdapter);
        mPresenter.start();
    }

    /**
     * Initial toolbar of this activity.
     *
     * @param toolbar toolbar object of this activity.
     */
    protected abstract void initToolBar(Toolbar toolbar);

    /**
     * Create adapter of recycler view of media item list.
     *
     * @return adapter of media item list.
     */
    protected abstract CommonCursorAdapter<Image> createAdapter();

    /**
     * Open the preview page of specific media item.
     *
     * @param item     specific media item
     * @param position position of this specific media item in list.
     */
    protected abstract void startPreviewActivity(Image item, int position);

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showContent() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showLoading() {
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateAlbums(List<Album> albums, int currentAlbumIndex) {
        mDropdownAdapter.setData(albums, currentAlbumIndex);
    }

    @Override
    public void updateMedia(Cursor cursor) {
        mAdapter.setCursor(cursor);
    }

    @Override
    public void returnResult(Result result) {
        Intent intent = new Intent();
        intent.putExtra(Constant.INTENT_EXTRA_IMAGES, result);
        setResult(RESULT_OK, intent);
        finish();
    }

    protected void takePhoto() {
        ContextWrap context = new ContextWrap(this);
        int result = PermissionManager.checkPermission(context,
                new String[]{Constant.PER_STORAGE, Constant.PER_CAMERA});
        if (result == PermissionManager.STATE_REQUEST) {
            waitRunnable = new Runnable() {
                @Override
                public void run() {
                    takePhoto();
                }
            };
            return;
        }
        try {
            File file = Utils.getNewImage(getApplicationContext(), mConfig.isPhotoSave());
            mCurrentImage = Image.fromPhoto(file);
            Utils.takePhoto(context, file);
        } catch (IOException e) {
            Intent intent = new Intent();
            intent.putExtra(Constant.INTENT_EXTRA_CODE, Constant.ERROR_CODE_CREATE_FAIL);
            intent.putExtra(Constant.INTENT_EXTRA_MESSAGE, e.getMessage());
            setResult(Constant.RESULT_ERROR, intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            if (mConfig.isPhotoSave()) {
                Utils.galleryAddPic(this, mCurrentImage.getUri());
            }
            mCurrentImage.setChecked(true);
            // Return directly from take photo in photo picker.
            mPresenter.updateMedia(mCurrentImage);
            if (mConfig.isEnableCaptureInPickReturn()) {
                mPresenter.addSelectItem(mCurrentImage);
                mPresenter.completePick();
                return;
            }
            mPresenter.addSelectItem(mCurrentImage);
            startPreviewActivity(mCurrentImage, 0);

            // Add photo into album list.
            Album album = mPresenter.getAlbum();
            String path = new File(mCurrentImage.getPath()).getParent();
            if (album.getId() == Album.BUCKET_ID_ALL || TextUtils.equals(album.getPath(), path)) {
                mAdapter.addData(album.isContainCapture() ? 1 : 0, mCurrentImage);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mDropdownMenu != null && mDropdownMenu.getPopup().isShowing()) {
            mDropdownMenu.getPopup().dismiss(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults, callback);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constant.STATE_BUNDLE_CONFIG, mConfig);
        outState.putParcelable(Constant.STATE_BUNDLE_ALBUM, mPresenter.getAlbum());
        outState.putParcelable(Constant.STATE_BUNDLE_IMAGE, mCurrentImage);
        outState.putParcelableArrayList(Constant.STATE_BUNDLE_IMAGES_SELECTED, mPresenter.getSelectItems());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentImage = savedInstanceState.getParcelable(Constant.STATE_BUNDLE_IMAGE);
    }

    private static class ImageItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(4, 4, 4, 4);
        }
    }

    private class CustomPopupHelper implements DropdownMenu.PopupHelper {

        @Override
        public void initMenu(View menu) {
            // Calculate height of menu.
            float itemHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100,
                    getResources().getDisplayMetrics());
            int menuHeight = (int) (itemHeight * 5);
            boolean isFixHeight = mDropdownAdapter.getData() != null
                    && mDropdownAdapter.getData().size() > 5;
            // Add view into ConstraintLayout.
            if (mContent.indexOfChild(menu) == -1) {
                mContent.addView(menu, Math.max(0, mContent.indexOfChild(mToolbar) - 1));
            }
            // Init constraint set of menu in ConstraintLayout.
            ConstraintSet con = new ConstraintSet();
            con.clone(mContent);
            con.connect(menu.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
            con.connect(menu.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
            con.connect(menu.getId(), ConstraintSet.TOP, mToolbar.getId(), ConstraintSet.BOTTOM, 0);
            con.constrainHeight(menu.getId(), isFixHeight ? menuHeight : ConstraintSet.WRAP_CONTENT);
            con.constrainWidth(menu.getId(), ConstraintSet.MATCH_CONSTRAINT);
            con.applyTo(mContent);
        }

        @Override
        public void show(View menu, boolean animated) {
            mDropdownAnimator.startAnimationToShow(animated, menu, mOverlay);
        }

        @Override
        public void dismiss(View menu, boolean animated) {
            mDropdownAnimator.startAnimationToDismiss(animated, menu, mOverlay);
        }
    }

    private class OnAlbumClickListener implements DropdownMenu.OnItemClickListener<Album> {

        @Override
        public void onItemSelected(DropdownMenu.BaseAdapter<Album> adapter, Album model, int position) {
            mPresenter.getAlbum().setChecked(false);
            mPresenter.setAlbum(model);
            model.setChecked(true);
            mPresenter.loadMedia(model.getId());
            adapter.notifyDataSetChanged();
        }
    }
}
