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
     * 重载一个不带Headers的createPostRequest方法 ，因为平时开发中我们自定义请求头headers的情况非常少，
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
     * 对外创建post请求对象，带请求头headers
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

    /**
     * 重载一个不带Headers的createGetRequest方法
     * @param url
     * @param params
     * @return
     */
    public static Request createGetRequest(String url, RequestParams params){
        return createGetRequest(url ,params, null);
    }
    /**
     * 带请求头的Get请求
     * @param url
     * @param params
     * @param headers
     * @return
     */
    public static Request createGetRequest(String url, RequestParams params, RequestParams headers){
        // 1、构建参数
        // 由于get类型的请求，请求参数是在请求url之后的，所以首先通过一个StringBuilder将请求参数params缀到url之后
        StringBuilder urlBuilder = new StringBuilder(url).append("?");
        if (params != null){
            // 参数遍历
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                urlBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
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

        Headers mHeader = mHeaderBuilder.build();
        return new Request.Builder().
                url(urlBuilder.substring(0, urlBuilder.length() - 1))
                .headers(mHeader)
                .get()
                .build();
    }

    /**
     * 封装文件上传类型请求
     * 文件上传需要使用Post类型
     * @param url
     * @param params
     * @return
     */
    // 实现封装文件请求之前，先定义一个多媒体的请求类型
    public static final MediaType FILE_TYPE =  MediaType.parse("application/octet-stream");
    public static Request createMultiPostRequest(String url, RequestParams params){
        // 构造者模式创建requestbody对象
        MultipartBody.Builder  requestBody = new MultipartBody.Builder();
        // 由于是文件上传请求，所以我们指定为表单类型来提交
        requestBody.setType(MultipartBody.FORM);
        // 开始遍历请求参数
        if (params != null) {
            // 由于是文件上传，这里的Value类型不能用String，需要使用Object来代替；使用fileParams
            for (Map.Entry<String, Object> entry : params.fileParams.entrySet()) {
                // 将参数添加到MultipartBody类型的requestBody中

                // 如果entry的值类型是一个文件
                if (entry.getValue() instanceof File) {
                    // 添加请求头部分   RequestBody的创建中要传递媒体类型
                    requestBody.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
                            RequestBody.create(FILE_TYPE, (File) entry.getValue()));
                }
                // 如果entry是一个json字符串或者json实体对象，本质上是一个String类型
                else if (entry.getValue() instanceof String) {
                    // 添加请求头部分
                    requestBody.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
                            RequestBody.create(null, (String) entry.getValue()));
                }
            }
        }
        return new Request.Builder().url(url).post(requestBody.build()).build();
    }

}
