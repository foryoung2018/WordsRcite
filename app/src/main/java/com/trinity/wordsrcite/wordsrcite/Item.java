package com.trinity.wordsrcite.wordsrcite;

/**
 * Created by foryoung on 2018/4/22.
 */

public class Item {

    private String word;
    private String trans;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTrans() {
        return trans;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }

    public Item(String word, String trans) {
        this.word = word;
        this.trans = trans;
    }

    public Item() {
    }

    @Override
    public String toString() {
        return "Item{" +
                "word='" + word + '\'' +
                ", trans='" + trans + '\'' +
                '}';
    }
}
