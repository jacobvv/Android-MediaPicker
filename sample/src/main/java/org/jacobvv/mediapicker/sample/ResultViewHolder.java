package org.jacobvv.mediapicker.sample;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.jacobvv.mediapicker.data.model.Image;

public class ResultViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mViews = new SparseArray<>();

    public ResultViewHolder(View itemView) {
        super(itemView);
    }

    @SuppressWarnings("unchecked")
    public <V extends View> V getView(int resId) {
        View view = mViews.get(resId);
        if (view == null) {
            view = itemView.findViewById(resId);
            if (view == null) {
                return null;
            }
            mViews.put(resId, view);
        }
        return (V) view;
    }

    public void setUpView(Image model) {
        ImageView thumbnail = getView(R.id.result_image_thumbnail);
        TextView title = getView(R.id.result_title);
        TextView subTitle = getView(R.id.result_sub_title);
        TextView uri = getView(R.id.result_uri);
        TextView path = getView(R.id.result_path);
        TextView compress = getView(R.id.result_compress);

        Glide.with(itemView.getContext())
                .load(model.getUri())
                .into(thumbnail);
        String subTitleStr = model.getMimeType()
                + " size=" + model.getSize()
                + " duration=" + model.getDuration();
        title.setText(model.getName());
        subTitle.setText(subTitleStr);
        uri.setText(model.getUri().toString());
        path.setText(model.getPath());
        String compressStr = "Compress=" + (model.isCompressed() ? model.getCompressed().toString() : "None");
        compress.setText(compressStr);
    }

}
