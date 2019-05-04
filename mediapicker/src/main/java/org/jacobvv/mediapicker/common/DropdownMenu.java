package org.jacobvv.mediapicker.common;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.jacobvv.mediapicker.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Jacob
 * @date 18-1-22
 */
public class DropdownMenu extends FrameLayout implements View.OnClickListener {

    private BaseAdapter mAdapter;
    private BaseViewHolder mTitle;

    private InternalMenuPopup mPopup;

    public DropdownMenu(@NonNull Context context) {
        this(context, null);
    }

    public DropdownMenu(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropdownMenu(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPopup = new CustomPopup(getContext());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DropdownMenu(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mPopup = new CustomPopup(getContext());
    }

    public void setAdapter(BaseAdapter adapter) {
        mAdapter = adapter;
        if (mTitle != null) {
            removeView(mTitle.itemView);
        }
        mTitle = adapter.getTitle();
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        mPopup.setAdapter(adapter);
        if (mAdapter.getData() != null && !mAdapter.getData().isEmpty()) {
            adapter.setCurrent(0);
        }
        addView(mTitle.itemView, lp);
        setOnClickListener(this);
    }

    public void setSelection(int position) {
        mAdapter.setCurrent(position);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mPopup.dismiss(false);
    }

    @Override
    public void onClick(View v) {
        if (mPopup.isShowing()) {
            mPopup.dismiss(true);
        } else {
            mPopup.show(true);
        }
    }

    public void setPopupHelper(PopupHelper helper) {
        mPopup.setPopupHelper(helper);
    }

    public MenuPopup getPopup() {
        return mPopup;
    }

    public interface MenuPopup {
        /**
         * Show the popup
         */
        void show(boolean animated);

        /**
         * Dismiss the popup
         */
        void dismiss(boolean animated);

        /**
         * @return true if the popup is showing, false otherwise.
         */
        boolean isShowing();
    }

    /**
     * Implements some sort of popup selection interface for selecting a menu option.
     * Allows for different dropdown menu modes.
     */
    private interface InternalMenuPopup extends MenuPopup {
        void setAdapter(BaseAdapter adapter);

        void setPopupHelper(PopupHelper helper);

        void initMenu();
    }

    public interface PopupHelper {
        void initMenu(View menu);

        void show(View menu, boolean animated);

        void dismiss(View menu, boolean animated);
    }

    public interface OnItemClickListener<T> {
        void onItemSelected(BaseAdapter<T> adapter, T model, int position);
    }

    private static class CustomPopup implements InternalMenuPopup {

        boolean isShowing;
        private PopupHelper mHelper;
        private RecyclerView mRecyclerView;

        @SuppressLint("InflateParams")
        private CustomPopup(Context context) {
            mRecyclerView = (RecyclerView) LayoutInflater.from(context).inflate(
                    R.layout.dropdown_menu, null);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        }

        @Override
        public void setAdapter(BaseAdapter adapter) {
            mRecyclerView.setAdapter(adapter);
            adapter.setPopup(this);
        }

        @Override
        public void setPopupHelper(PopupHelper helper) {
            mHelper = helper;
            initMenu();
        }

        @Override
        public void initMenu() {
            mHelper.initMenu(mRecyclerView);
            isShowing = false;
            mHelper.dismiss(mRecyclerView, false);
        }

        @Override
        public void show(boolean animated) {
            if (mHelper != null && !isShowing) {
                mHelper.show(mRecyclerView, animated);
                isShowing = true;
            }
        }

        @Override
        public void dismiss(boolean animated) {
            if (mHelper != null && isShowing) {
                mHelper.dismiss(mRecyclerView, animated);
                isShowing = false;
            }
        }

        @Override
        public boolean isShowing() {
            return isShowing;
        }
    }

    public static abstract class BaseAdapter<T>
            extends RecyclerView.Adapter<BaseViewHolder<T>> {

        OnItemClickListener<T> mListener;
        private InternalMenuPopup mPopup;
        @LayoutRes
        private int mItemRes;
        private BaseViewHolder<T> mTitleHolder;
        private List<T> mData = new ArrayList<>();
        private T mCurrent;

        protected BaseAdapter(Context context, @LayoutRes int titleRes, @LayoutRes int itemRes) {
            this(context, titleRes, itemRes, null);
        }

        protected BaseAdapter(Context context, @LayoutRes int titleRes, @LayoutRes int itemRes,
                              List<T> data) {
            View title = LayoutInflater.from(context).inflate(titleRes, null);
            mTitleHolder = createTitle(title);
            this.mItemRes = itemRes;
            if (data != null && data.size() > 0) {
                this.mData = data;
            }
        }

        private void setPopup(InternalMenuPopup popup) {
            this.mPopup = popup;
        }

        @Override
        public BaseViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(mItemRes, parent, false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(lp);
            return createItem(itemView);
        }

        @Override
        public void onBindViewHolder(BaseViewHolder<T> holder, int position) {
            T model = mData.get(position);
            holder.setUpView(model, position);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public BaseViewHolder<T> getTitle() {
            return mTitleHolder;
        }

        public List<T> getData() {
            return mData;
        }

        public void setData(@NonNull Collection<? extends T> data) {
            setData(data, 0);
        }

        public void setData(@NonNull Collection<? extends T> data, int selection) {
            mData.clear();
            mData.addAll(data);
            mPopup.initMenu();
            if (mData.size() > selection) {
                setCurrent(selection);
            }
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

        public void addData(@IntRange(from = 0) int position, @NonNull Collection<? extends T> data) {
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

        public T getCurrent() {
            return mCurrent;
        }

        public void setCurrent(int pos) {
            if (mCurrent != null && mCurrent.equals(mData.get(pos))) {
                return;
            }
            mCurrent = mData.get(pos);
            mTitleHolder.setUpView(mCurrent, pos);
            // Invoke callback of on item click.
            if (mListener != null) {
                mListener.onItemSelected(this, mCurrent, pos);
            }
        }

        protected abstract BaseTitleViewHolder<T> createTitle(View view);

        protected abstract BaseItemViewHolder<T> createItem(View view);

        OnItemClickListener<T> getOnItemClickListener() {
            return mListener;
        }

        public void setOnItemClickListener(OnItemClickListener<T> listener) {
            mListener = listener;
        }

    }

    public static abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

        protected Context mContext;
        private SparseArray<View> mViews = new SparseArray<>();

        BaseViewHolder(View v) {
            super(v);
            mContext = v.getContext();
        }

        @SuppressWarnings("unchecked")
        protected <V extends View> V getView(int resId) {
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

        public abstract void setUpView(T model, int position);
    }

    public static abstract class BaseTitleViewHolder<T> extends BaseViewHolder<T> {

        protected BaseTitleViewHolder(View v) {
            super(v);
        }
    }

    public static abstract class BaseItemViewHolder<T> extends BaseViewHolder<T> {
        protected BaseItemViewHolder(View v, final BaseAdapter<T> adapter) {
            super(v);
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get model at this position.
                    int pos = getLayoutPosition();
                    // Update current in adapter.
                    adapter.setCurrent(pos);
                    // Dismiss popup
                    adapter.mPopup.dismiss(true);
                }
            });
        }
    }
}
