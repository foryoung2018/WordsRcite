package com.trinity.wordsrcite.wordsrcite.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.trinity.wordsrcite.wordsrcite.BuildConfig;
import com.trinity.wordsrcite.wordsrcite.MyApplication;
import com.trinity.wordsrcite.wordsrcite.R;
import com.trinity.wordsrcite.wordsrcite.request.GetRequest_Interface;
import com.trinity.wordsrcite.wordsrcite.request.LoggingInterceptor;
import com.trinity.wordsrcite.wordsrcite.request.Request_interface;
import com.trinity.wordsrcite.wordsrcite.request.Translation;
import com.trinity.wordsrcite.wordsrcite.request.Translation1;
import com.trinity.wordsrcite.wordsrcite.util.NetUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class TranslateFragment extends Fragment {

    private View view;
    @BindView(R.id.tv_f)
    TextView tv_f;
    @BindView(R.id.tv_show)
    TextView tv_show;
    @BindView(R.id.tv_s)
    TextView tv_s;
    @BindView(R.id.tv_trans)
    ImageView imTransfer;
    @BindView(R.id.button2)
    Button bt_translate;
    @BindView(R.id.add_content)
    EditText add_content;

    private Unbinder unbinder;
    //设缓存有效期为两天
    protected static final long CACHE_STALE_SEC = 60 * 60 * 24 * 2;
    //查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
    protected static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_SEC;
    //查询网络的Cache-Control设置，头部Cache-Control设为max-age=0时则不会使用缓存而请求服务器
    protected static final String CACHE_CONTROL_NETWORK = "max-age=0";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_translate, container, false);
        unbinder = ButterKnife.bind(this, view);
        getRequest();
        initOkHttpClient();

        return view;
    }

    public void getRequest() {

        //步骤4:创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://fy.iciba.com/") // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();

        // 步骤5:创建 网络请求接口 的实例
        GetRequest_Interface request = retrofit.create(GetRequest_Interface.class);

        //对 发送请求 进行封装
        Call<Translation> call = request.getCall();

        //步骤6:发送网络请求(异步)
        call.enqueue(new Callback<Translation>() {
            //请求成功时候的回调
            @Override
            public void onResponse(Call<Translation> call, Response<Translation> response) {
                //请求处理,输出结果
                response.body().show();
            }

            //请求失败时候的回调
            @Override
            public void onFailure(Call<Translation> call, Throwable throwable) {
                System.out.println("连接失败");
            }
        });
    }

    public void postRequest(String word) {

        //步骤4:创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl("http://fanyi.youdao.com/") // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        // 步骤5:创建 网络请求接口 的实例
        Request_interface request = retrofit.create(Request_interface.class);

        //对 发送请求 进行封装(设置需要翻译的内容)
        request.getCall(word).subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<Translation1>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Translation1 value) {
                        tv_show.setText(value.getTranslateResult().get(0).get(0).getTgt());
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @OnClick({R.id.tv_trans,R.id.button2})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_trans:
                String f = tv_f.getText().toString();
                String s = tv_s.getText().toString();
                tv_f.setText(s);
                tv_s.setText(f);
                break;
            case R.id.button2:
//                rxRequest();
//                rx();
                postRequest(add_content.getText().toString());
                break;
        }
    }

    private void rx() {

        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "Observable thread is : " + Thread.currentThread().getName());
                Log.d(TAG, "emit 1");
                emitter.onNext(1);
            }
        });

        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "Observer thread is :" + Thread.currentThread().getName());
                Log.d(TAG, "onNext: " + integer);
            }
        };

        observable.subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "After observeOn(mainThread), current thread is: " + Thread.currentThread().getName());
                    }
                })
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "After observeOn(io), current thread is : " + Thread.currentThread().getName());
                    }
                })
                .subscribe(consumer);


    }

    private static final String TAG = "TranslateFragment";

    private void rxRequest() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; ; i++) {
                    emitter.onNext(i);
                }
            }
        }).subscribeOn(Schedulers.io())
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer % 10 == 0;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "" + integer);
                    }
                });



    }

    //1
//    private Observable<String> createTextChangeObservable() {
//        //2
//        Observable<String> textChangeObservable = Observable.create(new ObservableOnSubscribe<String>() {
//            @Override
//            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
//                //3
//                final TextWatcher watcher = new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//                    @Override
//                    public void afterTextChanged(Editable s) {}
//
//                    //4
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                        emitter.onNext(s.toString());
//                    }
//                };
//
//                //5
//                mQueryEditText.addTextChangedListener(watcher);
//
//                //6
//                emitter.setCancellable(new Cancellable() {
//                    @Override
//                    public void cancel() throws Exception {
//                        mQueryEditText.removeTextChangedListener(watcher);
//                    }
//                });
//            }
//        });
//
//        return textChangeObservable
//                .filter(new Predicate<String>() {
//                    @Override
//                    public boolean test(String query) throws Exception {
//                        return query.length() >= 2;
//                    }
//                }).debounce(1000, TimeUnit.MILLISECONDS);  // add this line
//    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private static OkHttpClient mOkHttpClient;

    // 配置OkHttpClient
    private static void initOkHttpClient() {
        if (mOkHttpClient == null) {
            // 因为BaseUrl不同所以这里Retrofit不为静态，但是OkHttpClient配置是一样的,静态创建一次即可
            File cacheFile = new File(MyApplication.getContext().getCacheDir(),
                    "HttpCache"); // 指定缓存路径
            Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); // 指定缓存大小100Mb
            // 云端响应头拦截器，用来配置缓存策略
            Interceptor rewriteCacheControlInterceptor = new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    if (!NetUtil.isConnected(MyApplication.getContext())) {
                        request = request.newBuilder()
                                .cacheControl(CacheControl.FORCE_CACHE).build();
                        Logger.e("no network");
                    }
                    okhttp3.Response originalResponse = chain.proceed(request);
                    if (NetUtil.isConnected(MyApplication.getContext())) {
                        //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
                        String cacheControl = request.cacheControl().toString();
                        return originalResponse.newBuilder()
                                .header("Cache-Control", cacheControl)
                                .removeHeader("Pragma").build();
                    } else {
                        return originalResponse.newBuilder().header("Cache-Control",
                                "public, only-if-cached," + CACHE_STALE_SEC)
                                .removeHeader("Pragma").build();
                    }
                }
            };
//            mOkHttpClient = new OkHttpClient();
//            mOkHttpClient.setCache(cache);
//            mOkHttpClient.networkInterceptors().add(rewriteCacheControlInterceptor);
//            mOkHttpClient.interceptors().add(rewriteCacheControlInterceptor);
//            mOkHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
            //okhttp 3

//            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(
                        new HttpLoggingInterceptor.Logger() {
                            @Override
                            public void log(String message) {
                                Logger.i("message",message);
                            }
                        });
                logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                mOkHttpClient = new OkHttpClient.Builder().cache(cache)
                        .addNetworkInterceptor(rewriteCacheControlInterceptor)
                        .addInterceptor(rewriteCacheControlInterceptor)
                        .addInterceptor(logInterceptor)
                        .connectTimeout(10, TimeUnit.SECONDS).build();
//            }
        }
    }

    /**
     * 根据网络状况获取缓存的策略
     *
     * @return
     */
    @NonNull
    public static String getCacheControl() {
        return NetUtil.isConnected(MyApplication.getContext()) ? CACHE_CONTROL_NETWORK : CACHE_CONTROL_CACHE;
    }
}
