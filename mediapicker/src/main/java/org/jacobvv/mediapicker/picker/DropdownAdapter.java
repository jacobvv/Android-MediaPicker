package org.jacobvv.mediapicker.picker;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import org.jacobvv.mediapicker.R;
import org.jacobvv.mediapicker.common.DropdownMenu;
import org.jacobvv.mediapicker.data.model.Album;

/**
 * Created by Jacob on 18-1-17.
 */

public class DropdownAdapter extends DropdownMenu.BaseAdapter<Album> {

    protected DropdownAdapter(Context context) {
        super(context, R.layout.album_dropdown_title, R.layout.album_dropdown_item);
    }

    @Override
    protected DropdownMenu.BaseTitleViewHolder<Album> createTitle(View view) {
        return new TitleHolder(view);
    }

    @Override
    protected DropdownMenu.BaseItemViewHolder<Album> createItem(View view) {
        return new ItemHolder(view, this);
    }

    public static class TitleHolder extends DropdownMenu.BaseTitleViewHolder<Album> {

        TitleHolder(View v) {
            super(v);
        }

        @Override
        public void setUpView(Album model, int position) {
            TextView title = getView(R.id.album_title_tv);
            if (model.getId() == Album.BUCKET_ID_ALL) {
                title.setText(mContext.getString(R.string.pick_title_def));
            } else {
                title.setText(model.getName());
            }
        }

    }

    public static class ItemHolder extends DropdownMenu.BaseItemViewHolder<Album> {

        ItemHolder(View v, DropdownMenu.BaseAdapter<Album> adapter) {
            super(v, adapter);
        }

        @Override
        public void setUpView(Album model, int position) {
            ImageView thumbnail = getView(R.id.album_thumbnail_iv);
            TextView title = getView(R.id.album_title_tv);
            TextView count = getView(R.id.album_num_tv);
            ImageView check = getView(R.id.album_check_iv);
            if (model.getCover() == null) {
                thumbnail.setImageResource(R.drawable.album_dropdown_placeholder);
            } else {
                Glide.with(mContext)
                        .load(model.getCover())
                        .into(thumbnail);
            }
            if (model.getId() == Album.BUCKET_ID_ALL) {
                title.setText(R.string.pick_title_def);
            } else {
                title.setText(model.getName());
            }
            count.setText(mContext.getString(R.string.pick_album_num, model.getCount()));
            check.setVisibility(model.isChecked() ? View.VISIBLE : View.GONE);
        }
    }
}
