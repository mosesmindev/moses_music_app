package com.moses.moses_voice.view.discovery;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public class DiscoveryFragment extends Fragment {

    private Context mContext;

    public static Fragment newInstance(){
        DiscoveryFragment  fragment = new DiscoveryFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }
}
