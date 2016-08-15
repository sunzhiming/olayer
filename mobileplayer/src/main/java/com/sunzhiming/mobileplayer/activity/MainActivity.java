package com.sunzhiming.mobileplayer.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sunzhiming.mobileplayer.R;
import com.sunzhiming.mobileplayer.adapter.MainAdapter;
import com.sunzhiming.mobileplayer.base.BaseeActivity;
import com.sunzhiming.mobileplayer.fragment.ListFragment;
import com.sunzhiming.mobileplayer.util.Constant;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends BaseeActivity  {
    @Bind(R.id.tv_vido)
    TextView mTvVido;
    @Bind(R.id.tv_music)
    TextView mTvMusic;
    @Bind(R.id.line)
    View mLine;
    @Bind(R.id.viewpager)
    ViewPager mViewpager;
    private ArrayList<Fragment> mFragments;
    private int mLineWidth;


    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {

        mFragments = new ArrayList<>();
        mFragments.add(ListFragment.getInstance(getBundle(Constant.VDIEO)));
        mFragments.add(ListFragment.getInstance(getBundle(Constant.MIUSIC)));
        MainAdapter mainAdapter = new MainAdapter(getSupportFragmentManager(), mFragments);
        mViewpager.setAdapter(mainAdapter);
        //更新标题
        upDataTitle();

        //初始化指示线的宽度
        initLine();

    }

    private Bundle getBundle(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt("type",type);
        return bundle;
    }

    private void initLine() {
        //1，计算线宽
        mLineWidth = getWindowManager().getDefaultDisplay().getWidth() / mFragments.size();
        //2，设置给线
        ViewGroup.LayoutParams params = mLine.getLayoutParams();
        params.width = mLineWidth;
        mLine.setLayoutParams(params);

    }

    @Override
    public void setListener() {
        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             * viewpager滑动改变的时候，调用
             * @param position
             * @param positionOffset
             * @param positionOffsetPixels
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //计算线要滚动的距离
                float targetX = mLine.getWidth()*position+positionOffsetPixels/mFragments.size();
                //2,让线移动距离
                ViewCompat.setTranslationX(mLine,targetX);
            }

            @Override
            public void onPageSelected(int position) {
                upDataTitle();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 更改标题样式
     */
    private void upDataTitle() {
        //1，获取当前viewpager选择的页
        int currentItem = mViewpager.getCurrentItem();
        //2,根据当前页来设置标题颜色
        mTvVido.setSelected(currentItem == 0);
        mTvMusic.setSelected(currentItem == 1);


        //根据当前页缩放标题
        ViewCompat.animate(mTvVido)
                .scaleX(currentItem == 0 ? 1f :0.8f)
                .scaleY(currentItem == 0 ? 1f :0.8f)
                .setDuration(400).start();
        ViewCompat.animate(mTvMusic)
                .scaleX(currentItem == 1 ? 1f :0.8f)
                .scaleY(currentItem == 1 ? 1f :0.8f)
                .setDuration(400).start();
    }


    @OnClick({R.id.tv_vido, R.id.tv_music})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_vido:
                mViewpager.setCurrentItem(0);
                break;
            case R.id.tv_music:
                mViewpager.setCurrentItem(1);
                break;
        }
    }
}
