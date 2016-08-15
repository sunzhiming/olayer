package com.sunzhiming.mobileplayer.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by sunzhiming on 2016/8/14.
 */
public abstract class BaseeActivity extends AppCompatActivity implements UiOperation {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);


        //设置监听
        setListener();
        //初始化数据
        initData();
    }

    @Override
    public void onClick(View view) {

    }


}
