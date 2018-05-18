package com.trinity.wordsrcite.wordsrcite;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class WordActivity extends AppCompatActivity  {

    public static final int WORD_FINISH = 1111 ;
    private ArrayList<String> data;
    private ArrayList<String> xml;
    private String path;
    private static final String TAG =  WordActivity.class.getSimpleName();
    private List<WordBean> words;

    private static final String INDEX_STRING_TOP = "↑";
    private RecyclerView mRv;
    private SwipeDelMenuAdapter mAdapter;
    private LinearLayoutManager mManager;
    private List<WordBean> mDatas = new ArrayList<>();

    private SuspensionDecoration mDecoration;

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
        path = intent.getExtras().getString("file");
//        WorkUtil.excute(new Runnable() {
//            @Override
//            public void run() {
        initialTts(); // 初始化TTS引擎
        parse(path);
//            }
//        },mainHandler,TTS_OK);
//        initDialog();
        initView();
        initDatas(getResources().getStringArray(R.array.provinces));
        Realm.init(this);
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                // Add a person
                Word w = realm.createObject(Word.class);
                w.setWord("111") ;
                w.setTranslate("22");
                w.setPhonetic("111");
            }
        });

        // Build the query looking at all users:
        RealmQuery<Word> query = realm.where(Word.class);
        RealmResults<Word> result1 = query.findAll();
        String word = result1.get(0).getWord();
        Toast.makeText(this,word,Toast.LENGTH_SHORT).show();
    }


    AlertDialog dialog;

    private void initDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(WordActivity.this);
        builder.setTitle("普通的对话框的标题");
        builder.setMessage("这是一个普通的对话框的内容");
        builder.setView(R.layout.dialog_progress);
        dialog = builder.create();

//        ((Animatable) ((ImageView) dialog.getWindow().findViewById(R.id.circular_indeterminate_progress_medium)).getDrawable()).start();
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

    /**
     * 组织数据源
     *
     * @param data
     * @return
     */
    private void initDatas(final String[] data) {
        mDatas = new ArrayList<>();
        //微信的头部 也是可以右侧IndexBar导航索引的，
        // 但是它不需要被ItemDecoration设一个标题titile
        mDatas.add((WordBean) new WordBean("新的朋友").setBaseIndexTag(INDEX_STRING_TOP));
        mDatas.add((WordBean) new WordBean("新的朋友").setBaseIndexTag(INDEX_STRING_TOP));
        mDatas.add((WordBean) new WordBean("新的朋友").setBaseIndexTag(INDEX_STRING_TOP));
        mDatas.add((WordBean) new WordBean("新的朋友").setBaseIndexTag(INDEX_STRING_TOP));
        for (int i = 0; i < data.length; i++) {
            WordBean cityBean = new WordBean();
            cityBean.setWord(data[i]);//设置城市名称
            mDatas.add(cityBean);
        }
        mAdapter.setDatas(words);
        mAdapter.notifyDataSetChanged();

        mIndexBar.setmPressedShowTextView(mTvSideBarHint)//设置HintTextView
                .setNeedRealIndex(true)//设置需要真实的索引
                .setmLayoutManager(mManager)//设置RecyclerView的LayoutManager
                .setmSourceDatas(words)//设置数据
                .invalidate();
        mDecoration.setmDatas(words);
    }

    private void parse(String file) {
        try {
            // 创建一个工厂对象
            SAXParserFactory factory = SAXParserFactory.newInstance();
            // 通过工厂对象得到一个解析器对象
            SAXParser parser = factory.newSAXParser();
            // 通过parser得到XMLReader对象
            XMLReader reader = parser.getXMLReader();
            // 为reader对象注册事件处理接口
            XmlUtils util = new XmlUtils();
            XmlUtils.MyHandler handler = util.new MyHandler();
            reader.setContentHandler(handler);
            // 解析指定XML字符串对象
            InputStream is = new FileInputStream(file);
            InputSource source = new InputSource(is);
            reader.parse(source);
            words = handler.getList();
            Log.i(TAG,words.toString());
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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


    class Lisenter implements SpeechSynthesizerListener{

        @Override
        public void onSynthesizeStart(String s) {

        }

        @Override
        public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {

        }

        @Override
        public void onSynthesizeFinish(String s) {

        }

        @Override
        public void onSpeechStart(String s) {

        }

        @Override
        public void onSpeechProgressChanged(String s, int i) {

        }

        @Override
        public void onSpeechFinish(String s) {

        }

        @Override
        public void onError(String s, SpeechError speechError) {

        }
    }
}
