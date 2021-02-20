package com.moses.lib_network.okhttp.response;

import android.os.Handler;
import android.os.Looper;

import com.moses.lib_network.okhttp.exception.OkHttpException;
import com.moses.lib_network.okhttp.response.listener.DisposeDataHandle;
import com.moses.lib_network.okhttp.response.listener.DisposeDataListener;
import com.moses.lib_network.okhttp.utils.ResponseEntityToModule;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 处理Json类型的响应
 *
 * @author vision
 * @function 专门处理JSON的回调
 */
public class CommonJsonCallback implements Callback {
    /**
     * the logic layer exception, may alter in different app
     */
    protected final String EMPTY_MSG = "";

    /**
     * the java layer exception, do not same to the logic error
     */
    protected final int NETWORK_ERROR = -1; // the network relative error  表示网络层异常
    protected final int JSON_ERROR = -2; // the JSON relative error 表示Json解析异常
    protected final int OTHER_ERROR = -3; // the unknow error 表示配置类型异常

    // 有了数据，要对应用层进行响应，用到Listener
    private DisposeDataListener mListener;
    // 处理Json类型的响应，首先要有字节码数据，字节码表示json数据转成哪个类的对象
    private Class<?> mClass;
    // 获得到请求数据后，刚回调得到请求数据后，数据是在子线程中，需要用到Handler将数据发送到主线程(UI线程)中
    private Handler mDeliveryHandler; // 用来发送数据到UI主线程


    /**
     * CommonJsonCallback类的构造方法
     *
     * @param handle
     */
    public CommonJsonCallback(DisposeDataHandle handle) {
        // 初始化上述变量
        mListener = handle.mListener;
        mClass = handle.mClass;
        // 通过Looper.getMainLooper()的方式确保初始化的handler在主线程中
        mDeliveryHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 请求失败方法
     *
     * @param call
     * @param e
     */
    @Override
    public void onFailure(Call call, final IOException e) {
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                // 因为是失败的，所以直接抛个异常就好
                mListener.onFailure(new OkHttpException(NETWORK_ERROR, e));
            }
        });
    }

    /**
     * 响应成功方法，逻辑处理稍微复杂
     * @param call
     * @param response
     * @throws IOException
     */
    @Override
    public void onResponse(Call call, Response response) throws IOException {
        // 1、获取响应数据  response整体返回的一个字符串
        final String RESULT = response.body().string();  // 这个string方法就是okhhtp框架的ResponseBody类中将读取的byte数组转为String类型的string方法
        // 2、开始处理响应数据
        // 此时数据还在子线程中，所以通过mDeliveryHandler调用post方法进入UI主线程中
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                handleResponse(RESULT);
            }
        });
    }

    /**
     * 处理响应的方法
     * @param result
     */
    private void handleResponse(String result) {
        // 如果响应结果是null或者直接是个空字符串，视为异常
        if (result == null || result.trim().equals("")){
            mListener.onFailure(new OkHttpException(NETWORK_ERROR,EMPTY_MSG));
            return; // 继续往下走
        }

        // 否则，就对返回的结果RESULT  （onResponse定义的RESULT，很可能是json）进行解析了
        // 解析需要进行try catch ，避免解析过程中出错，比如给的实体对象和服务器返回的数据不匹配
        try{
            // 如果要解析的对象为null，表明应用层不想让网络层解析数据，只想要拿到服务器响应的最原始的数据
            if (mClass == null) {
                // 通过mListener.onSucces()返回最原始的数据
                mListener.onSuccess(result);
            }
            // 否则，就是需要网络框架帮助应用层解析数据对象
            else{
                // 将Object解析成对应的实体对象 ，可以使用任意的注入gson、fastjson等任意json解析库类代替ResponseEntityToModule.parseJsonToModule去解析
                Object obj = ResponseEntityToModule.parseJsonToModule(result,mClass);
                // 如果obj != null，表明解析成功，保证获取的这个obj一定是业务层（应用层）想要获取的实体响应对象
                if (obj != null) {
                    mListener.onSuccess(obj);
                }
                // 解析失败，将请求当作异常处理
                else {
                    mListener.onFailure(new OkHttpException(JSON_ERROR, EMPTY_MSG));
                }
            }
        }catch(Exception e){
            mListener.onFailure(new OkHttpException(OTHER_ERROR, e.getMessage()));
            e.printStackTrace();
        }

    }


}
