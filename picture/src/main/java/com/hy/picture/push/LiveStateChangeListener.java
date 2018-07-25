package com.hy.picture.push;

public interface LiveStateChangeListener {

    /**
     * 发送错误
     * @param code
     */
    void onError(int code);

}
