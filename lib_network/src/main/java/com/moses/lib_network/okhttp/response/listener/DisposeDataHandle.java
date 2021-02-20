package com.moses.lib_network.okhttp.response.listener;

/**
 * 包装类：
 * 包装了Listener、要解析成的对象类型mClass、文件保存路径
 * @author vision
 *
 */
public class DisposeDataHandle {
    public DisposeDataListener mListener = null;
    public Class<?> mClass = null;
    public String mSource = null; // 文件保存路径

    public DisposeDataHandle(DisposeDataListener listener)
    {
        this.mListener = listener;
    }

    public DisposeDataHandle(DisposeDataListener listener, Class<?> clazz)
    {
        this.mListener = listener;
        this.mClass = clazz;
    }

    public DisposeDataHandle(DisposeDataListener listener, String source)
    {
        this.mListener = listener;
        this.mSource = source;
    }
}