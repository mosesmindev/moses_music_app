package com.moses.lib_network.okhttp;

import com.moses.lib_network.okhttp.response.CommonFileCallback;
import com.moses.lib_network.okhttp.response.CommonJsonCallback;
import com.moses.lib_network.okhttp.response.listener.DisposeDataHandle;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author qndroid
 * @function 用来发送get, post请求的工具类，包括设置一些请求的共用参数
 */
public class CommonOkHttpClient {
    // 超时时间定义为30秒
    private static final int TIME_OUT = 30;
    // 单例模式的应用：整个工程中有一个OkHttpClient对象即可，所以定义一个单例的OkHttpClient对象mOkHttpClient
    private static OkHttpClient mOkHttpClient;

    // 在静态语句块中完成对OkHttpClient对象 mOkHttpClient 的初始化
    static {
        // 构建者builder模式
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        // hostnameVerifier：host name Verifier 主机名称匹配器  域名匹配器
        okHttpClientBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                // 表示默认是信任域名的
                return true;
            }
        });
        /**
         *  为所有请求添加请求头，看个人需求
         *  addInterceptor：添加拦截器   实现了匿名内部类的拦截器
         */
        okHttpClientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request =
                        // chain对象获取request对象，然后通过request的构建者模式添加请求头
                        // 这里只添加了一个自定义的请求头，如果还想再添加公共的请求头，还可以用同样的方式在拦截器中添加]
                        chain.request().newBuilder().addHeader("User-Agent", "Imooc-Mobile") // 标明发送本次请求的客户端
                                .build();
                // 将request加入到链中
                return chain.proceed(request);
            }
        });

        // 设置连接超时时间  时长都是TIME_OUT 30，单位都是秒
        okHttpClientBuilder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
        // 设置读超时时间
        okHttpClientBuilder.readTimeout(TIME_OUT, TimeUnit.SECONDS);
        // 设置写超时时间
        okHttpClientBuilder.writeTimeout(TIME_OUT, TimeUnit.SECONDS);
        // 设置为允许重定向
        okHttpClientBuilder.followRedirects(true);

        /**
         * trust all the https point
         */
        // 完成以上基本设置后，初始化mOkHttpClient
        mOkHttpClient = okHttpClientBuilder.build();
    }


    /**
     * 发送get类型的请求
     * @param request
     * @param handle
     * @return
     */
    public static Call get(Request  request, DisposeDataHandle handle){
        Call call = mOkHttpClient.newCall(request);
        // 将call入队列   call将请求request和响应CommonJsonCallback关联了起来
        call.enqueue(new CommonJsonCallback(handle));
        return call;
    }

    /**
     * 发送post类型的请求
     * 备注 ：文件上传和post的实现是一样的，需要文件上传的时候，需要上传文件的时候，可以直接单独调用post方法即可
     * @param request
     * @param handle
     * @return
     */
    public static Call post(Request  request, DisposeDataHandle handle){
        Call call = mOkHttpClient.newCall(request);
        // 将call入队列   call将请求request和响应CommonJsonCallback关联了起来
        call.enqueue(new CommonJsonCallback(handle));
        return call;
    }

    /**
     * 文件下载请求
     * @param request
     * @param handle
     * @return
     */
    public static Call downloadFile(Request request ,DisposeDataHandle handle){
        Call call = mOkHttpClient.newCall(request);
         call.enqueue(new CommonFileCallback(handle));
         return call;
    }

    public static OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

}
