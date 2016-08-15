package com.sunzhiming.mobileplayer.base;

import android.view.View;

/**
 * Created by sunzhiming on 2016/8/14.
 */
public interface UiOperation extends View.OnClickListener{
     int getLayoutId();

     void initData();

    void setListener();

}
