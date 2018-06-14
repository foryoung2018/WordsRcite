package com.example.libraryvideo.view;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by huizai on 2017/12/7.
 */

public abstract class RecyclerBaseAdapter<T> extends RecyclerView.Adapter<ABRecyclerViewHolder> {

    protected boolean isNeedShowEffect = false;

    public interface Item {
        int TYPE_HEADER = 0x00;
        int TYPE_FOOTER = 0x01;
        int TYPE_NORMAL = 0x02;
        int TYPE_SEPERATION = 0x09;
        int TYPE_FOOTER_ACTION = 0x03;
        int TYPE_VIEWPAGER = 0x04;
        int TYPE_SOURCE = 0x05;
        int TYPE_SEARCH = 0x06;
        int TYPE_ACTIVITY = 0x07;
        int TYPE_RECOMMEND_CAR = 0x08;
    }

    private Activity mActivity;
    protected List<T> list = null;

   // protected LoadingFooterView footerView;
    protected boolean hasHeader = false;
    protected boolean hasFooter = false;
    protected boolean hasFooterAction = false;
    protected OnItemClickListener onItemClickListener;

    protected int choosePosition = 0;

    public void setChoosePosition(int choosePosition) {
        this.choosePosition = choosePosition;
    }

    public int getChoosePosition() {
        return choosePosition;
    }

    public RecyclerBaseAdapter(Activity mActivity, List<T> list) {
        this.mActivity = mActivity;
        this.list = list;
    }

    public void setNeedShowEffect(boolean needShowEffect) {
        isNeedShowEffect = needShowEffect;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public boolean isHeader(int position) {
        return hasHeader() && position == 0;
    }

    public boolean isFooter(int position) {
        if (hasHeader()) {
            return hasFooter() && position == list.size() + 1;
        } else {
            return hasFooter() && position == list.size();
        }
    }

    public View getFooterView() {
        return null;
    }

//    public LoadingFooterView getLoadingFooterView() {
//        return footerView;
//    }

    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }

//    public void setFooterView(LoadingFooterView footerView) {
//        if (footerView != null) {
//            this.hasFooter = true;
//            this.footerView = footerView;
//        }
//    }

    /**
     * 可见状态
     * @param state
     */
    public void setFooterViewState(boolean state) {
       // if (footerView != null) {
            if (state){
                this.hasFooter = true;
              //  footerView.getLoadingFooterView().setVisibility(View.VISIBLE);
            } else {
                this.hasFooter = false;
             //   footerView.getLoadingFooterView().setVisibility(View.GONE);
            }
      //  }
    }

    public void setFooterActionView(boolean flag) {
        this.hasFooterAction = flag;
    }

    public boolean hasHeader() {
        return hasHeader;
    }

    public boolean hasFooter() {
        return hasFooter;
    }

    public boolean hasFooterAction() {
        return hasFooterAction;
    }

    public int getHeaderCount() {
        return hasHeader() ? 1 : 0;
    }

    public int getFooterCount() {
        return hasFooter() ? 1 : 0;
    }

    @Override
    public ABRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Item.TYPE_FOOTER){
            return  null;//new FootViewHolder(footerView.getLoadingFooterView());
        } else {
            return onCreateHolder(parent, viewType);
        }
    }

    public abstract ABRecyclerViewHolder onCreateHolder(ViewGroup parent, int viewType);

    protected void onBindFooterView(View footerView){}

    @Override
    public void onBindViewHolder(ABRecyclerViewHolder holder, int position) {
        if (getItemViewType(position) == Item.TYPE_HEADER) {
            holder.onBindViewHolder(position);
        } else if (getItemViewType(position) == Item.TYPE_FOOTER) {
            onBindFooterView(holder.itemView);
        } else {
            holder.onBindViewHolder(position - getHeaderCount());
        }
    }

    protected class FootViewHolder extends ABRecyclerViewHolder{

        public FootViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBindViewHolder(int position) {

        }
    }

    protected T getItemByPosition(int position) {
        return list.get(getPosition(position));
    }

    protected int getPosition(int position) {
        if (hasHeader()) {
            return position - 1;
        } else {
            return position;
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        count += (hasHeader() ? 1 : 0);
        count += (hasFooter() ? 1 : 0);
        if (!hasFooter()) {
            count += (hasFooterAction() ? 1 : 0);
        }
        count += list.size();
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        int size = getItemCount();
        if (hasHeader() && position == 0) {
            return Item.TYPE_HEADER;
        } else if (hasFooter() && position == size - 1) {
            return Item.TYPE_FOOTER;
        } else if (hasFooterAction() && position == size - 1){
            return Item.TYPE_FOOTER_ACTION;
        }
        return Item.TYPE_NORMAL;
    }

    public void removeAll() {
        if (getList() != null) {
            getList().removeAll(getList());
           // new Handler(Looper.getMainLooper()).post(() -> notifyDataSetChanged());
        }
    }

    public void addAll(List<T> list) {
        if (getList() != null) {
            getList().addAll(list);
          //  new Handler(Looper.getMainLooper()).post(() -> notifyDataSetChanged());
        }
    }

    public void removeItem(T item) {
        if (getList() != null) {
            getList().remove(item);
           // new Handler(Looper.getMainLooper()).post(() -> notifyDataSetChanged());
        }
    }

    public void remove(int position) {
        if (getList() != null) {
            if (getHeaderCount() <= position && position < getItemCount()) {
                getList().remove(position);
              //  new Handler(Looper.getMainLooper()).post(() -> notifyDataSetChanged());
            }
        }
    }

    public Activity getActivity() {
        return mActivity;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.onItemClickListener = itemClickListener;
    }

    /**
     * 单个状态改变监听
     */
    protected int currentType = 0;
    protected RecommentChangeListener changeListener;

    public int getCurrentType() {
        return currentType;
    }

    public void setCurrentType(int currentType) {
        this.currentType = currentType;
    }

    public void setChangeListener(RecommentChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    public interface RecommentChangeListener {
        void change(int type);
    }

    public interface ActionListener {
        void onRefresh();
    }
}
