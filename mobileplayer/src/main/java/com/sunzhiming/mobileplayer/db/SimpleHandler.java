package com.sunzhiming.mobileplayer.db;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.widget.CursorAdapter;

import com.sunzhiming.mobileplayer.util.Utils;

/**
 * Created by sunzhiming on 2016/8/14.
 */
public class SimpleHandler extends AsyncQueryHandler {
    public SimpleHandler(ContentResolver cr) {
        super(cr);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        super.onQueryComplete(token, cookie, cursor);

        if (cookie != null && cookie instanceof CursorAdapter){
            CursorAdapter cursorAdapter = (CursorAdapter) cookie;
            //更新数据
            cursorAdapter.changeCursor(cursor);
        }
        Utils.printCursor(cursor);

    }
}
