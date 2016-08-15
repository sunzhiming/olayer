package com.sunzhiming.mobileplayer.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;

import com.sunzhiming.mobileplayer.R;
import com.sunzhiming.mobileplayer.base.BaseeActivity;

import butterknife.Bind;

/**
 * Created by sunzhiming on 2016/8/14.
 */
public class SplashActivity extends BaseeActivity {
    @Bind(R.id.ll)
    LinearLayout mLl;

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initData() {

        //进入主界面，一般在欢迎界面初始化数据
        int height = getWindowManager().getDefaultDisplay().getHeight();
        //1，初始化的时候，让mLl移动到下面
        ViewCompat.setTranslationY(mLl,height);
        //2，执行向上移动的动画
        ViewCompat.animate(mLl).translationY(0f)
                .setDuration(800)
                .setStartDelay(500)
                .setInterpolator(new OvershootInterpolator(2))//设置弹性
                .start();


        //当动画结束后，跳转到主界面

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }
        },1500);



    }

    @Override
    public void setListener() {

    }

    /**
     * 当正在进行动画的时候，不让用户按 退出键
     */
    @Override
    public void onBackPressed() {
//        super.onBackPressed();

    }
}
