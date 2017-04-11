package com.feicuiedu.treasure_20170327;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.feicuiedu.treasure_20170327.commons.ActivityUtils;
import com.feicuiedu.treasure_20170327.treasure.HomeActivity;
import com.feicuiedu.treasure_20170327.user.UserPrefs;
import com.feicuiedu.treasure_20170327.user.login.LoginActivity;
import com.feicuiedu.treasure_20170327.user.register.RegisterActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    public static final String MAIN_ACTION = "navigate_to_main";
    private ActivityUtils mActivityUtils;
    private Unbinder mUnbinder;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        // 接收到广播之后处理
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivityUtils = new ActivityUtils(this);
        mUnbinder = ButterKnife.bind(this);

        // 判断一下用户是不是已经登录过了
        SharedPreferences preferences = getSharedPreferences("user_info", MODE_PRIVATE);
        if (preferences!=null){

            // 如果登录了
            if (preferences.getInt("key_tokenid",0)== UserPrefs.getInstance().getTokenid()){
                mActivityUtils.startActivity(HomeActivity.class);
                finish();
            }
        }

        // 注册本地广播
        IntentFilter fliter = new IntentFilter(MAIN_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,fliter);

    }

    @OnClick({R.id.btn_Register, R.id.btn_Login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_Register:
                mActivityUtils.startActivity(RegisterActivity.class);
                break;
            case R.id.btn_Login:
                mActivityUtils.startActivity(LoginActivity.class);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
}
