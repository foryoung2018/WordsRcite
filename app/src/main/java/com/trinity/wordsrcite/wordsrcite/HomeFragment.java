package com.trinity.wordsrcite.wordsrcite;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.trinity.wordsrcite.wordsrcite.util.FileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {


    private ArrayList<String> data;
    private ArrayList<String> xml;
    private ArrayList<String> file;
    private View view;

    private RecyclerView mRecyclerView = null;

    private HomeFragment.DataAdapter mDataAdapter = null;

    private static HashMap<String, Boolean> mapInitied = new HashMap<String, Boolean>();

    public HomeFragment() {
        // Required empty public constructor
    }

    //单例模式
    public static HomeFragment newInstance(){
        HomeFragment homeFragment=new HomeFragment();
        return homeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.manu, container, false);
        getXmlList();
        copyFileList();
        initView();
        return view;
    }

    public List<String> getXmlList() {

        AssetManager assetManager = getActivity().getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
        data = new ArrayList<>();
        if(files!=null&& files.length>0){
            for (String s : files){
                if (s.contains(".xml")){
                    data.add(s);
                }
            }
        }
        return data;
    }

    private void copyFileList() {
        xml = new ArrayList<String>();
        file = new ArrayList<String>();
        String path = FileUtil.createTmpDir(getActivity(),"WordBook");

        for (String s : data){
            String destFilename = path + "/" + s;

            boolean recover = false;
            Boolean existed = mapInitied.get(s); // 启动时完全覆盖一次
            if (existed == null || !existed) {
                recover = true;
            }
            try{
                FileUtil.copyFromAssets(getActivity().getAssets(), s, destFilename, recover);
            }catch (Exception e){

            }
            xml.add(destFilename);
            file.add(s);
        }

        Log.i(TAG,"xml ="+xml );
    }

    private void initView() {
        mRecyclerView = (RecyclerView)view.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDataAdapter = new DataAdapter(getActivity());
        mDataAdapter.setData(data);
        mRecyclerView.setAdapter(mDataAdapter);

    }

    private class DataAdapter extends RecyclerView.Adapter {

        private LayoutInflater mLayoutInflater;
        private ArrayList<String> mDataList = new ArrayList<>();

        public DataAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
        }

        public void setData(ArrayList<String> list) {
            this.mDataList = list;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DataAdapter.ViewHolder(mLayoutInflater.inflate(R.layout.wordbook_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            DataAdapter.ViewHolder viewHolder = (DataAdapter.ViewHolder) holder;
            viewHolder.tv.setText(mDataList.get(position));
            Map<String,String> map = new HashMap<>();
            map.put("path",xml.get(position));
            map.put("file",file.get(position));
            viewHolder.tv.setTag(map);
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            private Button tv;

            public ViewHolder(View itemView) {
                super(itemView);
                tv = (Button) itemView.findViewById(R.id.button);

                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle b =  new Bundle();
                        Map<String,String> map = (Map<String,String>)v.getTag();
                        b.putString("path", map.get("path"));
                        b.putString("file",map.get("file"));
                        Intent intent = new Intent(getActivity(),WordActivity.class);
                        intent.putExtras(b);
                        startActivity(intent);
                    }
                });
            }
        }
    }
}
