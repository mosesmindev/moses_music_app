package com.moses.moses_voice.api;

import com.moses.lib_network.okhttp.CommonOkHttpClient;
import com.moses.lib_network.okhttp.request.CommonRequest;
import com.moses.lib_network.okhttp.request.RequestParams;
import com.moses.lib_network.okhttp.response.listener.DisposeDataHandle;
import com.moses.lib_network.okhttp.response.listener.DisposeDataListener;
import com.moses.moses_voice.model.user.User;

/**
 * 请求中心
 * 包含app中所有的请求地址和请求接口
 * 即所有的请求接口都会在这个类中存放
 */
public class RequestCenter {


    /**
     * 常量（静态）内部类  HttpConstants  中：
     * 存放所有的请求地址，即存放所有的请求接口地址，都是一些静态常量
     */
    static class HttpConstants {
        private static final String ROOT_URL = "http://imooc.com/api";
        //private static final String ROOT_URL = "http://39.97.122.129";

        /**
         * 首页请求接口
         */
        private static String HOME_RECOMMAND = ROOT_URL + "/module_voice/home_recommand";

        private static String HOME_RECOMMAND_MORE = ROOT_URL + "/module_voice/home_recommand_more";

        private static String HOME_FRIEND = ROOT_URL + "/module_voice/home_friend";

        /**
         * 登陆接口
         */
        public static String LOGIN = ROOT_URL + "/module_voice/login_phone";
    }

    // 根据参数发送所有post请求
    public static void getRequest(String url, RequestParams params, DisposeDataListener listener,
                                  Class<?> clazz) { CommonOkHttpClient.post(CommonRequest.
                createPostRequest(url, params), new DisposeDataHandle(listener, clazz));  // listener：回调listener，通知业务层； clazz：要解析的对象目标类类型
    }


    /**
     * 用户登陆请求
     */
    public static void login(DisposeDataListener listener) {

        RequestParams params = new RequestParams();
        params.put("mb", "18734924592");
        params.put("pwd", "999999q");
        RequestCenter.getRequest(HttpConstants.LOGIN, params, listener, User.class);
    }
}

