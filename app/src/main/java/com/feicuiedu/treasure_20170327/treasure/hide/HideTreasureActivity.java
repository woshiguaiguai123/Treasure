package com.feicuiedu.treasure_20170327.treasure.hide;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.baidu.mapapi.model.LatLng;
import com.feicuiedu.treasure_20170327.R;
import com.feicuiedu.treasure_20170327.commons.ActivityUtils;
import com.feicuiedu.treasure_20170327.treasure.TreasureRepo;
import com.feicuiedu.treasure_20170327.user.UserPrefs;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

// 埋藏宝藏的详细页面
public class HideTreasureActivity extends AppCompatActivity implements HideTreasureView{

    private static final String KEY_TITLE = "key_title";
    private static final String KEY_ADDRESS = "key_address";
    private static final String KEY_LATLNG = "key_latlng";
    private static final String KEY_ALTITUDE = "key_altitude";
    @BindView(R.id.hide_send)
    ImageView mHideSend;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.et_description)
    EditText mEtDescription;
    private ActivityUtils mActivityUtils;
    private ProgressDialog mProgressDialog;

    // 对外提供一个跳转的方法
    public static void open(Context context, String title, String address, LatLng latLng, double altitude) {
        Intent intent = new Intent(context, HideTreasureActivity.class);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_ADDRESS, address);
        intent.putExtra(KEY_LATLNG, latLng);
        intent.putExtra(KEY_ALTITUDE, altitude);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide_treasure);
        ButterKnife.bind(this);
        mActivityUtils = new ActivityUtils(this);

        // toolbar
        setSupportActionBar(mToolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getIntent().getStringExtra(KEY_TITLE));
        }
    }

    // 宝藏的上传
    @OnClick(R.id.hide_send)
    public void sendTreasure(){

        // 取出传递的数据
        Intent intent = getIntent();
        String title = intent.getStringExtra(KEY_TITLE);
        String address = intent.getStringExtra(KEY_ADDRESS);
        LatLng latlng = intent.getParcelableExtra(KEY_LATLNG);
        double altitude = intent.getDoubleExtra(KEY_ALTITUDE,0);

        // 拿到用户的TokenId
        int tokenid = UserPrefs.getInstance().getTokenid();

        // 拿到描述信息
        String desc = mEtDescription.getText().toString();

        // 需要上传的请求体数据
        HideTreasure hideTreasure = new HideTreasure();
        hideTreasure.setTitle(title);// 标题
        hideTreasure.setAltitude(altitude);// 海拔
        hideTreasure.setDescription(desc);// 描述信息
        hideTreasure.setLatitude(latlng.latitude);// 纬度
        hideTreasure.setLongitude(latlng.longitude);// 经度
        hideTreasure.setLocation(address);// 地址
        hideTreasure.setTokenId(tokenid);// 用户令牌

        // 埋藏宝藏的网络请求
        new HideTreasurePresenter(this).hideTreasure(hideTreasure);
    }

    // 处理返回箭头
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // -------------------------埋藏宝藏的视图实现----------------------------
    @Override
    public void showMessage(String msg) {
        mActivityUtils.showToast(msg);
    }

    @Override
    public void navigateToHome() {
        finish();
        // 清除缓存 : 为了返回到之前的页面重新去请求数据
        TreasureRepo.getInstance().clear();
    }

    @Override
    public void showProgress() {
        mProgressDialog = ProgressDialog.show(this, "宝藏上传", "宝藏正在上传中，请稍等~");
    }

    @Override
    public void hideProgress() {
        if (mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
    }
}
