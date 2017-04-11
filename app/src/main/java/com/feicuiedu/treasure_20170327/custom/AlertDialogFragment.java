package com.feicuiedu.treasure_20170327.custom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feicuiedu.treasure_20170327.R;

/**
 * Created by gqq on 2017/3/28.
 */

// 自定义的一个对话框：内部使用AlertDialog实现
public class AlertDialogFragment extends DialogFragment{

    private static final String KEY_TITLE = "key_title";
    private static final String KEY_MESSAGE = "key_message";

    /** 官方推荐的：两种方式
     * 1. onCreateView()返回一个对话框的视图，采用搭建一个布局layout的方式
     * 2. onCreateDialog()可以在方法中直接创建AlertDialog的方式
     *
     * 显示：
     * show()方法
     */

    // 需要传递一些数据来展示，可以对外提供一个创建方法，在方法的参数中进行数据的传递
    public static AlertDialogFragment getInstances(String title,String message){
        AlertDialogFragment alertDialogFragment = new AlertDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE,title);
        bundle.putString(KEY_MESSAGE,message);

        // Fragment的官方推荐的一种数据传递方式
        alertDialogFragment.setArguments(bundle);
        return alertDialogFragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String title = getArguments().getString(KEY_TITLE);
        String message = getArguments().getString(KEY_MESSAGE);

        // 根据传递的数据构建一个对话框
        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }
}
