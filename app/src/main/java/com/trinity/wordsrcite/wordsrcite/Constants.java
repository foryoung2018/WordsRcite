package com.trinity.wordsrcite.wordsrcite;

import java.util.HashMap;

/**
 * Created by Guijun Zhou on 2017/10/11.
 */

public class Constants {
    public static HashMap<String, String> HOSTMAP = new HashMap<>();
    static {
        HOSTMAP.put("debug", "nova-dev.momenta.cn");
        HOSTMAP.put("stage", "nova-stage.momenta.cn");
        HOSTMAP.put("mtest", "nova-test.momenta.cn");
        HOSTMAP.put("release", "nova.momenta.cn");
    }
    public static boolean DEBUG = BuildConfig.DEBUG;
    public static final String APPTAG = "DVR_";
    public static final String API_SCHEME = "https";
    public static final String API_HOST = HOSTMAP.get(BuildConfig.BUILD_TYPE);

    public static final String S3_KEY_MEDIA_PRE = BuildConfig.BUILD_TYPE.equals("release") ? "" : BuildConfig.BUILD_TYPE+"/";
    public static final String S3_KEY_MEDIA_IMAGE = S3_KEY_MEDIA_PRE + "dvr/"+BuildConfig.FLAVOR+"/image";


    public static final String INIT_WORD_LIST = "init_word_list";
}
