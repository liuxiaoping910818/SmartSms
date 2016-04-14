package com.example.android.smartsms.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.smartsms.R;
import com.example.android.smartsms.bean.Group;

/**
 * Created by Administrator on 2016/4/9.
 */
public class GroupListAdapter  extends CursorAdapter{


    public GroupListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return View.inflate(context,R.layout.item_group_list,null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //拿到组件
        ViewHolder holder=getHolder(view);

        //拿到显示的数据,将结果集转化为一个bean对象
        Group group=Group.createFromCursor(cursor);
        holder.tv_groupList_name.setText(group.getName()+"("+group.getThread_count()+")");
        if (DateUtils.isToday(group.getCreate_date())){

            holder.tv_groupList_date.setText(DateFormat.getTimeFormat(context).format(group.getCreate_date()));

        }


    }
    private ViewHolder getHolder(View view){

        ViewHolder holder= (ViewHolder) view.getTag();
        if (holder==null){

            holder=new ViewHolder(view);
        }

        return holder;
    }

    class ViewHolder{

        private TextView tv_groupList_name;
        private TextView tv_groupList_date;

        public ViewHolder(View view){

            tv_groupList_name= (TextView) view.findViewById(R.id.tv_grouplist_name);
            tv_groupList_date= (TextView) view.findViewById(R.id.tv_grouplist_date);
        }
    }

}
