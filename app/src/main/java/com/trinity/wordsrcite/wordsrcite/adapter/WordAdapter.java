package com.trinity.wordsrcite.wordsrcite.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.trinity.wordsrcite.wordsrcite.R;
import com.trinity.wordsrcite.wordsrcite.Word.WordBean;

import java.util.List;


/**
 * Created by zhangxutong .
 * Date: 16/08/28
 */

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.ViewHolder> {
    protected Context mContext;
    protected List<WordBean> mDatas;
    protected LayoutInflater mInflater;

    public WordAdapter(Context mContext, List<WordBean> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        mInflater = LayoutInflater.from(mContext);
    }

    public List<WordBean> getDatas() {
        return mDatas;
    }

    public WordAdapter setDatas(List<WordBean> datas) {
        mDatas = datas;
        return this;
    }

    @Override
    public WordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_word, parent, false));
    }

    @Override
    public void onBindViewHolder(final WordAdapter.ViewHolder holder, final int position) {
        final WordBean wordBean = mDatas.get(position);
        holder.tvCity.setText(wordBean.getWord());
        holder.tvTrans.setText(wordBean.getTranslate());
        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "pos:" + position, Toast.LENGTH_SHORT).show();
            }
        });
        holder.avatar.setImageResource(R.drawable.friend);
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCity;
        TextView tvTrans;
        ImageView avatar;
        View content;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCity = (TextView) itemView.findViewById(R.id.tvCity);
            avatar = (ImageView) itemView.findViewById(R.id.ivAvatar);
            tvTrans = (TextView) itemView.findViewById(R.id.tvTrans);
            content = itemView.findViewById(R.id.content);
        }
    }
}
