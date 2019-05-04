package org.jacobvv.mediapicker.picker.multi;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.jacobvv.mediapicker.R;
import org.jacobvv.mediapicker.common.recycler.BaseRecyclerViewHolder;
import org.jacobvv.mediapicker.common.recycler.CommonCursorAdapter;
import org.jacobvv.mediapicker.data.model.Image;
import org.jacobvv.mediapicker.picker.BasePickerActivity;
import org.jacobvv.mediapicker.preview.MultiPreviewActivity;
import org.jacobvv.mediapicker.util.Constant;
import org.jacobvv.mediapicker.util.Utils;

import java.util.ArrayList;

public class MultiPickerActivity extends BasePickerActivity {

    private static final String TAG = "MultiPickerActivity";
    private TextView mBtnDown;

    @Override
    protected void initToolBar(Toolbar toolbar) {
        mBtnDown = (TextView) findViewById(R.id.toolbar_btn_ok);
        mBtnDown.setVisibility(View.VISIBLE);
        mBtnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.completePick();
            }
        });
        updateDownBtn();
    }

    @Override
    protected CommonCursorAdapter<Image> createAdapter() {
        MultiTypeFactory factory = new MultiTypeFactory(mPresenter, mConfig);
        SparseArray<BaseRecyclerViewHolder.OnItemClickListener<Image>> imageListeners = new SparseArray<>();
        imageListeners.put(R.id.item_image, new OnCameraClickListener());
        factory.registerListener(0, imageListeners);

        SparseArray<BaseRecyclerViewHolder.OnItemClickListener<Image>> checkableImageListeners = new SparseArray<>();
        checkableImageListeners.put(R.id.item_image, new OnImageClickListener());
        checkableImageListeners.put(R.id.item_check_region, new OnImageCheckListener());
        factory.registerListener(1, checkableImageListeners);
        return new CommonCursorAdapter<>(factory, new MultiModelFactory());
    }

    @Override
    protected void startPreviewActivity(Image item, int position) {
        Intent intent = new Intent(getApplicationContext(), MultiPreviewActivity.class);
        intent.putExtra(Constant.INTENT_EXTRA_CONFIG, mConfig);
        intent.putExtra(Constant.INTENT_EXTRA_ALBUM, mPresenter.getAlbum());
        intent.putExtra(Constant.INTENT_EXTRA_IMAGE, item);
        intent.putExtra(Constant.INTENT_EXTRA_POSITION, position);
        intent.putParcelableArrayListExtra(Constant.INTENT_EXTRA_IMAGES, mPresenter.getSelectItems());
        startActivityForResult(intent, Constant.REQUEST_MULTI_PREVIEW);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_MULTI_PREVIEW && data != null) {
            ArrayList<Image> selected = data.getParcelableArrayListExtra(Constant.INTENT_EXTRA_IMAGES);
            mPresenter.setSelectItems(selected);
            mAdapter.notifyDataSetChanged();
            updateDownBtn();
        }
    }

    private void updateDownBtn() {
        mBtnDown.setText(getString(R.string.btn_text_down_count, Utils.getCheckedNum(
                mPresenter.getSelectSize(), mConfig.getPickLimit())));
        if (mPresenter.getSelectSize() == 0) {
            mBtnDown.setClickable(false);
            mBtnDown.setTextColor(Color.GRAY);
        } else {
            mBtnDown.setClickable(true);
            mBtnDown.setTextColor(Color.BLACK);
        }
    }

    private class OnCameraClickListener implements BaseRecyclerViewHolder.OnItemClickListener<Image> {

        @Override
        public void onClick(BaseRecyclerViewHolder<Image> holder, Image model, int position) {
            if (mConfig.getPickLimit() != 0
                    && mPresenter.getSelectSize() >= mConfig.getPickLimit()) {
                Log.w(TAG, "Image selection reached the limit! Choose up to "
                        + mConfig.getPickLimit() + " pictures.");
                Toast.makeText(MultiPickerActivity.this,
                        getString(R.string.error_limit, mConfig.getPickLimit()),
                        Toast.LENGTH_SHORT).show();
            } else {
                takePhoto();
            }
        }
    }

    private class OnImageClickListener implements BaseRecyclerViewHolder.OnItemClickListener<Image> {

        @Override
        public void onClick(BaseRecyclerViewHolder<Image> holder, Image model, int position) {
            startPreviewActivity(null, position);
        }
    }

    private class OnImageCheckListener implements BaseRecyclerViewHolder.OnItemClickListener<Image> {

        @Override
        public void onClick(BaseRecyclerViewHolder<Image> holder, Image model, int position) {
            if (!model.isChecked()
                    && mConfig.getPickLimit() != 0
                    && mPresenter.getSelectSize() >= mConfig.getPickLimit()) {
                Log.w(TAG, "Image selection reached the limit! Choose up to "
                        + mConfig.getPickLimit() + " pictures.");
                Toast.makeText(MultiPickerActivity.this,
                        getString(R.string.error_limit, mConfig.getPickLimit()),
                        Toast.LENGTH_SHORT).show();
            } else {
                model.setChecked(!model.isChecked());
                if (model.isChecked()) {
                    mPresenter.addSelectItem(model);
                } else {
                    mPresenter.removeSelectItem(model);
                }
                updateDownBtn();
            }
            holder.setUpView(model, position);
        }
    }

}
