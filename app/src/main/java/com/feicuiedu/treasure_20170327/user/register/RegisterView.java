package com.feicuiedu.treasure_20170327.user.register;

/**
 * Created by gqq on 2017/3/28.
 */

// 注册的视图接口
public interface RegisterView {

    void showProgress();// 显示进度

    void hideProgress();// 隐藏进度

    void showMessage(String msg);// 显示信息

    void navigateToHome();// 跳转页面

}
