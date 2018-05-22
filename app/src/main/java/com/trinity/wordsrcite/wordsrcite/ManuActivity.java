package com.trinity.wordsrcite.wordsrcite;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.trinity.wordsrcite.wordsrcite.util.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class ManuActivity extends AppCompatActivity  {

    private ArrayList<String> data;
    private ArrayList<String> xml;

    private RecyclerView mRecyclerView = null;

    private DataAdapter mDataAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manu);
        getXmlList();
        copyFileList();
        initView();

    }

    private static final String SD_PATH = "/sdcard/hdzuoye/pic/";
    private static final String IN_PATH = "/hdzuoye/pic/";

    /**
     * 随机生产文件名
     *
     * @return
     */
    private static String generateFileName() {
//        return UUID.randomUUID().toString();
        return System.currentTimeMillis()+"";

    }
    /**
     * 保存bitmap到本地
     *
     * @param context
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(Context context, Bitmap mBitmap) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            savePath = SD_PATH;
        } else {
            savePath = context.getApplicationContext().getFilesDir()
                    .getAbsolutePath()
                    + IN_PATH;
        }
        try {
            filePic = new File(savePath + generateFileName() + ".png");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }
    /**
     * base64转为bitmap
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.NO_PADDING);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private static HashMap<String, Boolean> mapInitied = new HashMap<String, Boolean>();

    private void copyFileList() {
        xml = new ArrayList<String>();
        String path =FileUtil.createTmpDir(this,"WordBook");

        for (String s : data){
            String destFilename = path + "/" + s;

            boolean recover = false;
            Boolean existed = mapInitied.get(s); // 启动时完全覆盖一次
            if (existed == null || !existed) {
                recover = true;
            }
            try{
                FileUtil.copyFromAssets(this.getAssets(), s, destFilename, recover);
            }catch (Exception e){

            }
            xml.add(destFilename);
        }

    Log.i(TAG,"xml ="+xml );
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDataAdapter = new DataAdapter(this);
        mDataAdapter.setData(data);
        mRecyclerView.setAdapter(mDataAdapter);

    }

    public List<String> getXmlList() {

        AssetManager assetManager = this.getAssets();
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
            return new ViewHolder(mLayoutInflater.inflate(R.layout.wordbook_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.tv.setText(mDataList.get(position));
            viewHolder.tv.setTag(xml.get(position));
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
                        b.putString("file",(String)v.getTag());
                        Intent intent = new Intent(ManuActivity.this,WordActivity.class);
                        intent.putExtras(b);
                        startActivity(intent);
                    }
                });
            }
        }
    }
}
