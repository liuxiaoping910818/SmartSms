package com.example.android.smartsms.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.android.smartsms.R;
import com.example.android.smartsms.adapter.ConversationListAdapter;
import com.example.android.smartsms.base.BaseFragment;
import com.example.android.smartsms.bean.Conversation;
import com.example.android.smartsms.bean.Group;
import com.example.android.smartsms.dao.GroupDao;
import com.example.android.smartsms.dao.GroupOpenHelper;
import com.example.android.smartsms.dao.SimpleQueryHandler;
import com.example.android.smartsms.dao.ThreadGroupDao;
import com.example.android.smartsms.dialog.ConfirmDialog;
import com.example.android.smartsms.dialog.DeleteMsgDialog;
import com.example.android.smartsms.dialog.ListDialog;
import com.example.android.smartsms.globle.Constant;
import com.example.android.smartsms.ui.activity.ConversationDetailActivity;
import com.example.android.smartsms.ui.activity.NewMsgActivity;
import com.example.android.smartsms.utils.ToastUtils;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.List;

/**
 * Created by Administrator on 2016/4/9.
 */
public class ConversationFragment extends BaseFragment {

    private Button bt_conversation_edit;
    private Button bt_conversation_new_msg;
    private Button bt_conversation_select_all;
    private Button bt_conversation_cancel_select;
    private Button bt_conversation_delete;
    private LinearLayout ll_conversation_edit_menu;
    private LinearLayout ll_conversation_select_menu;
    private ListView lv_conversation_list;
    private ConversationListAdapter adapter;
    private List<Integer> selectedConversationIds;
    private DeleteMsgDialog dialog;

    static final int WHAT_DELETE_COMPLETE = 0;
    static final int WHAT_UPDATE_DELETE_PROGRESS = 1;

    Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){

                case WHAT_DELETE_COMPLETE:
                    adapter.setIsSelectMode(false);
                    adapter.notifyDataSetChanged();//负责删除后将页面刷新
                    showEditMenu();//删除后返回编辑页面
                    dialog.dismiss();//删除对话完成时使对话框消失
                    break;
                case WHAT_UPDATE_DELETE_PROGRESS:
                    dialog.updateProgressAndTitle(msg.arg1+1);
                    break;
            }
        }
    };
    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // 填充布局文件，返回view对象
        View view = inflater.inflate(R.layout.fragment_conversation, null);

        lv_conversation_list = (ListView) view.findViewById(R.id.lv_conversation_list);

        bt_conversation_edit = (Button) view.findViewById(R.id.bt_conversation_edit);
        bt_conversation_new_msg = (Button) view.findViewById(R.id.bt_conversation_new_msg);
        bt_conversation_select_all = (Button) view.findViewById(R.id.bt_conversation_select_all);
        bt_conversation_cancel_select = (Button) view.findViewById(R.id.bt_conversation_cancel_select);
        bt_conversation_delete = (Button) view.findViewById(R.id.bt_conversation_delete);

        ll_conversation_edit_menu = (LinearLayout) view.findViewById(R.id.ll_conversation_edit_menu);
        ll_conversation_select_menu = (LinearLayout) view.findViewById(R.id.ll_conversation_select_menu);
        return view;
    }

    @Override
    public void initListener() {
        bt_conversation_edit.setOnClickListener(this);
        bt_conversation_new_msg.setOnClickListener(this);
        bt_conversation_select_all.setOnClickListener(this);
        bt_conversation_cancel_select.setOnClickListener(this);
        bt_conversation_delete.setOnClickListener(this);

        lv_conversation_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (adapter.getIsSelectMode()) {
                    //选中选框
                    adapter.selectSingle(position);
                } else {
                    //进入会话详细
                    Intent intent = new Intent(getActivity(), ConversationDetailActivity.class);
                    //携带数据：address和thread_id
                    Cursor cursor = (Cursor) adapter.getItem(position);
                    Conversation conversation = Conversation.createFromCursor(cursor);
                    intent.putExtra("address", conversation.getAddress());
                    intent.putExtra("thread_id", conversation.getThread_id());
                    startActivity(intent);
                }
            }
        });

        //添加群组对话框时开始监听
        lv_conversation_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor= (Cursor) adapter.getItem(position);
                Conversation conversation=Conversation.createFromCursor(cursor);
                //判断选中的会话是否有所属的群组
                if (ThreadGroupDao.hasGroup(getActivity().getContentResolver(),conversation.getThread_id())){

                    //该会话已经被添加，弹出ConfirmDialog
                    showExitDialog(conversation.getThread_id());

                }else {

                    //该会话已经被添加，列出所有群组
                    showSelectGroupDialog(conversation.getThread_id());
                }
                //消费掉这个事件，否则会
                return true;
            }
        });

    }

    @Override
    public void initData() {

        adapter = new ConversationListAdapter(getActivity(), null);
        lv_conversation_list.setAdapter(adapter);

        SimpleQueryHandler queryHandler = new SimpleQueryHandler(getActivity().getContentResolver());

        String[] projection = {//查询的短信的字段
                "sms.body AS snippet",
                "sms.thread_id AS _id",
                "groups.msg_count AS msg_count",
                "address AS address",
                "date AS date"
        };
        //开始异步查询
        //arg0、arg1：可以用来携带一个int型和一个对象
        //arg1:用来携带adapter对象，查询完毕后给adapter设置cursor
        queryHandler.startQuery(0, adapter, Constant.URI.URI_SMS_CONVERSATION, projection, null, null, "date desc");

    }

    //具体实现侦听
    @Override
    public void processClick(View v) {

        switch (v.getId()) {
            case R.id.bt_conversation_edit:
                showSelectMenu();
                //进入选择模式
                adapter.setIsSelectMode(true);
                adapter.notifyDataSetChanged();
                break;
            case R.id.bt_conversation_cancel_select:
                showEditMenu();
                //退出选择模式
                adapter.setIsSelectMode(false);
                adapter.cancelSelect();
                break;
            case R.id.bt_conversation_select_all:
                adapter.selectAll();
                break;
            case R.id.bt_conversation_delete:
                selectedConversationIds = adapter.getSelectedConversationIds();
                if (selectedConversationIds.size() == 0)

                    return;
                showDeleteDialog();
              //  deleteSms();
                break;
            case R.id.bt_conversation_new_msg:              //新建短信
                Intent intent=new Intent(getActivity(), NewMsgActivity.class);
                startActivity(intent);
                break;

        }
    }

    //选择菜单往上移动，编辑菜单向下移动
    private void showSelectMenu() {
        ViewPropertyAnimator.animate(ll_conversation_edit_menu).translationY(ll_conversation_edit_menu.getHeight()).setDuration(200);
        //延时200毫秒执行run方法的代码
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                ViewPropertyAnimator.animate(ll_conversation_select_menu).translationY(0).setDuration(200);
            }
        }, 200);


    }

    private void showEditMenu() {
        ViewPropertyAnimator.animate(ll_conversation_select_menu).translationY(ll_conversation_edit_menu.getHeight()).setDuration(200);
        //延时200毫秒执行run方法的代码
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewPropertyAnimator.animate(ll_conversation_edit_menu).translationY(0).setDuration(200);
            }
        }, 200);

    }

    //当删除内容多时，为了不线程阻塞，将其在子线程中进行
    boolean isStopDelete = false;
    private void deleteSms() {
        dialog = DeleteMsgDialog.showDeleteDialog(getActivity(), selectedConversationIds.size(), new DeleteMsgDialog.OnDeleteCancelListener() {

            @Override
            public void onCancel() {
                isStopDelete = true;
            }
        });

        Thread t = new Thread(){
            @Override
            public void run() {
                for(int i = 0; i < selectedConversationIds.size(); i++){
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //中断删除
                    if(isStopDelete){
                        isStopDelete = false;
                        break;
                    }
                    //取出集合中的会话id,以id作为where条件删除所有符合条件的短信
                    String where = "thread_id = " + selectedConversationIds.get(i);
                    getActivity().getContentResolver().delete(Constant.URI.URI_SMS, where, null);

                    //发送消息，让删除进度条刷新，同时把当前的删除进度传给进度条
                    Message msg = handler.obtainMessage();
                    msg.what = WHAT_UPDATE_DELETE_PROGRESS;
                    //把当前删除进度存入消息中
                    msg.arg1 = i;
                    handler.sendMessage(msg);
                }
                //删除会话后，清空集合
                selectedConversationIds.clear();
                handler.sendEmptyMessage(WHAT_DELETE_COMPLETE);
            }
        };
        t.start();

    }

    private void showDeleteDialog() {
        ConfirmDialog.showDialog(getActivity(), "提示", "真的要删除会话吗？", new ConfirmDialog.ConfirmListener() {
            @Override
            public void onCancle() {


            }

            @Override
            public void onConfirm() {

                deleteSms();
            }
        });

    }
    private void showExitDialog(final int thread_id) {
        //先通过会话id查询群组id
        final int group_id= ThreadGroupDao.getGroupIdByThreadId(getActivity().getContentResolver(), thread_id);
        //通过群组id查询群组名字
        String name = GroupDao.getGroupNameByGroupId(getActivity().getContentResolver(), group_id);

        String message = "该会话已经被添加至[" + name + "]群组，是否要退出该群组？";
        ConfirmDialog.showDialog(getActivity(), "提示", message, new ConfirmDialog.ConfirmListener() {

            @Override
            public void onConfirm() {
                //把选中的会话从群组中删除
                boolean isSuccess = ThreadGroupDao.deleteThreadGroupByThreadId(getActivity().getContentResolver(), thread_id, group_id);
                ToastUtils.ShowToast(getActivity(), isSuccess? "退出成功" : "退出失败");
            }

            @Override
            public void onCancle() {
            }
        });
    }

    public void showSelectGroupDialog(final int thread_id){

        final Cursor cursor=getActivity().getContentResolver().query(Constant.URI.URI_GROUP_QUERY,null,null,null,null);
        if (cursor.getCount()==0){

            ToastUtils.ShowToast(getActivity(),"当前没有群组，请先创建");
            return;
        }
        String[] items=new String[cursor.getCount()];
        //遍历cursor,取出
        while (cursor.moveToNext()){

            Group group=Group.createFromCursor(cursor);
            //获取所有群组的名字，并将群组的名全部存入一个string集合里
            items[cursor.getPosition()]=group.getName();
        }
        ListDialog.showDialog(getActivity(), "请选择群组", items, new ListDialog.OnListDialogLietener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //cursor就是查询Groups表得到的，里就是群组的所有信息
                cursor.moveToPosition(position);
                Group group= Group.createFromCursor(cursor);
                //把指定会话存入指定群组
                boolean isSuccess=ThreadGroupDao.insertThreadGroup(getActivity().getContentResolver(),thread_id,group.get_id());
                ToastUtils.ShowToast(getActivity(),isSuccess?"插入成功":"插入失败");
            }
        });

    }
}
