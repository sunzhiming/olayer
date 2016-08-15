package com.sunzhiming.mobileplayer.bean;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;

/**
 * Created by sunzhiming on 2016/8/14.
 */
public class VideoInfo implements Serializable{

    public String title;
    public String path;
    public long time;
    public long size;

   public static VideoInfo fromCursor(Cursor cursor){
       VideoInfo videoInfo = new VideoInfo();
       videoInfo.title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
       videoInfo.path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
       videoInfo.time = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
       videoInfo.size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
       return videoInfo;
   }
}
