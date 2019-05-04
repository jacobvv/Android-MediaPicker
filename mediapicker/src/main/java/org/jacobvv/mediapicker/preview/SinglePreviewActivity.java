package org.jacobvv.mediapicker.preview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import org.jacobvv.mediapicker.Config;
import org.jacobvv.mediapicker.R;
import org.jacobvv.mediapicker.data.model.Image;
import org.jacobvv.mediapicker.util.BarUtils;
import org.jacobvv.mediapicker.util.CompressAsyncTask;
import org.jacobvv.mediapicker.util.Constant;
import org.jacobvv.mediapicker.util.OnCompressListener;
import org.jacobvv.mediapicker.util.Utils;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.List;

/**
 * @author Jacob
 */
public class SinglePreviewActivity extends AppCompatActivity {

    private static final String TAG = "SinglePreviewActivity";

    private CropImageView mCropImageView;
    private TextView mBtnDown;

    private Image mImageItem;
    private Config mConfig;

    private OnCompressListener mCompressCallback = new OnCompressCallback();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            mConfig = intent.getParcelableExtra(Constant.INTENT_EXTRA_CONFIG);
            mImageItem = intent.getParcelableExtra(Constant.INTENT_EXTRA_IMAGE);
        } else {
            mConfig = savedInstanceState.getParcelable(Constant.STATE_BUNDLE_CONFIG);
            mImageItem = savedInstanceState.getParcelable(Constant.STATE_BUNDLE_IMAGE);
        }
        if (mConfig.getThemeId() != 0) {
            setTheme(mConfig.getThemeId());
        }

        setContentView(R.layout.preview_single_act);

        initToolBar();
        initContent();
    }

    private void initToolBar() {
        BarUtils.setStatusBarLightMode(this, true);
        BarUtils.setStatusBarColor(this, ContextCompat.getColor(this, android.R.color.white));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        mBtnDown = (TextView) findViewById(R.id.toolbar_btn_ok);
        mBtnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                down(null);
            }
        });
        mBtnDown.setText(getString(R.string.btn_text_down));
        mBtnDown.setEnabled(!mConfig.isEnableClip());
    }

    private void initContent() {
        if (mConfig.isEnableClip()) {
            mCropImageView = (CropImageView) findViewById(R.id.preview_crop);
            CompressAsyncTask task = new CompressAsyncTask(this, mCompressCallback);
            task.execute(mImageItem);
            mCropImageView.setVisibility(View.VISIBLE);
            mCropImageView.setImageUriAsync(mImageItem.getUri());
            if (mConfig.getClipType() == Config.CLIP_RECT) {
                mCropImageView.setCropShape(CropImageView.CropShape.RECTANGLE);
            } else if (mConfig.getClipType() == Config.CLIP_CIRCLE) {
                mCropImageView.setCropShape(CropImageView.CropShape.OVAL);
            }
            if (mConfig.getClipRatio() > 0) {
                mCropImageView.setAspectRatio(100, (int) (mConfig.getClipRatio() * 100));
            }
            mCropImageView.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
                @Override
                public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
                    down(result.getUri());
                }
            });
        } else {
            PhotoView imageView = (PhotoView) findViewById(R.id.preview);
            imageView.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(mImageItem.getUri())
                    .into(imageView);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constant.STATE_BUNDLE_CONFIG, mConfig);
        outState.putParcelable(Constant.STATE_BUNDLE_IMAGE, mImageItem);
    }

    @Override
    public void onBackPressed() {
        cancel();
    }

    private void down(Uri result) {
        if (!mConfig.isEnableClip()) {
            Intent intent = new Intent();
            intent.putExtra(Constant.INTENT_EXTRA_IMAGE, mImageItem);
            setResult(RESULT_OK, intent);
            finish();
        } else if (result != null) {
            if (mConfig.isPhotoSave()) {
                Utils.galleryAddPic(this, result);
            }
            Intent intent = new Intent();
            intent.putExtra(Constant.INTENT_EXTRA_IMAGE, Image.fromPath(result));
            setResult(RESULT_OK, intent);
            finish();
        } else {
            try {
                mCropImageView.saveCroppedImageAsync(Uri.fromFile(
                        Utils.getNewImage(this, mConfig.isPhotoSave())));
            } catch (IOException e) {
                Intent intent = new Intent();
                intent.putExtra(Constant.INTENT_EXTRA_CODE, Constant.ERROR_CODE_CREATE_FAIL);
                intent.putExtra(Constant.INTENT_EXTRA_MESSAGE, e.getMessage());
                setResult(Constant.RESULT_ERROR, intent);
                finish();
            }
        }
    }

    private void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private class OnCompressCallback implements OnCompressListener {
        @Override
        public void onStart() {
            Log.d(TAG, "Compress start.");
        }

        @Override
        public void onSuccess(List<Image> images) {
            Log.d(TAG, "Compress SUCCESS.");
            mCropImageView.setImageUriAsync(images.get(0).getCompressed());
            mBtnDown.setEnabled(true);
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "Image compress error: " + e.getLocalizedMessage());
            e.printStackTrace();
            mBtnDown.setEnabled(true);
        }
    }

}
