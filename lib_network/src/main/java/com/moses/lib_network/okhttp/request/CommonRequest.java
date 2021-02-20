package com.moses.lib_network.okhttp.request;

import java.io.File;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 公共的网络请求类，类中会提供：
 * get方法
 * post方法
 * 文件上传方法
 * ……
 *
 * 等方法供应用层去调用，即对外提供：get/post/文件上传请求
 *
 */
public class CommonRequest {
    /**
     * 重载一个createPostRequest方法 ，因为平时开发中我们自定义请求头headers的情况非常少，
     * 大部分的headers都是在构造http的时候统一传好了。
     *
     * 直接调用封装好的带headers的createPostRequest，并且将headers参数置为null即可。
     * @param url
     * @param params
     * @return
     */
    public static Request createPostRequest(String url,RequestParams params){
        return createPostRequest(url ,params, null);
    }

    /**
     * 对外创建post请求对象
     * @param url
     * @param params
     * @param headers
     * @return
     */
  public static Request createPostRequest(String url,RequestParams params,RequestParams headers ){
      /**
       * 下面来构建Request对象，构建Request对象，需要首先构建请求体
       */

      // 使用FormBody的构建者模式创建请求参数体对象mFormBodyBuilder
      FormBody.Builder  mFormBodyBuilder = new FormBody.Builder();
      // 有了FormBody后，我们将请求参数放入FormBody类中
      // 1、构建请求参数
      if (params != null){
          for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
              // 请求参数遍历，将每个参数都放入FormBody类型的mFormBodyBuilder中
              mFormBodyBuilder.add(entry.getKey(), entry.getValue());
          }
      }


      // 使用Headers的构建者模式创建请求参数体对象mHeaderBuilder
      Headers.Builder mHeaderBuilder = new Headers.Builder();
      // 2、构建请求头：Headers
      if (headers != null) {
          for (Map.Entry<String, String> entry : headers.urlParams.entrySet()) {
              // 请求头遍历
              mHeaderBuilder.add(entry.getKey(), entry.getValue());
          }
      }

      // 3、请求参数体和请求头构建完成后，开始真正构建请求的Request对象
      FormBody mFormBody = mFormBodyBuilder.build();
      Headers mHeader = mHeaderBuilder.build();
      Request request = new Request.Builder()
              .url(url)
              .headers(mHeader)
              .post(mFormBody)
              .build();

      return request;

  }
}
