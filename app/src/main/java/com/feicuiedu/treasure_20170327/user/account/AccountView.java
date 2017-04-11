package com.feicuiedu.treasure_20170327.user.account;

/**
 * Created by gqq on 2017/4/10.
 */
// 头像的视图接口
public interface AccountView {

    void showProgress();

    void hideProgress();

    void showMessage(String msg);

    void updatePhoto(String photoUrl);
}
