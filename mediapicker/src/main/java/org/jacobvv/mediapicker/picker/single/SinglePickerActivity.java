package org.jacobvv.mediapicker.picker.single;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;

import org.jacobvv.mediapicker.R;
import org.jacobvv.mediapicker.common.recycler.BaseRecyclerViewHolder;
import org.jacobvv.mediapicker.common.recycler.CommonCursorAdapter;
import org.jacobvv.mediapicker.data.model.Image;
import org.jacobvv.mediapicker.picker.BasePickerActivity;
import org.jacobvv.mediapicker.preview.SinglePreviewActivity;
import org.jacobvv.mediapicker.util.Constant;

public class SinglePickerActivity extends BasePickerActivity {

    @SuppressWarnings("unused")
    private static final String TAG = "SinglePickerActivity";

    @Override
    protected void initToolBar(Toolbar toolbar) {
    }

    @Override
    protected CommonCursorAdapter<Image> createAdapter() {
        SparseArray<BaseRecyclerViewHolder.OnItemClickListener<Image>> imageListeners = new SparseArray<>();
        imageListeners.put(R.id.item_image, new OnImageClickListener());
        SingleTypeFactory factory = new SingleTypeFactory(imageListeners);
        return new CommonCursorAdapter<>(factory, new SingleModelFactory());
    }

    @Override
    protected void startPreviewActivity(Image item, int position) {
        Intent intent = new Intent(getApplicationContext(), SinglePreviewActivity.class);
        intent.putExtra(Constant.INTENT_EXTRA_IMAGE, item);
        intent.putExtra(Constant.INTENT_EXTRA_CONFIG, mConfig);
        startActivityForResult(intent, Constant.REQUEST_SINGLE_PREVIEW);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_SINGLE_PREVIEW) {
            if (resultCode == Constant.RESULT_ERROR) {
                setResult(Constant.RESULT_ERROR, data);
                finish();
                return;
            }
            if (resultCode == Activity.RESULT_OK) {
                mCurrentImage = data.getParcelableExtra(Constant.INTENT_EXTRA_IMAGE);
                mCurrentImage.setChecked(true);
                mPresenter.clearSelectItem();
                mPresenter.addSelectItem(mCurrentImage);
                mPresenter.completePick();
            }
        }
    }

    private class OnImageClickListener implements BaseRecyclerViewHolder.OnItemClickListener<Image> {

        @Override
        public void onClick(BaseRecyclerViewHolder<Image> holder, Image model, int position) {
            if (model.getFrom() == Image.FROM_NONE) {
                takePhoto();
            } else {
                mCurrentImage = model;
                startPreviewActivity(model, position);
            }
        }
    }

}
