package com.sunzhiming.mobileplayer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunzhiming.mobileplayer.R;
import com.sunzhiming.mobileplayer.bean.VideoInfo;
import com.sunzhiming.mobileplayer.util.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sunzhiming on 2016/8/14.
 */
public class VideoListAdapter extends CursorAdapter {
    public VideoListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    /**
     * 返回adapter布局文件
     * @param context
     * @param cursor
     * @param viewGroup
     * @return
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = View.inflate(context, R.layout.adapter_list, null);
        return view;
    }

    /**
     * 将cursor数据绑定到view上
     * @param view
     * @param context
     * @param cursor
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = ViewHolder.getHolder(view);
        //绑定数据
        //将cursor转为javabean
        VideoInfo videoInfo = VideoInfo.fromCursor(cursor);
        holder.mTitle.setText(videoInfo.title);
        holder.mSize.setText(Formatter.formatFileSize(context,videoInfo.size));
        holder.mTime.setText(Utils.formatDuration(videoInfo.time));
    }

    static class ViewHolder {
        @Bind(R.id.icon)
        ImageView mIcon;
        @Bind(R.id.title)
        TextView mTitle;
        @Bind(R.id.time)
        TextView mTime;
        @Bind(R.id.size)
        TextView mSize;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public static ViewHolder getHolder(View view){
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder == null){
                holder = new ViewHolder(view);
                view.setTag(holder);
            }
            return holder;
        }
    }
}
