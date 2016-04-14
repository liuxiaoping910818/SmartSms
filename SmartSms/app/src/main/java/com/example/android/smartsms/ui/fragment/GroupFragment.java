package com.example.android.smartsms.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.android.smartsms.R;
import com.example.android.smartsms.adapter.GroupListAdapter;
import com.example.android.smartsms.base.BaseFragment;
import com.example.android.smartsms.bean.Group;
import com.example.android.smartsms.dao.GroupDao;
import com.example.android.smartsms.dao.SimpleQueryHandler;
import com.example.android.smartsms.dialog.InputDialog;
import com.example.android.smartsms.dialog.ListDialog;
import com.example.android.smartsms.globle.Constant;
import com.example.android.smartsms.ui.activity.GroupDetailActivity;
import com.example.android.smartsms.utils.ToastUtils;

/**
 * Created by Administrator on 2016/4/9.
 */
public class GroupFragment extends BaseFragment {

    private Button bt_group_newgroup;
    private ListView lv_group_list;
    private GroupListAdapter adapter;
    private SimpleQueryHandler queryHandler;
    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_group, null);
        bt_group_newgroup = (Button) view.findViewById(R.id.bt_group_newgroup);
        lv_group_list = (ListView) view.findViewById(R.id.lv_group_list);
        return view;
    }

    @Override
    public void initListener() {
        bt_group_newgroup.setOnClickListener(this);

        //给条目设置长按侦听
        lv_group_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                Cursor cursor  = (Cursor) adapter.getItem(position);
                //用对象来取出相应的内容
                final Group group = Group.createFromCursor(cursor);
                //长按之后会跳出来一个对话框。
                ListDialog.showDialog(getActivity(), "选择操作", new String[]{"修改","删除"}, new ListDialog.OnListDialogLietener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id) {
                        switch (position) {
                            case 0://修改
                                //弹出输入对话框
                                InputDialog.showDialog(getActivity(), "修改群组", new InputDialog.OnInputDialogListener() {

                                    @Override
                                    public void onConfirm(String text) {
                                        //确认修改群组名字
                                        GroupDao.updateGroupName(getActivity().getContentResolver(), text, group.get_id());
                                    }
                                    @Override
                                    public void onCancel() {
                                    }
                                });
                                break;
                            case 1://删除
                                GroupDao.deleteGroup(getActivity().getContentResolver(), group.get_id());
                                break;
                        }
                    }
                });
                return false;
            }
        });

        lv_group_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //跳转时携带群组名字，群组id
                Cursor cursor= (Cursor) adapter.getItem(position);
                Group group=Group.createFromCursor(cursor);
                if (group.getThread_count()>0){

                    Intent intent=new Intent(getActivity(), GroupDetailActivity.class);
                    intent.putExtra("name",group.getName());
                    intent.putExtra("group_id",group.get_id());
                    startActivity(intent);
                }else {

                    ToastUtils.ShowToast(getActivity(),"当前群组没有任何佳话");
                }

            }
        });

    }

    //查询数据库，将信息显示到屏幕上
    @Override
    public void initData() {

        adapter=new GroupListAdapter(getActivity(),null);
        lv_group_list.setAdapter(adapter);

        queryHandler=new SimpleQueryHandler(getActivity().getContentResolver());
        queryHandler.startQuery(0,adapter,Constant.URI.URI_GROUP_QUERY,null,null,null,"create_date desc");//最新的显示在最上面


    }

    @Override
    public void processClick(View v) {
        switch (v.getId()){

            case R.id.bt_group_newgroup:
                InputDialog.showDialog(getActivity(), "创建群组", new InputDialog.OnInputDialogListener() {
                    @Override
                    public void onCancel() {


                    }

                    @Override
                    public void onConfirm(String text) {

                        if (!TextUtils.isEmpty(text)){

                            GroupDao.insertGroup(getActivity().getContentResolver(),text);
                        }else {

                            ToastUtils.ShowToast(getActivity(),"群组名不能为空");
                        }


                    }
                });
                break;
        }

    }
}
