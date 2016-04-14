package com.example.android.smartsms.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.smartsms.R;

/**
 * Created by Administrator on 2016/4/9.创建 群组
 */
public class InputDialog  extends BaseDialog{

    private String title;
    private TextView tv_inputdialog_title;
    private EditText et_inputdialog_message;
    private Button bt_inputdialog_confirm;
    private Button bt_inputdialog_cancel;

    private OnInputDialogListener onInputDialogListener;

    protected InputDialog(Context context,String title,OnInputDialogListener onInputDialogListener) {
        super(context);
        this.title=title;
        this.onInputDialogListener=onInputDialogListener;
    }

    //本类中提供静态方法，获取本类对象
    public static void showDialog(Context context,String title,OnInputDialogListener onInputDialogListener){

        InputDialog dialog=new InputDialog(context,title,onInputDialogListener);
        //对话框默认不支持文本输入，手动把一个输入框设置为对话框的内容，Android自动对其进行设置
        dialog.setView(new EditText(context));
        dialog.show();
    }

    @Override
    public void initView() {

        setContentView(R.layout.dialog_input);
        tv_inputdialog_title= (TextView) findViewById(R.id.tv_inputdialog_title);
        et_inputdialog_message= (EditText) findViewById(R.id.et_inputdialog_message);
        bt_inputdialog_cancel= (Button) findViewById(R.id.bt_inputdialog_cancel);
        bt_inputdialog_confirm= (Button) findViewById(R.id.bt_inputdialog_confirm);


    }

    @Override
    public void initListener() {

        bt_inputdialog_cancel.setOnClickListener(this);
        bt_inputdialog_confirm.setOnClickListener(this);

    }

    @Override
    public void initData() {

        tv_inputdialog_title.setText(title);

    }

    @Override
    public void processClick(View v) {

        switch (v.getId()){

            case R.id.bt_inputdialog_cancel:
                if (onInputDialogListener!=null){
                    onInputDialogListener.onCancel();

                }
                break;
            case R.id.bt_inputdialog_confirm:
                if (onInputDialogListener!=null){

                    onInputDialogListener.onConfirm(et_inputdialog_message.getText().toString());
                }
                break;
        }

    }
    public  interface  OnInputDialogListener{

        void onCancel();
        void onConfirm(String text);
    }
}
