package com.example.libraryvideo.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by huizai on 2017/12/7.
 */

public abstract class ABRecyclerViewHolder extends RecyclerView.ViewHolder {
    public ABRecyclerViewHolder(View itemView) {
        super(itemView);
    }
    public abstract void onBindViewHolder(final int position);
}
