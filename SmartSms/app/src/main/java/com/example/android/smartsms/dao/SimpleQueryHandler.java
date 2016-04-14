package com.example.android.smartsms.dao;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.widget.CursorAdapter;

import com.example.android.smartsms.utils.CursorUtils;

/**
 * Created by Administrator on 2016/4/9.
 */

/**
 * 获取具体的短信条目
 */
public class SimpleQueryHandler extends AsyncQueryHandler {

    public SimpleQueryHandler(ContentResolver cr) {
        super(cr);
    }

    //查询完毕时调用
    //arg0、arg1：查询开始时携带的数据
    //arg2:查询结果
    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        super.onQueryComplete(token, cookie, cursor);
//		CursorUtils.printCursor(cursor);
        if(cookie != null && cookie instanceof CursorAdapter){
            //查询得到的cursor，交给CursorAdapter，由它把cursor的内容显示至listView
            ((CursorAdapter)cookie).changeCursor(cursor);
        }
    }
}
