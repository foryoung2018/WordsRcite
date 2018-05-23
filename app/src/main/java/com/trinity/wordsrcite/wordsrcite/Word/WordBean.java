package com.trinity.wordsrcite.wordsrcite.Word;

import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import io.realm.RealmModel;

public class WordBean extends BaseIndexPinyinBean implements Serializable {
    private String word;
    private String translate;
    private String phonetic ;
    private String chinese;
    private List<String> letters;
    private String fileName;

    public WordBean(Word w) {
        setTranslate(w.getTranslate());
        setWord(w.getWord());
        setPhonetic(w.getPhonetic()) ;
        setFileName(w.getFileName());
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public WordBean(String word, String translate, String phonetic) {
        this.word = word;
        this.translate = translate;
        this.phonetic = phonetic;
    }

    public WordBean(String word) {
        this.word = word;
    }

    public WordBean() {
    }

    public String getWord() {
        return word;
    }

    public String getTranslate() {
        return translate;
    }

    public String getChinese() {
        return chinese;
    }

    public List<String> getLetters() {
        return letters;
    }

    public String getPhonetic() {
        return phonetic;
    }


    public void setWord(String word) {
        this.word = word.trim();
        String str[] = word.split("");
        setLetters(Arrays.asList(str));
    }

    public void setTranslate(String translate) {
        this.translate = translate;
        setChinese(getChinese(translate));
    }

    private String getChinese(String str) {
        str = str.replaceAll("[a-zA-Z]","" ).replace("\n","");
        return str;
    }


    public void setChinese(String chinese) {
        this.chinese = chinese;
    }

    public void setLetters(List<String> letters) {
        this.letters = letters;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }


    @Override
    public String getTarget() {
        return word;
    }
}
