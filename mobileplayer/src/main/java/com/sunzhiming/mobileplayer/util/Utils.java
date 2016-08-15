package com.sunzhiming.mobileplayer.util;

import android.database.Cursor;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sunzhiming on 2016/8/14.
 */
public class Utils {
    public static final  String TAG = "utils";
    public static Cursor printCursor(Cursor cursor){
        if (cursor == null) return null;
        //打印有多少条记录
         Log.i(TAG,"共"+cursor.getCount()+"条记录");

        while(cursor.moveToNext()){
            int columnCount = cursor.getColumnCount();
            for (int i = 0;i<columnCount;i++){
                String columnName = cursor.getColumnName(i);

                String columValue = cursor.getString(i);

                 Log.i("tag",columnName+"+++++++=="+columValue);
            }
        }
        return cursor;

    }

    /**
     * 将long类型的时间转为01：23：33格式
     */
    public static String formatDuration(long duration){

        int HOUR = 60*60*1000;
        int MINUTE = 60*1000;
        int SEOND = 1000;
        //1,先计算有多少小时
        int h = (int) (duration / HOUR);//计算有多少小时
        int remain = (int)duration % HOUR;
        //2,计算有分钟
        int m = (int) remain / MINUTE;
        remain = remain % MINUTE;
        //3,计算有多少秒
        int s = (int) remain / SEOND;

        //组装成01：23：33格式，如果不足1小时
        if (h == 0){
            //说明不足一小时
            return String.format("%02d:%02d",m,s);
        }else{
            //有一小时多
            return String.format("%02d:%02d:%02d",h,m,s);
        }
    }

    public static String formartSystemTime(){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
       return format.format(new Date());
    }
}
