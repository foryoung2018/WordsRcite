package com.trinity.wordsrcite.wordsrcite.Service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.trinity.wordsrcite.wordsrcite.Constants;
import com.trinity.wordsrcite.wordsrcite.Word.Word;
import com.trinity.wordsrcite.wordsrcite.Word.WordBean;
import com.trinity.wordsrcite.wordsrcite.util.SharedPreferencesUtils;
import com.trinity.wordsrcite.wordsrcite.util.XmlUtils;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class WordParseService extends IntentService {
    private static final String TAG  = WordParseService.class.getSimpleName();

    private boolean isRunning = true;
    private int count = 0;
    private List<WordBean> words;
    public static final String PARSE_OK  = "pasre_ok";
    public static final String WORD_LIST = "word_list";


    public WordParseService() {
        super("");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public WordParseService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String path = intent.getStringExtra("xml_path");
        parse(path);
        realm();
        writeSP();
    }

    private void writeSP() {
        SharedPreferencesUtils.setParam(this, Constants.INIT_WORD_LIST,true);
    }

    //写入数据库
    private void realm() {
        Realm.init(this);
        final Realm realm = Realm.getDefaultInstance();
        for(final WordBean b : words) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override public void execute(Realm realm) {
                    // Add a person
                    Word w = realm.createObject(Word.class);
                    w.setWord(b.getWord()) ;
                    w.setTranslate(b.getTranslate());
                    w.setPhonetic(b.getPhonetic());
                }
            });
        }
        RealmQuery<Word> query = realm.where(Word.class);
        RealmResults<Word> result1 = query.findAll();
        Log.i(TAG,result1.toString());
    }

    // 发送服务状态信息
    private void sendServiceStatus(String status) {
        Intent intent = new Intent(status);
        intent.putExtra(WORD_LIST,(Serializable) words);
        sendBroadcast(intent);
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
            XmlUtils.MyHandler handler = util.new MyHandler(this);
            reader.setContentHandler(handler);
            // 解析指定XML字符串对象
            InputStream is = new FileInputStream(file);
            InputSource source = new InputSource(is);
            reader.parse(source);
            words = handler.getList();
            sendServiceStatus(PARSE_OK);

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
