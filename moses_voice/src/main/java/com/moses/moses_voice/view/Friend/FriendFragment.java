package com.moses.moses_voice.view.Friend;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.moses.moses_voice.view.discovery.DiscoveryFragment;

public class FriendFragment  extends Fragment{

    private Context mContext;

    public static Fragment newInstance(){
        FriendFragment fragment = new FriendFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }
}
