package com.moses.moses_voice.view.home.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.moses.moses_voice.view.Friend.FriendFragment;
import com.moses.moses_voice.view.discovery.DiscoveryFragment;
import com.moses.moses_voice.view.home.model.CHANNEL;
import com.moses.moses_voice.view.mine.MineFragment;

/**
 *  首页ViewPager的Adapter。
 *
 *   因为首页只有3个Fragment，所以不需要使用FragmentStatePagerAdapter ？ 不需要保存状态
 *   只需要继承使用简单的FragmentPagerAdapter即可。
 *   至于FragmentPagerAdapter和FragmentStatePagerAdapter的区别，自己查阅资料学习。
 */
public class HomePagerAdapter extends FragmentPagerAdapter {

    private CHANNEL[] mList;

    /**
     * 构造方法
     * @param fm
     * @param datas
     */
    public HomePagerAdapter(FragmentManager fm, CHANNEL[] datas) {
        super(fm);
        mList = datas;
    }

    // 初始化对应的Fragment，获取对应数据类型的Fragment实例
    // 这里使用到了单例模式。这种方式，避免首页一次性创建所有类型的4个framgent，造成首页卡顿。我们这里滑动到某一个类型，再去创建对应的Fragment。
    @Override
    public Fragment getItem(int iPosition) {
        // 获取数据类型
        int type = mList[iPosition].getValue();
        switch (type) {
            case CHANNEL.MINE_ID:
                return MineFragment.newInstance();
            case CHANNEL.DISCORY_ID:
                return DiscoveryFragment.newInstance();
            case CHANNEL.FRIEND_ID:
                return FriendFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.length;
    }

}
