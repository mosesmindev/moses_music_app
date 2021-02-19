package com.moses.moses_voice.view.mine;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.moses.moses_voice.view.Friend.FriendFragment;

public class MineFragment  extends Fragment{
    private Context mContext;

    public static Fragment newInstance(){
        MineFragment fragment = new MineFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

}
