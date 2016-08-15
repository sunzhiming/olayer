package com.sunzhiming.mobileplayer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.sunzhiming.mobileplayer.R;

/**
 * Created by sunzhiming on 2016/8/15.
 */
public class MusicAdapter extends BaseAdapter{
    private Context mContext;
    public MusicAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return 0;
    }
    @Override
    public Object getItem(int i) {
        return null;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View view1 = View.inflate(mContext, R.layout.activity_music, null);
        return view1;
    }
}
