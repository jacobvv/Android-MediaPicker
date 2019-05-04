package org.jacobvv.mediapicker.picker.multi;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.SparseArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import org.jacobvv.mediapicker.Config;
import org.jacobvv.mediapicker.R;
import org.jacobvv.mediapicker.common.recycler.BaseRecyclerViewHolder;
import org.jacobvv.mediapicker.common.recycler.BaseTypeFactory;
import org.jacobvv.mediapicker.data.model.Image;
import org.jacobvv.mediapicker.picker.PickerContract;

import static org.jacobvv.mediapicker.common.recycler.BaseRecyclerViewHolder.OnItemClickListener;

/**
 * @author Jacob
 * @date 18-4-3
 */
public class MultiTypeFactory implements BaseTypeFactory<Image> {

    public static final int ITEM_CAMERA_LAYOUT_ID = R.layout.item_image;
    public static final int ITEM_IMAGE_LAYOUT_ID = R.layout.item_image_check;

    private PickerContract.Presenter mPresenter;
    private Config mConfig;
    private SparseArray<SparseArray<OnItemClickListener<Image>>> mListeners = new SparseArray<>();

    public MultiTypeFactory(PickerContract.Presenter presenter, Config config) {
        this.mPresenter = presenter;
        this.mConfig = config;
    }

    public void registerListener(int type, SparseArray<OnItemClickListener<Image>> listeners) {
        mListeners.put(type, listeners);
    }

    @Override
    public int getLayoutId(int type) {
        if (type == 0) {
            return ITEM_CAMERA_LAYOUT_ID;
        } else {
            return ITEM_IMAGE_LAYOUT_ID;
        }
    }

    @Override
    public BaseRecyclerViewHolder<Image> createViewHolder(int viewType, View itemView) {
        SparseArray<OnItemClickListener<Image>> listeners = mListeners.get(viewType);
        if (listeners == null) {
            throw new IllegalArgumentException("Listeners of type are not registered.");
        }
        if (viewType == 0) {
            return new CameraViewHolder(itemView, listeners);
        } else {
            ImageViewHolder holder = new ImageViewHolder(itemView, listeners);
            if (mConfig.getCheckbox() != 0) {
                CheckBox checkBox = holder.getView(R.id.item_check_box);
                checkBox.setButtonDrawable(mConfig.getCheckbox());
            }
            return holder;
        }
    }

    class CameraViewHolder extends BaseRecyclerViewHolder<Image> {

        CameraViewHolder(View v, SparseArray<OnItemClickListener<Image>> listeners) {
            super(v, listeners);
        }

        @Override
        public void setUpView(Image model, int position) {
            ImageView image = getView(R.id.item_image);
            image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            image.setImageResource(R.drawable.picker_camera);
        }
    }


    class ImageViewHolder extends BaseRecyclerViewHolder<Image> {

        ImageViewHolder(View v, SparseArray<OnItemClickListener<Image>> listeners) {
            super(v, listeners);
        }

        @Override
        public void setUpView(Image model, int position) {
            model.setChecked(mPresenter.containSelectItem(model));
            ImageView image = getView(R.id.item_image);
            CheckBox check = getView(R.id.item_check_box);
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(mContext)
                    .load(model.getUri())
                    .into(image);
            check.setChecked(model.isChecked());
            check.setClickable(false);
            if (model.isChecked()) {
                image.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            } else {
                image.setColorFilter(0xFFEEEEEE, PorterDuff.Mode.MULTIPLY);
            }
        }
    }
}
