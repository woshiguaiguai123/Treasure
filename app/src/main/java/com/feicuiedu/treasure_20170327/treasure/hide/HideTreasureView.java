package com.feicuiedu.treasure_20170327.treasure.hide;

/**
 * Created by gqq on 2017/4/7.
 */
// 埋藏宝藏的视图接口
public interface HideTreasureView {

    void showMessage(String msg);
    void navigateToHome();
    void showProgress();
    void hideProgress();
}
