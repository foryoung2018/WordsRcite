# WordsRcite

### 拆解背词，随时随地听单词

--------------------------------------------------------------------------------

### ---2018/5/23
//TODO  Fragmentation   https://github.com/YoKeyword/Fragmentation.git  待加入

--------------------------------------------------------------------------------

### ---2018/5/24
//TODO  RetrofitDemo   https://github.com/Carson-Ho/RetrofitDemo.git 待加入

--------------------------------------------------------------------------------

### ---2018/5/25
//TODO  RxAndroid   待加入

--------------------------------------------------------------------------------

### ---2018/5/29
加入有道翻译接口
加入HttpLoggingInterceptor  RxAndroid
````
OkHttpClient client = new OkHttpClient();
HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
logging.setLevel(Level.BASIC);
client.interceptors().add(logging);

/* 可以通过 setLevel 改变日志级别
 共包含四个级别：NONE、BASIC、HEADER、BODY

NONE 不记录

BASIC 请求/响应行
--> POST /greeting HTTP/1.1 (3-byte body)
<-- HTTP/1.1 200 OK (22ms, 6-byte body)

HEADER 请求/响应行 + 头

--> Host: example.com
Content-Type: plain/text
Content-Length: 3

<-- HTTP/1.1 200 OK (22ms)
Content-Type: plain/text
Content-Length: 6

BODY 请求/响应行 + 头 + 体
*/

// 可以通过实现 Logger 接口更改日志保存位置
HttpLoggingIntercetptor logging = new HttpLoggingInterceptor(new Logger() {
    @Override
    public void log(String message) {
        Timber.tag("okhttp").d(message);
    }
});
```

//TODO插件化，待加入
