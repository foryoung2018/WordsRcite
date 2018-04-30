package com.trinity.wordsrcite.wordsrcite.util;

import android.util.Log;
import android.util.Xml;

import com.trinity.wordsrcite.wordsrcite.Item;
import com.trinity.wordsrcite.wordsrcite.Word.WordBean;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.HandlerBase;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import static android.content.ContentValues.TAG;

/**
 * =====作者=====
 * 许英俊
 * =====时间=====
 * 2016/11/9.
 */
public class XmlUtils {


    /**
     * SAX是一个解析速度快并且占用内存少的xml解析器，SAX解析XML文件采用的是事件驱动，它并不需要解析完整个文档，而是按内容顺序解析文档的过程
     */
    public List<WordBean> sax2xml(InputStream is) throws Exception {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        //初始化Sax解析器
        SAXParser sp = spf.newSAXParser();
        //新建解析处理器
        MyHandler handler = new MyHandler();
        //将解析交给处理器
        sp.parse(is, handler);
        //返回List
        return handler.getList();
    }

    public class MyHandler extends DefaultHandler {

        private List<WordBean> list =null;
        private WordBean item =null;
        //用于存储读取的临时变量
        private String tempString;
        private String tag = null;
        private StringBuilder sb;

        private boolean flag = false;

        /**
         * 解析到文档开始调用，一般做初始化操作
         *
         * @throws SAXException
         */
        @Override
        public void startDocument() throws SAXException {
            list = new ArrayList<>();
            super.startDocument();
        }

        /**
         * 解析到文档末尾调用，一般做回收操作
         *
         * @throws SAXException
         */
        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
        }

        /**
         * 每读到一个元素就调用该方法
         *
         * @param uri
         * @param localName
         * @param qName
         * @param attributes
         * @throws SAXException
         */
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            flag = true;
            if ("item".equals(localName)) {
                //读到Item标签
                item = new WordBean();
//                list.add(item);
            }
            tag = localName;
            sb = new StringBuilder();


        }

        /**
         * 读到元素的结尾调用
         *
         * @param uri
         * @param localName
         * @param qName
         * @throws SAXException
         */
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);


            //结束解析节点
            flag = false;
            if( qName.equals("item")){
                list.add(item);
            }
            String s = sb.toString();
            Log.i(TAG,"yang tag = "+tag);
            if("word".equals(tag)){
                item.setWord(s);
            }else if("trans".equals(tag)){
                item.setTranslate(s);
            }else if("phonetic".equals(tag)){
                item.setPhonetic(s);
            }
            tag = null;

        }

        /**
         * 读到属性内容调用
         *
         * @param ch
         * @param start
         * @param length
         * @throws SAXException
         */
        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            if(!flag) {
                return;
            }
            sb.append(new String(ch, start, length) );
        }

        /**
         * 获取该List
         *
         * @return
         */
        public List<WordBean> getList() {
            return list;
        }
    }

}
