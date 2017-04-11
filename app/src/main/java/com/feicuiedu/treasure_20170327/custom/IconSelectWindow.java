package com.feicuiedu.treasure_20170327.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.feicuiedu.treasure_20170327.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gqq on 2017/4/10.
 */
// 用户头像点击弹出的视图(视图：从相册、相机、取消)
public class IconSelectWindow extends PopupWindow{

    private Activity mActivity;
    private Listener mListener;

    /**
     * 1. 视图的填充(布局的填充)：可以在构造方法中完成
     *      设置背景、设置焦点等
     * 2. 视图里面控件的事件：比如点击事件，一般可以让使用者去处理
     * 3. 显示出来：可以提供一个show方法
     *
     * 处理具体的点击事件：使用者处理
     * 接口回调
     * 跳转接口：跳转到相册、跳转到相机
     * 初始化：通过构造方法传递
     */

    // 调用父类的构造方法：通过参数实现视图填充
    public IconSelectWindow(Activity activity,Listener listener) {
        super(activity.getLayoutInflater().inflate(R.layout.window_select_icon,null),
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        ButterKnife.bind(this,getContentView());

        mActivity = activity;
        mListener = listener;

        setFocusable(true);// 获得焦点
        setBackgroundDrawable(new BitmapDrawable());// 设置背景
    }

    // 展示的方法
    public void show(){
        showAtLocation(mActivity.getWindow().getDecorView(), Gravity.BOTTOM,0,0);
    }

    // 视图的点击事件
    @OnClick({R.id.btn_gallery,R.id.btn_camera,R.id.btn_cancel})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.btn_gallery:
                // 跳转到相册
                mListener.toGallery();
                break;
            case R.id.btn_camera:
                // 跳转到相机
                mListener.toCamera();
                break;
            case R.id.btn_cancel:
                // 取消
                break;
        }
        dismiss();
    }

    // 跳转的接口
    public interface Listener{
        void toGallery();// 到相册
        void toCamera();// 到相机
    }
}
