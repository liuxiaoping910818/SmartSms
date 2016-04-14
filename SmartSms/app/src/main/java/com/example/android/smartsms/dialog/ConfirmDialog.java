package com.example.android.smartsms.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.smartsms.R;

import java.util.concurrent.RecursiveTask;

/**
 * Created by Administrator on 2016/4/9.
 */
public class ConfirmDialog extends BaseDialog{


    private  String title;
    private  String message;
    private  TextView tv_dialog_title;
    private  TextView tv_dialog_message;
    private Button bt_dialog_cancel;
    private  Button bt_dialog_confirm;
    private ConfirmListener confirmListener;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    protected ConfirmDialog(Context context) {
        super(context);

    }
    public static void showDialog(Context context, String title, String message, ConfirmListener confirmListener){
        ConfirmDialog dialog = new ConfirmDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setConfirmListener(confirmListener);
        dialog.show();
    }


    @Override
    public void initView() {

        setContentView(R.layout.dialog_confirm);
        tv_dialog_title= (TextView) findViewById(R.id.tv_dialog_message);
        tv_dialog_message= (TextView) findViewById(R.id.tv_dialog_message);

        bt_dialog_cancel= (Button) findViewById(R.id.bt_dialog_cancel);
        bt_dialog_confirm= (Button) findViewById(R.id.bt_dialog_confirm);

    }

    @Override
    public void initListener() {

        bt_dialog_confirm.setOnClickListener(this);
        bt_dialog_cancel.setOnClickListener(this);

    }

    @Override
    public void initData() {

        tv_dialog_title.setText(title);
        tv_dialog_message.setText(message);
    }

    @Override
    public void processClick(View v) {

        switch (v.getId()){

            case R.id.bt_dialog_cancel:

                if (confirmListener!=null){

                    confirmListener.onCancle();
                }
                break;
            case R.id.bt_dialog_confirm:

                if (confirmListener!=null){

                    confirmListener.onConfirm();
                }
                break;
        }

        dismiss();
    }

    public void setConfirmListener(ConfirmListener confirmListener){

        this.confirmListener=confirmListener;
    }
   public interface  ConfirmListener{

        void onCancle();
        void onConfirm();
    }
}
