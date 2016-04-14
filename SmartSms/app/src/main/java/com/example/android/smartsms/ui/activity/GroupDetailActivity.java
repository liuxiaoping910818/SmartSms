package com.example.android.smartsms.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.smartsms.R;
import com.example.android.smartsms.adapter.ConversationListAdapter;
import com.example.android.smartsms.base.BaseActivity;
import com.example.android.smartsms.bean.Conversation;
import com.example.android.smartsms.dao.SimpleQueryHandler;
import com.example.android.smartsms.globle.Constant;

/**
 * Created by Administrator on 2016/4/11.创建群组且的显示具体内容的的aCTIVITY
 */
public class GroupDetailActivity extends BaseActivity {
    private String groupname;
    private int groupId;
    private ListView lv_group_detail;
    private SimpleQueryHandler queryHandler;
    private ConversationListAdapter adapter;

    @Override
    public void initView() {

        setContentView(R.layout.activity_group_detail);
        lv_group_detail= (ListView) findViewById(R.id.lv_group_detail);


    }

    @Override
    public void initListener() {

        lv_group_detail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //直接跳转至ConversationDetailActivity
                //进入会话详细
                Intent intent=new Intent(GroupDetailActivity.this,ConversationDetailActivity.class);
                //携带数据：address和thread_id
                Cursor cursor= (Cursor) adapter.getItem(position);
                Conversation conversation=Conversation.createFromCursor(cursor);

                intent.putExtra("address",conversation.getAddress());
                intent.putExtra("thread_id",conversation.getThread_id());
                startActivity(intent);
            }
        });


    }

    @Override
    public void initData() {

        Intent intent=getIntent();
        groupname=intent.getStringExtra("name");
        groupId=intent.getIntExtra("group_id", -1);
        initTitleBar();


        //复用会话列表的所有组件、参数、条目布局、查询字段、表
        //只要查询的条件不一样
        adapter=new ConversationListAdapter(this,null);
        lv_group_detail.setAdapter(adapter);

        String[] projection = {
                "sms.body AS snippet",
                "sms.thread_id AS _id",
                "groups.msg_count AS msg_count",
                "address AS address",
                "date AS date"
        };

        queryHandler=new SimpleQueryHandler(getContentResolver());
        queryHandler.startQuery(0,adapter,Constant.URI.URI_SMS_CONVERSATION,projection,buildQuery(),null,null);



    }

    @Override
    public void processClick(View v) {

        switch (v.getId()){

            case R.id.iv_titlebar_back_btn:
                finish();//退出当前activity
                break;
        }

    }

    public void initTitleBar(){

        ((TextView)findViewById(R.id.tv_titlebar_title)).setText(groupname);
        findViewById(R.id.iv_titlebar_back_btn).setOnClickListener(this);
    }

    private String buildQuery(){

        Cursor cursor=getContentResolver().query(Constant.URI.URI_THREAD_GROUP_QUERY,new  String[]{"thread_id"},"group_id="+groupId,null,null);
        String selection = "thread_id in (";
        while (cursor.moveToNext()) {
            if(cursor.isLast())
                //最后一个会话id后面不要逗号
                selection += cursor.getString(0);
            else
                selection += cursor.getString(0) + ", ";
        }
        selection += ")";
        return selection;

    }
}
