package com.example.android.smartsms.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.smartsms.R;

import java.util.IllegalFormatCodePointException;

/**
 * Created by Administrator on 2016/4/9.
 */
public class AutoSearchAdapter extends CursorAdapter{


    public AutoSearchAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return View.inflate(context,R.layout.item_auto_search_tv,null);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder holder = getHolder(view);
        holder.tv_autosearch_name.setText(cursor.getString(cursor.getColumnIndex("display_name")));
        holder.tv_autosearch_address.setText(cursor.getString(cursor.getColumnIndex("data1")));

    }

    public ViewHolder getHolder(View view){

        ViewHolder holder= (ViewHolder) view.getTag();
        if (holder==null){

            holder=new ViewHolder(view);
            view.setTag(holder);
        }
        return holder;
    }
    class ViewHolder{//拿到所有的组件对象

        private TextView tv_autosearch_name;
        private TextView tv_autosearch_address;

        public ViewHolder(View view){

            tv_autosearch_name= (TextView) view.findViewById(R.id.tv_autosearch_name);
            tv_autosearch_address= (TextView) view.findViewById(R.id.tv_autosearch_address);
        }

    }

    //点击下拉列表时返回号码，我还得查看其是什么时候被调用的
    public CharSequence convertToString(Cursor cursor){

        return cursor.getString(cursor.getColumnIndex("data1"));
    }
}
