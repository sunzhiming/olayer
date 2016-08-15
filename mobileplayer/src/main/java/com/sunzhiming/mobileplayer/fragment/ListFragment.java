package com.sunzhiming.mobileplayer.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.sunzhiming.mobileplayer.R;
import com.sunzhiming.mobileplayer.activity.VideoPlayActivity;
import com.sunzhiming.mobileplayer.adapter.VideoListAdapter;
import com.sunzhiming.mobileplayer.base.BaseFragment;
import com.sunzhiming.mobileplayer.bean.VideoInfo;
import com.sunzhiming.mobileplayer.db.SimpleHandler;
import com.sunzhiming.mobileplayer.util.Constant;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ListFragment extends BaseFragment {
    @Bind(R.id.listview)
    ListView mListview;
    private CursorAdapter mListAdapter;

    public static ListFragment getInstance(Bundle args) {
        ListFragment listFragment = new ListFragment();
        listFragment.setArguments(args);
        return listFragment;
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_list;
    }


    @Override
    public void setListener() {
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor) mListAdapter.getItem(position);
                //将视频数据传递给播放界面
                Intent intent = new Intent(getActivity(),VideoPlayActivity.class);
                intent.putExtra("currntVideo",position);
                intent.putExtra("videoList",cusorToList(cursor));
                startActivity(intent);
            }
        });
    }

    private  ArrayList<VideoInfo> cusorToList(Cursor cursor) {
        ArrayList<VideoInfo> list = new ArrayList<>();
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()){
            list.add(VideoInfo.fromCursor(cursor));
        }
        return list;
    }

    @Override
    public void initData() {
        //1.给listview设置adapter
        SimpleHandler simpleHandler = new SimpleHandler(getActivity().getContentResolver());
        Bundle bundle = getArguments();
        int type = bundle.getInt("type");
        if (type == Constant.VDIEO) {
            mListAdapter = new VideoListAdapter(getActivity(),null);
            //获取视频
            //通过系统对外暴露的内容提供者获取多媒体数据
            //由于原生系统只认识MP3,3gp，所以该方法是无法获取到avi等其他格式的视频的
            Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;//sd卡上视频的uri路径
            String[] projection = {MediaStore.Video.Media._ID , MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.SIZE
                    , MediaStore.Video.Media.DATA};//定义所查询的列

            //在主线程查询，会造成UI阻塞
//            Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);

            //使用AsyncQueryHandler进行异步查询
            simpleHandler.startQuery(0, mListAdapter, uri, projection, null, null, null);
        } else if (type == Constant.MIUSIC) {



        }
        mListview.setAdapter(mListAdapter);
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
