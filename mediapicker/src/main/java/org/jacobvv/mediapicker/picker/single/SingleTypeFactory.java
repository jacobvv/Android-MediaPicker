package org.jacobvv.mediapicker.picker.single;

import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import org.jacobvv.mediapicker.R;
import org.jacobvv.mediapicker.common.recycler.BaseRecyclerViewHolder;
import org.jacobvv.mediapicker.common.recycler.BaseTypeFactory;
import org.jacobvv.mediapicker.data.model.Image;

/**
 * @author Jacob
 * @date 18-4-3
 */
public class SingleTypeFactory implements BaseTypeFactory<Image> {

    public static final int ITEM_LAYOUT_ID = R.layout.item_image;

    private SparseArray<BaseRecyclerViewHolder.OnItemClickListener<Image>> mListeners;

    public SingleTypeFactory(SparseArray<BaseRecyclerViewHolder.OnItemClickListener<Image>> listeners) {
        this.mListeners = listeners;
    }

    @Override
    public int getLayoutId(int type) {
        return ITEM_LAYOUT_ID;
    }

    @Override
    public BaseRecyclerViewHolder<Image> createViewHolder(int viewType, View itemView) {
        return new ImageViewHolder(itemView, mListeners);
    }

    class ImageViewHolder extends BaseRecyclerViewHolder<Image> {

        ImageViewHolder(View v, SparseArray<OnItemClickListener<Image>> listeners) {
            super(v, listeners);
        }

        @Override
        public void setUpView(Image model, int position) {
            ImageView image = getView(R.id.item_image);
            if (model.getType() == 0) {
                image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                image.setImageResource(R.drawable.picker_camera);
            } else {
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(mContext)
                        .load(model.getUri())
                        .into(image);
            }
        }
    }
}
