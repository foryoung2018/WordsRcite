package com.trinity.wordsrcite.wordsrcite.util;

import android.util.Log;
import android.util.Xml;

import com.trinity.wordsrcite.wordsrcite.Item;

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

/**
 * =====作者=====
 * 许英俊
 * =====时间=====
 * 2016/11/9.
 */
public class XmlUtils {

//    /**
//     * DOM解析XML文件时，会将XML文件的所有内容读取到内存中（内存的消耗比较大），然后允许您使用DOM API遍历XML树、检索所需的数据
//     */
//    public List<Item> dom2xml(InputStream is) throws Exception {
//        //一系列的初始化
//        List<Item> list = new ArrayList<>();
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder builder = factory.newDocumentBuilder();
//        //获得Document对象
//        Document document = builder.parse(is);
//        //获得Item的List
//        NodeList ItemList = document.getElementsByTagName("Item");
//        //遍历Item标签
//        for (int i = 0; i < ItemList.getLength(); i++) {
//            //获得Item标签
//            Node node_Item = ItemList.item(i);
//            //获得Item标签里面的标签
//            NodeList childNodes = node_Item.getChildNodes();
//            //新建Item对象
//            Item Item = new Item();
//            //遍历Item标签里面的标签
//            for (int j = 0; j < childNodes.getLength(); j++) {
//                //获得name和nickName标签
//                Node childNode = childNodes.item(j);
//                //判断是name还是nickName
//                if ("name".equals(childNode.getNodeName())) {
//                    String name = childNode.getTextContent();
//                    Item.setName(name);
//                    //获取name的属性
//                    NamedNodeMap nnm = childNode.getAttributes();
//                    //获取sex属性，由于只有一个属性，所以取0
//                    Node n = nnm.item(0);
//                    Item.setSex(n.getTextContent());
//                } else if ("nickName".equals(childNode.getNodeName())) {
//                    String nickName = childNode.getTextContent();
//                    Item.setNickName(nickName);
//                }
//            }
//            //加到List中
//            list.add(Item);
//        }
//        return list;
//    }

    /**
     * SAX是一个解析速度快并且占用内存少的xml解析器，SAX解析XML文件采用的是事件驱动，它并不需要解析完整个文档，而是按内容顺序解析文档的过程
     */
    public List<Item> sax2xml(InputStream is) throws Exception {
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

        private List<Item> list;
        private Item item;
        //用于存储读取的临时变量
        private String tempString;
        private String tag = null;

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
            if ("item".equals(localName)) {
                //读到Item标签
                item = new Item();
                list.add(item);
                Log.i("main","1tempString="+tempString);
            } else if ("word".equals(localName)) {
                //获取name里面的属性
                Log.i("main","2tempString="+tempString);
            } else if("trans".equals(localName)){
                Log.i("main","3tempString="+tempString);
            }
            tag = localName;


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
            tag = null;
            super.endElement(uri, localName, qName);
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
            if (null != tag) {
                String value = new String(ch, start, length);
                if(tag.equals("word")||tag.equals("trans")){
                    System.out.println("value = " + value);
                }
            }
        }

        /**
         * 获取该List
         *
         * @return
         */
        public List<Item> getList() {
            return list;
        }
    }

    /**
     * Pull解析器的运行方式与 SAX 解析器相似。它提供了类似的事件，可以使用一个switch对感兴趣的事件进行处理
     */
//    public List<Item> pull2xml(InputStream is) throws Exception {
//        List<Item> list = null;
//        Item Item = null;
//        //创建xmlPull解析器
//        XmlPullParser parser = Xml.newPullParser();
//        ///初始化xmlPull解析器
//        parser.setInput(is, "utf-8");
//        //读取文件的类型
//        int type = parser.getEventType();
//        //无限判断文件类型进行读取
//        while (type != XmlPullParser.END_DOCUMENT) {
//            switch (type) {
//                //开始标签
//                case XmlPullParser.START_TAG:
//                    if ("Items".equals(parser.getName())) {
//                        list = new ArrayList<>();
//                    } else if ("Item".equals(parser.getName())) {
//                        Item = new Item();
//                    } else if ("name".equals(parser.getName())) {
//                        //获取sex属性
//                        String sex = parser.getAttributeValue(null,"sex");
//                        Item.setSex(sex);
//                        //获取name值
//                        String name = parser.nextText();
//                        Item.setName(name);
//                    } else if ("nickName".equals(parser.getName())) {
//                        //获取nickName值
//                        String nickName = parser.nextText();
//                        Item.setNickName(nickName);
//                    }
//                    break;
//                //结束标签
//                case XmlPullParser.END_TAG:
//                    if ("Item".equals(parser.getName())) {
//                        list.add(Item);
//                    }
//                    break;
//            }
//            //继续往下读取标签类型
//            type = parser.next();
//        }
//        return list;
//    }
//
//    /**
//     * SAX和Pull的区别：SAX解析器的工作方式是自动将事件推入事件处理器进行处理，因此你不能控制事件的处理主动结束；而Pull解析器的工作方式为允许你的应用程序代码主动从解析器中获取事件，正因为是主动获取事件，因此可以在满足了需要的条件后不再获取事件，结束解析。
//     */
}
