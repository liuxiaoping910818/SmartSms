package com.example.android.smartsms.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/4/9.
 */
public class ToastUtils {
    public static void ShowToast(Context context, String msg){
        Toast.makeText(context, msg, 0).show();
    }
}
