package com.moses.lib_network.okhttp.exception;

/*
 * 自定义异常类，返回ecode、emsg到业务层
 *
 * 只要看到OkHttpException这个自定义异常，就直到一定是封装的网络请求框架报的错
 */
public class OkHttpException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * the server return code
     */
    private int ecode;

    /**
     * the server return error message
     */
    private Object emsg;

    public OkHttpException(int ecode, Object emsg) {
        this.ecode = ecode;
        this.emsg = emsg;
    }

    public int getEcode() {
        return ecode;
    }

    public Object getEmsg() {
        return emsg;
    }
}
