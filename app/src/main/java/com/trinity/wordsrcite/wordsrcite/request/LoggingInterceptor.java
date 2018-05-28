package com.trinity.wordsrcite.wordsrcite.request;

import com.orhanobut.logger.Logger;
import com.trinity.wordsrcite.wordsrcite.BuildConfig;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class LoggingInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (BuildConfig.DEBUG) {
            Logger.i(String.format("发送请求 %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));
        }
        return chain.proceed(request);
    }

}
