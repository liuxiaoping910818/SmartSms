package com.example.android.smartsms.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.android.smartsms.utils.ToastUtils;

/**
 * Created by Administrator on 2016/4/9.
 */
public class SendSmsReceiver extends BroadcastReceiver{

    public static final String ACTION_SEND_SMS="com.example.android.smartsms.sendsms";

    @Override
    public void onReceive(Context context, Intent intent) {
        int code=getResultCode();
        if (code== Activity.RESULT_OK){

            ToastUtils.ShowToast(context,"发送成功");
        }else {

            ToastUtils.ShowToast(context,"发送失败");
        }
    }
}
