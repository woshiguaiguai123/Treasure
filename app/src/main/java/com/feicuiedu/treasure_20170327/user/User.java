package com.feicuiedu.treasure_20170327.user;

/**
 * Created by gqq on 2017/3/30.
 */

import com.google.gson.annotations.SerializedName;

/** 用户类
 * GsonFormat创建实体类：设置里面下载插件
 */
public class User {

    /**
     * UserName : qjd
     * Password : 654321
     */

    @SerializedName("UserName")
    private String userName;

    @SerializedName("Password")
    private String password;

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}
