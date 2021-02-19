package com.moses.moses_voice.view.home;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import com.moses.lib_common_ui.pager_indicator.ScaleTransitionPagerTitleView;
import com.moses.moses_voice.R;
import com.moses.moses_voice.view.home.model.CHANNEL;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

/**
 * 首页Activity
 */
public class HomeActivity extends FragmentActivity implements View.OnClickListener {

    // 指定首页要出现的卡片
    private static final CHANNEL[] CHANNELS =
            new CHANNEL[]{CHANNEL.MY, CHANNEL.DISCORY, CHANNEL.FRIEND};


    /*
     * View的初始化
     */
    private DrawerLayout mDrawerLayout;
    private View mToggleView;
    private View mSearchView;
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        initData();
    }

    private void initData() {
    }

    private void initView() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggleView = findViewById(R.id.toggle_view);
        mToggleView.setOnClickListener(this);
        mSearchView = findViewById(R.id.search_view);
        mViewPager = findViewById(R.id.view_pager);
        //mViewPager.setAdapter(mAdapter);
        initMagicIndicator();
    }

    /**
     * 初始化ViewPager指示器的方法，这里用到了一个开源的指示器
     */
    private void initMagicIndicator() {
        // 初始化指示器MagicIndicator
        MagicIndicator magicIndicator = findViewById(R.id.magic_indicator);
        // 为View设置背景，防止它的背景和默认布局背景不一致
        magicIndicator.setBackgroundColor(Color.WHITE);
        // 为指示器创建一个Navigator，这都是MagicIndicator的官方用法
        CommonNavigator commonNavigator = new CommonNavigator(this);
        // 初始化commonNavigator，最重要的初始化工作是为它设置一个Adapter
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            /**
             * @return 共计有多少指示器
             */
            @Override
            public int getCount() {
                return CHANNELS == null ? 0 : CHANNELS.length;
            }

            /**
             * 比较重要的方法，初始化每个TitleView的效果。
             * @param context
             * @param index
             * @return
             */
            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                // 初始化TitleView，使用带播放效果的ScaleTransitionPagerTitleView
                SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
                // 为simplePagerTitleView设置文字、颜色、字体等
                // 设置问题
                simplePagerTitleView.setText(CHANNELS[index].getKey());
                //  设置字体大小为19
                simplePagerTitleView.setTextSize(19);
                //  设置字体加粗
                simplePagerTitleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                // 设置默认的颜色为 ： #999999
                simplePagerTitleView.setNormalColor(Color.parseColor("#999999"));
                // 设置被选中的颜色为 ： #333333
                simplePagerTitleView.setSelectedColor(Color.parseColor("#333333"));
                // 点击事件：切换到ViewPager对应的页面中
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);// 设置当前展示的菜单内容的内容数量
                    }
                });
                return simplePagerTitleView;//返回simplePagerTitleView
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return null;
            }

            @Override
            public float getTitleWeight(Context context, int index) {
                return 1.0f;
            }
        });


        // 为指示器magicIndicator设置Navigator
        magicIndicator.setNavigator(commonNavigator);
        // 基本初步完成指示器初始化后，还要对指示器进行绑定   ViewPagerHelper是magicIndicator开源库提供的
        ViewPagerHelper.bind(magicIndicator, mViewPager);

    }

    @Override
    public void onClick(View view) {

    }
}
