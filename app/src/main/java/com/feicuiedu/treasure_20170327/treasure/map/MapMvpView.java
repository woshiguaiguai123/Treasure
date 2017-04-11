package com.feicuiedu.treasure_20170327.treasure.map;

import com.feicuiedu.treasure_20170327.treasure.Treasure;

import java.util.List;

/**
 * Created by gqq on 2017/4/5.
 */

// 数据获取的视图接口
public interface MapMvpView {

    void showMessage(String msg);// 显示信息

    void setTreasureData(List<Treasure> list);// 设置数据

}
