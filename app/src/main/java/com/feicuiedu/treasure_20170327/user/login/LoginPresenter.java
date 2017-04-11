package com.feicuiedu.treasure_20170327.user.login;

import android.os.Handler;
import android.os.Looper;

import com.feicuiedu.treasure_20170327.net.NetClient;
import com.feicuiedu.treasure_20170327.user.User;
import com.feicuiedu.treasure_20170327.user.UserPrefs;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gqq on 2017/3/28.
 */

// 登录的业务类：帮View去做业务请求
public class LoginPresenter {

    private Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * 业务类中间涉及到的视图怎么处理？
     * 1. 创建LoginActivity，不能采用这种方式
     * 2. 接口回调的方式
     * A 接口  里面有一个a()
     * AImpl是A接口的实现类  实现a()
     * 使用：A a = new Aimpl();
     * this.a = a;
     * a.a();
     * <p>
     * 接口创建好了，怎么初始化？
     * Activity实现视图接口
     */

    private LoginView mLoginView;
    private Call mCall;

    public LoginPresenter(LoginView loginView) {
        mLoginView = loginView;
    }

    // 登录的业务
    public void login(final User user) {

        mLoginView.showProgress();

        NetClient.getInstance().getTreasureApi().login(user).enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {

                mLoginView.hideProgress();

                // 成功
                if (response.isSuccessful()){
                    LoginResult userResult = response.body();
                    if (userResult==null){
                        mLoginView.showMessage("未知的错误");
                        return;
                    }
                    if (userResult.getCode()==1){
                        // 真正的成功了
                        // 保存头像和tokenid
                        UserPrefs.getInstance().setPhoto(NetClient.BASE_URL+userResult.getHeadpic());
                        UserPrefs.getInstance().setTokenid(userResult.getTokenid());
                        mLoginView.navigateToHome();
                    }
                    mLoginView.showMessage(userResult.getMsg());
                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                mLoginView.hideProgress();
                mLoginView.showMessage("请求失败"+t.getMessage());
            }
        });
    }
}
