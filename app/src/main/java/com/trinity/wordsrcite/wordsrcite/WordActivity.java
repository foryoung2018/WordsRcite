package com.trinity.wordsrcite.wordsrcite;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.mcxtzhang.indexlib.IndexBar.widget.IndexBar;
import com.mcxtzhang.indexlib.suspension.SuspensionDecoration;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.trinity.wordsrcite.wordsrcite.Service.WordParseService;
import com.trinity.wordsrcite.wordsrcite.Word.Word;
import com.trinity.wordsrcite.wordsrcite.Word.WordBean;
import com.trinity.wordsrcite.wordsrcite.Worker.WorkUtil;
import com.trinity.wordsrcite.wordsrcite.adapter.WordAdapter;
import com.trinity.wordsrcite.wordsrcite.control.InitConfig;
import com.trinity.wordsrcite.wordsrcite.control.MySyntherizer;
import com.trinity.wordsrcite.wordsrcite.control.NonBlockSyntherizer;
import com.trinity.wordsrcite.wordsrcite.decoration.DividerItemDecoration;
import com.trinity.wordsrcite.wordsrcite.listener.UiMessageListener;
import com.trinity.wordsrcite.wordsrcite.util.AutoCheck;
import com.trinity.wordsrcite.wordsrcite.util.FileUtil;
import com.trinity.wordsrcite.wordsrcite.util.OfflineResource;
import com.trinity.wordsrcite.wordsrcite.util.SharedPreferencesUtils;
import com.trinity.wordsrcite.wordsrcite.util.ToastUtil;
import com.trinity.wordsrcite.wordsrcite.util.XmlUtils;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static com.trinity.wordsrcite.wordsrcite.Service.WordParseService.PARSE_OK;


public class WordActivity extends AppCompatActivity  {

    public static final int WORD_FINISH = 1111 ;
    private String path;
    private String file;
    private static final String TAG =  WordActivity.class.getSimpleName();
    private List<WordBean> words;

    private static final String INDEX_STRING_TOP = "↑";
    private RecyclerView mRv;
    private SwipeDelMenuAdapter mAdapter;
    private LinearLayoutManager mManager;
    private List<WordBean> mDatas = new ArrayList<>();

    private SuspensionDecoration mDecoration;
    ProgressDialog dialog;
    /**
     * 右侧边栏导航区域
     */
    private IndexBar mIndexBar;

    /**
     * 显示指示器DialogText
     */
    private TextView mTvSideBarHint;

    protected String appId = "8238072";

    protected String appKey = "sW47nK8HSpCOzXYaUkwT6oNR";

    protected String secretKey = "d02349de595180aa138e4937f193d3bd";
    protected TtsMode ttsMode = TtsMode.MIX;
    protected String offlineVoice = OfflineResource.VOICE_MALE;

    private static final int TTS_OK = 1;

    // ===============初始化参数设置完毕，更多合成参数请至getParams()方法中设置 =================

    // 主控制类，所有合成控制方法从这个类开始

    protected MySyntherizer synthesizer;
    protected static String DESC = "请先看完说明。之后点击“合成并播放”按钮即可正常测试。\n"
            + "测试离线合成功能需要首次联网。\n"
            + "纯在线请修改代码里ttsMode为TtsMode.ONLINE， 没有纯离线。\n"
            + "本Demo的默认参数设置为wifi情况下在线合成, 其它网络（包括4G）使用离线合成。 在线普通女声发音，离线男声发音.\n"
            + "合成可以多次调用，SDK内部有缓存队列，会依次完成。\n\n";


    private RecyclerView mRecyclerView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word);
        Intent intent = getIntent();
        path = intent.getExtras().getString("path");
        file = intent.getExtras().getString("file");
        initialTts(); // 初始化TTS引擎
        initView();
        initDatas();
        if(!(boolean) SharedPreferencesUtils.getParam(this, file,false)){
            showDialog();
            Intent serviceIntent = new Intent(this, WordParseService.class);
            serviceIntent.putExtra("xml_path",path);
            serviceIntent.putExtra("xml_file",file);
            startService(serviceIntent);
            registerReceiver();
        }else{
            Realm.init(this);
            Realm realm = Realm.getDefaultInstance();
            RealmQuery<Word> query = realm.where(Word.class);
            RealmResults<Word> result1 = query.findAll();
            List<Word> ret= realm.copyFromRealm(result1);
            transform(ret);
            Log.i(TAG,"*** "+result1.toString());
            initView();
            initDatas();
        }

    }

    private void transform(List<Word> ret) {
        words  = new ArrayList<>();
        for(Word w: ret){
            WordBean bean = new WordBean(w);
            words.add(bean);
        }
    }


    public void showDialog(){
         dialog = new ProgressDialog(this);
        // 设置对话框参数
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("生成单词表");
//        dialog.setMessage("生成进度：");
        dialog.setCancelable(false);
        // 设置进度条参数
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMax(100);
        dialog.setProgress(30);
        dialog.incrementProgressBy(20);
        dialog.setIndeterminate(false); // 填false表示是明确显示进度的 填true表示不是明确显示进度的
        dialog.show();
    }

    Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            Log.i(TAG,"msg.what = " + msg.what);
            switch (msg.what){
                case TTS_OK:
                    break;

                case WORD_FINISH:
                    this.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            speakBegin();
                        }
                    },3000);
                    break;
            }
        }

    };

    private void initialTts() {
        LoggerProxy.printable(true); // 日志打印在logcat中
        // 设置初始化参数
        // 此处可以改为 含有您业务逻辑的SpeechSynthesizerListener的实现类
        SpeechSynthesizerListener listener = new UiMessageListener(mainHandler);

        Map<String, String> params = getParams();

        // appId appKey secretKey 网站上您申请的应用获取。注意使用离线合成功能的话，需要应用中填写您app的包名。包名在build.gradle中获取。
        InitConfig initConfig = new InitConfig(appId, appKey, secretKey, ttsMode, params, listener);
        synthesizer = new NonBlockSyntherizer(this, initConfig, mainHandler); // 此处可以改为MySyntherizer 了解调用过程
    }

    /**
     * 合成的参数，可以初始化时填写，也可以在合成前设置。
     *
     * @return
     */
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        // 以下参数均为选填
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        params.put(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置合成的音量，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_VOLUME, "9");
        // 设置合成的语速，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_SPEED, "4");
        // 设置合成的语调，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_PITCH, "5");

        params.put(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

        // 离线资源文件， 从assets目录中复制到临时目录，需要在initTTs方法前完成
        OfflineResource offlineResource = createOfflineResource(offlineVoice);
        // 声学模型文件路径 (离线引擎使用), 请确认下面两个文件存在
        params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, offlineResource.getTextFilename());
        params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE,
                offlineResource.getModelFilename());
        return params;
    }

    protected OfflineResource createOfflineResource(String voiceType) {
        OfflineResource offlineResource = null;
        try {
            offlineResource = new OfflineResource(this, voiceType);
        } catch (IOException e) {
            // IO 错误自行处理
            e.printStackTrace();
        }
        return offlineResource;
    }

    private void initView() {
        mRv = (RecyclerView) findViewById(R.id.list);
        mRv.setLayoutManager(mManager = new LinearLayoutManager(this));
        mAdapter = new SwipeDelMenuAdapter(this, words);
        mRv.setAdapter(mAdapter);
        mRv.addItemDecoration(mDecoration = new SuspensionDecoration(this, mDatas));
        //如果add两个，那么按照先后顺序，依次渲染。
        //mRv.addItemDecoration(new TitleItemDecoration2(this,mDatas));
        mRv.addItemDecoration(new DividerItemDecoration(WordActivity.this, DividerItemDecoration.VERTICAL_LIST));
        //使用indexBar
        mTvSideBarHint = (TextView) findViewById(R.id.tvSideBarHint);//HintTextView
        mIndexBar = (IndexBar) findViewById(R.id.indexBar);//IndexBar
    }

    private void initDatas() {
        mDatas = new ArrayList<>();
        //微信的头部 也是可以右侧IndexBar导航索引的，
        // 但是它不需要被ItemDecoration设一个标题titile
        mAdapter.setDatas(words);
        mAdapter.notifyDataSetChanged();

        mIndexBar.setmPressedShowTextView(mTvSideBarHint)//设置HintTextView
                .setNeedRealIndex(true)//设置需要真实的索引
                .setmLayoutManager(mManager)//设置RecyclerView的LayoutManager
                .setmSourceDatas(words)//设置数据
                .invalidate();
        mDecoration.setmDatas(words);
    }


    /**
     * 和CityAdapter 一模一样，只是修改了 Item的布局
     * Created by zhangxutong .
     * Date: 16/08/28
     */

    private class SwipeDelMenuAdapter extends WordAdapter {

        public SwipeDelMenuAdapter(Context mContext, List<WordBean> mDatas) {
            super(mContext, mDatas);
        }

        @Override
        public SwipeDelMenuAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(mInflater.inflate(R.layout.item_word_swipe, parent, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
            holder.itemView.findViewById(R.id.btnDel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((SwipeMenuLayout) holder.itemView).quickClose();
                    mDatas.remove(holder.getAdapterPosition());
                    mIndexBar.setmPressedShowTextView(mTvSideBarHint)//设置HintTextView
                            .setNeedRealIndex(true)//设置需要真实的索引
                            .setmLayoutManager(mManager)//设置RecyclerView的LayoutManager
                            .setmSourceDatas(mDatas)//设置数据
                            .invalidate();
                    notifyDataSetChanged();
                }
            });
        }
    }

    public void speak(View view){
        speakBegin();
    }

    private void speakBegin(){
        batchSpeak(words.get(i));
        mManager.scrollToPositionWithOffset(i, 60);
        i++;
    }

    private int i=0;


    @Override
    protected void onDestroy() {
        synthesizer.release();
        Log.i(TAG, "释放资源成功");
        mainHandler.removeCallbacks(null);
        super.onDestroy();
    }

    /**
     * 批量播放
     */
    private void batchSpeak(WordBean word) {
        List<Pair<String, String>> texts = new ArrayList<Pair<String, String>>();
        texts.add(new Pair<String, String>(word.getWord(), "a0"));
        texts.add(new Pair<String, String>(word.getChinese(), "a1"));
        texts.add(new Pair<String, String>(word.getTranslate(), "a2"));
        texts.add(new Pair<String, String>(word.getLetters().toString(), "a3"));
        texts.add(new Pair<String, String>(word.getLetters().toString(), "a3"));
        texts.add(new Pair<String, String>(word.getWord(), "a4"));
        int result = synthesizer.batchSpeak(texts);
    }

    private BroadcastReceiver parseReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getAction() == PARSE_OK)
            {
                words = (List<WordBean>) intent.getSerializableExtra(WordParseService.WORD_LIST);
                initView();
                initDatas();
                dialog.dismiss();
            }
        }
    };


    private void registerReceiver()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(PARSE_OK);
        registerReceiver(parseReceiver, filter);
    }

}
