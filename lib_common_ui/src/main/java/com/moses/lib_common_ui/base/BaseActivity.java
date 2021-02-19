package com.moses.lib_common_ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.moses.lib_common_ui.utils.StatusBarUtil;

public class BaseActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 整个app的沉浸式效果的第2部分工作在这里用Java代码实现，
         * 这里实现完成后，整个app中所有activity页面都会有沉浸式效果，就不需要在每个Activity页面中再一个个加效果了
         * 实现沉浸式效果，这里需要使用到一个第三方工具类：StatusBarUtil
         *
         * 备注：第1部分工作是在moses_voice的AndroidManifest. xml文件中配置android:theme为v21 style.xml
         */
        // 将状态栏模式变为黑色
        StatusBarUtil.statusBarLightMode(this);

    }
}
