package org.jacobvv.mediapicker.preview;

import android.database.Cursor;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import org.jacobvv.mediapicker.Config;
import org.jacobvv.mediapicker.R;
import org.jacobvv.mediapicker.data.model.Image;

import java.util.LinkedHashSet;

/**
 * @author Jacob
 * @date 17-12-28
 */
public class MultiPreviewAdapter extends PagerAdapter {

    private Config mConfig;
    private Cursor mCursor;
    private OnItemCheckListener mListener;
    private LinkedHashSet<Image> mSelectImages;

    MultiPreviewAdapter(Config config, OnItemCheckListener listener, Cursor cursor, LinkedHashSet<Image> selected) {
        mConfig = config;
        mListener = listener;
        mCursor = cursor;
        this.mSelectImages = selected;
    }

    @Override
    public int getCount() {
        if (!isDataValid(mCursor)) {
            return 0;
        } else {
            return mCursor.getCount();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        checkDataValid(position);
        View item = LayoutInflater.from(container.getContext())
                .inflate(R.layout.preview_multi_page, container, false);
        PhotoView photo = (PhotoView) item.findViewById(R.id.preview);
        final CheckBox checkBox = (CheckBox) item.findViewById(R.id.preview_checkbox);
        View placeHolder = item.findViewById(R.id.preview_check_region);

        Image image = Image.fromCursor(mCursor);
        image.setChecked(mSelectImages.contains(image));
        Glide.with(container.getContext())
                .load(image.getUri())
                .into(photo);
        checkBox.setChecked(image.isChecked());
        checkBox.setClickable(false);
        if (mConfig.getCheckbox() != 0) {
            checkBox.setButtonDrawable(mConfig.getCheckbox());
        }
        placeHolder.setTag(image);
        placeHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.setChecked(!checkBox.isChecked());
                if (mListener == null) {
                    return;
                }
                mListener.onCheck(checkBox, (Image) v.getTag());
            }
        });
        container.addView(item);
        return item;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void setCursor(Cursor cursor) {
        if (cursor == mCursor) {
            return;
        }
        if (cursor == null && isDataValid(mCursor)) {
            mCursor.close();
        }
        mCursor = cursor;
        notifyDataSetChanged();
    }

    private void checkDataValid(int position) {
        if (!isDataValid(mCursor)) {
            throw new IllegalStateException(
                    "Cannot lookup item id because the cursor is in invalid state.");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("Could not move cursor to position " + position
                    + " when trying to get an item id");
        }
    }

    private boolean isDataValid(Cursor cursor) {
        return cursor != null && !cursor.isClosed();
    }

    public interface OnItemCheckListener {
        void onCheck(CheckBox checkBox, Image image);
    }

}
