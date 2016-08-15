package com.sunzhiming.mobileplayer.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by sunzhiming on 2016/8/14.
 */
public abstract class BaseFragment extends Fragment implements UiOperation{
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {

        View view = inflater.inflate(getLayoutId(),null);
        ButterKnife.bind(this, view);
        return view;
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //设置监听器
        setListener();
        //初始化数据
        initData();
    }

    @Override
    public void onClick(View view) {

    }
}
