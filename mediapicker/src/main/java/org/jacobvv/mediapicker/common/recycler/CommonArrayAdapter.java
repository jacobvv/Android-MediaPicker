package org.jacobvv.mediapicker.common.recycler;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Jacob on 18-4-3.
 */

public class CommonArrayAdapter<T extends BaseItemModel> extends BaseRecyclerAdapter<T> {

    private List<T> mData = new ArrayList<>();

    protected CommonArrayAdapter(BaseTypeFactory<T> factory) {
        super(factory);
    }

    protected CommonArrayAdapter(BaseTypeFactory<T> factory, List<T> data) {
        super(factory);
        mData.addAll(data);
    }

    @Override
    protected T getItem(int pos) {
        return mData.get(pos);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public List<T> getData() {
        return mData;
    }

    public void setData(@NonNull Collection<? extends T> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(@NonNull T data) {
        mData.add(data);
        if (mData.size() == 1) {
            notifyDataSetChanged();
        } else {
            notifyItemInserted(mData.size());
        }
    }

    public void addData(@IntRange(from = 0) int position, @NonNull T data) {
        mData.add(position, data);
        if (mData.size() == 1) {
            notifyDataSetChanged();
        } else {
            notifyItemInserted(position);
        }
    }

    public void addData(@NonNull Collection<? extends T> data) {
        mData.addAll(data);
        if (mData.size() == data.size()) {
            notifyDataSetChanged();
        } else {
            notifyItemRangeInserted(mData.size() - data.size(), data.size());
        }
    }

    public void addData(@IntRange(from = 0) int position,
                        @NonNull Collection<? extends T> data) {
        mData.addAll(position, data);
        if (mData.size() == data.size()) {
            notifyDataSetChanged();
        } else {
            notifyItemRangeInserted(position, data.size());
        }
    }

    public void remove(@IntRange(from = 0) int position) {
        mData.remove(position);
        if (mData.isEmpty()) {
            notifyDataSetChanged();
        } else {
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mData.size() - position);
        }
    }
}
