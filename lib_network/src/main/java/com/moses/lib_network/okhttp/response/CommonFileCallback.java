package com.moses.lib_network.okhttp.response;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.moses.lib_network.okhttp.exception.OkHttpException;
import com.moses.lib_network.okhttp.response.listener.DisposeDataHandle;
import com.moses.lib_network.okhttp.response.listener.DisposeDownloadListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 处理文件类型的响应
 * @文件描述：专门处理文件下载回调
 */
public class CommonFileCallback implements Callback {
    /**
     * the java layer exception, do not same to the logic error
     */
    protected final int NETWORK_ERROR = -1; // the network relative error
    protected final int IO_ERROR = -2; // the JSON relative error
    protected final String EMPTY_MSG = "";
    /**
     * 将其它线程的数据转发到UI线程
     */
    private static final int PROGRESS_MESSAGE = 0x01; // 处理文件响应进度时的一个实现信息
    private Handler mDeliveryHandler; // 将响应数据从子线程转到UI主线程
    private DisposeDownloadListener mListener; // 业务层的回调
    private String mFilePath; // 文件保存的路径
    private int mProgress; // 需要处理进度，对进度进行计算，这里是当前进度

    /**
     * 构造方法，主要用于初始化声明的相关全局变量
     * @param handle
     */
    public CommonFileCallback(DisposeDataHandle handle) {
        this.mListener = (DisposeDownloadListener) handle.mListener;
        // 初始化FilePath
        this.mFilePath = handle.mSource;
        // 初始化mDeliveryHandler
        this.mDeliveryHandler = new Handler(Looper.getMainLooper()) {
            /**
             * 因为需要处理进度，所以需要重写handleMessage方法
             * @param msg
             */
            @Override
            public void handleMessage(Message msg) {
                // 如果msg.what是我们定义的PROGRESS_MESSAGE，就直接回调进度
                // 不管有几个case，写switch都会比较好，方便以后的扩展
                switch (msg.what) {
                    case PROGRESS_MESSAGE:
                        mListener.onProgress((int) msg.obj);
                        break;
                }
            }
        };
    }

    // onFailure和onResponse处理响应逻辑
    /**
     * 这里的onFailure处理和我们之前CommonJsonCallback中的onFailure的处理完全一致
     * @param call
     * @param ioexception
     */
    @Override
    public void onFailure(final Call call, final IOException ioexception) {
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onFailure(new OkHttpException(NETWORK_ERROR, ioexception));
            }
        });
    }

    /**
     *
     * @param call
     * @param response
     * @throws IOException
     */
    @Override
    public void onResponse(Call call, Response response) throws IOException {
        // 同样通过handleResponse方法来处理响应
        final File file = handleResponse(response);
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                // 文件不为null，表明文件保存成功
                if (file != null) {
                    // 回调请求成功方法，将文件回调到业务层
                    mListener.onSuccess(file);
                }
                // 否则，文件请求失败或者文件保存失败
                else {
                    // 回调请求失败方法
                    mListener.onFailure(new OkHttpException(IO_ERROR, EMPTY_MSG));
                }
            }
        });
    }

    /**
     * 处理响应的方法：
     * 需要我们从服务端拿到文件的字节流，将流写入到文件中
     * 此时还在子线程中，不必调用回调接口
     *
     * @param response
     * @return
     */
    private File handleResponse(Response response) {
        // 如果response为null，表明请求失败，直接return一个null
        if (response == null) {
            return null;
        }

        // 否则，请求成功，要开始处理和解析文件字节流，并保存文件。
        // 创建输入字节流
        InputStream inputStream = null;
        // 创建文件
        File file;
        // 创建输出字节流，写入响应字节流内容到要保存的文件
        FileOutputStream fos = null;
        // 缓存byte数组，确定每次写多少，缓存大小最好是1024的整数倍
        byte[] buffer = new byte[2048];
        int length;
        // 定义当前读写的长度   主要用于计算进度
        int currentLength = 0;
        // 文件总长度  主要用于计算进度
        double sumLength;

        // 开始实现处理逻辑
        // try块的功能：从输入流中读取，写入到输出流
        try {
            // 判断当前文件是否存在，如果不存在，需要创建文件
            checkLocalFilePath(mFilePath);
            // 获取文件路径
            file = new File(mFilePath);
            // 输出字节到文件中
            fos = new FileOutputStream(file);
            inputStream = response.body().byteStream();
            // 文件总长度
            sumLength = (double) response.body().contentLength();

            // 有了输入输出流之后，开始循环读取数据   如果不等于-1，表明没有读完
            while ((length = inputStream.read(buffer)) != -1) {
                // 写数据，从0到 length   把缓存的buffer中的数据都写入到fos字节输出流中
                fos.write(buffer, 0, length);
                // 写完成之后，对当前读写的长度进行累加
                currentLength += length;
                // 计算进度
                mProgress = (int) (currentLength / sumLength * 100);
                // 发送到handler对应的message ？？ 中
                mDeliveryHandler.obtainMessage(PROGRESS_MESSAGE, mProgress).sendToTarget();
            }
            // 循环执行完毕，文件读写完毕，必须要关闭流
            fos.flush();
        } catch (Exception e) {
            file = null;
        }
        // 因为有流的读写，所以需要写个finally ，无论怎样finally中的代码块都是要执行的，这里主要用来关闭流
        finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                file = null;
                e.printStackTrace();
            }
        }
        return file;
    }

    private void checkLocalFilePath(String localFilePath) {
        File path = new File(localFilePath.substring(0,
                localFilePath.lastIndexOf("/") + 1));
        File file = new File(localFilePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
