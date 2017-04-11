package com.feicuiedu.treasure_20170327.treasure.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.feicuiedu.treasure_20170327.R;
import com.feicuiedu.treasure_20170327.commons.ActivityUtils;
import com.feicuiedu.treasure_20170327.custom.TreasureView;
import com.feicuiedu.treasure_20170327.treasure.Area;
import com.feicuiedu.treasure_20170327.treasure.Treasure;
import com.feicuiedu.treasure_20170327.treasure.TreasureRepo;
import com.feicuiedu.treasure_20170327.treasure.detail.TreasureDetailActivity;
import com.feicuiedu.treasure_20170327.treasure.hide.HideTreasureActivity;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gqq on 2017/3/31.
 */
// 地图和宝藏的展示
public class MapFragment extends Fragment implements MapMvpView{

    private static final int LOCATION_REQUEST_CODE = 100;
    @BindView(R.id.iv_located)
    ImageView mIvLocated;
    @BindView(R.id.btn_HideHere)
    Button mBtnHideHere;
    @BindView(R.id.centerLayout)
    RelativeLayout mCenterLayout;
    @BindView(R.id.iv_scaleUp)
    ImageView mIvScaleUp;
    @BindView(R.id.iv_scaleDown)
    ImageView mIvScaleDown;
    @BindView(R.id.tv_located)
    TextView mTvLocated;
    @BindView(R.id.tv_satellite)
    TextView mTvSatellite;
    @BindView(R.id.tv_compass)
    TextView mTvCompass;
    @BindView(R.id.ll_locationBar)
    LinearLayout mLlLocationBar;
    @BindView(R.id.tv_currentLocation)
    TextView mTvCurrentLocation;
    @BindView(R.id.iv_toTreasureInfo)
    ImageView mIvToTreasureInfo;
    @BindView(R.id.et_treasureTitle)
    EditText mEtTreasureTitle;
    @BindView(R.id.cardView)
    CardView mCardView;
    @BindView(R.id.layout_bottom)
    FrameLayout mLayoutBottom;
    @BindView(R.id.map_frame)
    FrameLayout mMapFrame;
    @BindView(R.id.treasureView)
    TreasureView mTreasureView;
    @BindView(R.id.hide_treasure)
    RelativeLayout mHideTreasure;

    private BaiduMap mBaiduMap;
    private MapView mMapView;
    private LocationClient mLocationClient;
    private boolean isFirst = true;
    private static LatLng mCurrentLocation;
    private static String mCurrentAddr;
    private LatLng mCurrentStatus;
    private MapPresenter mMapPresenter;
    private ActivityUtils mActivityUtils;
    private Marker mCurrentMarker;
    private GeoCoder mGeoCoder;
    private String mGeoCoderAddr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container);

        // 1. 检测权限有没有授权成功
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // 没有成功，需要向用户申请
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 清空缓存的数据
        TreasureRepo.getInstance().clear();

        ButterKnife.bind(this, view);

        mActivityUtils = new ActivityUtils(this);

        mMapPresenter = new MapPresenter(this);
        // 视图的处理工作

        // 初始化百度地图
        initMapView();

        // 初始化定位相关
        initLocation();

        // 初始化地理编码相关
        initGeoCoder();

    }

    // 地理编码相关
    private void initGeoCoder() {

        // 第一步，创建地理编码检索实例；
        mGeoCoder = GeoCoder.newInstance();

        // 第二步，设置地理编码检索监听者；
        mGeoCoder.setOnGetGeoCodeResultListener(mGeoCoderResultListener);

        // 地图状态变化之后发起
    }

    // 地理编码的监听者
    private OnGetGeoCoderResultListener mGeoCoderResultListener = new OnGetGeoCoderResultListener() {

        // 获取地理编码结果:geoCodeResult拿到的结果
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

        }

        // 获取反向地理编码结果：reverseGeoCodeResult拿到的结果
        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            // 判断结果是否正确拿到
            if (reverseGeoCodeResult==null||reverseGeoCodeResult.error!= SearchResult.ERRORNO.NO_ERROR){
                // 没有拿到检索的结果
                mGeoCoderAddr = "未知的地址";
                mTvCurrentLocation.setText(mGeoCoderAddr);
                return;
            }
            // 拿到地址信息
            mGeoCoderAddr = reverseGeoCodeResult.getAddress();

            // 将地址信息设置给TextView
            mTvCurrentLocation.setText(mGeoCoderAddr);
        }
    };

    // 初始化定位相关
    private void initLocation() {

        // 前置：激活定位图层
        mBaiduMap.setMyLocationEnabled(true);

        // 1. 第一步，初始化LocationClient类
        mLocationClient = new LocationClient(getContext().getApplicationContext());

        // 2. 第二步，配置定位SDK参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开GPS
        option.setCoorType("bd09ll");// 设置坐标类型，默认gcj02，会有偏差，设置返回的定位结果坐标系
        option.setIsNeedAddress(true);// 需要地址信息
        // 设置参数给LocationClient
        mLocationClient.setLocOption(option);

        // 3. 第三步，实现BDLocationListener接口
        mLocationClient.registerLocationListener(mBDLocationListener);

        // 4. 第四步，开始定位
        mLocationClient.start();

    }

    // 定位监听
    private BDLocationListener mBDLocationListener = new BDLocationListener() {

        // 当获取到定位数据的时候会触发
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            if (bdLocation==null){
                // 没有拿到数据，可以重新进行请求
                mLocationClient.requestLocation();
                return;
            }

            // 拿到定位的经纬度
            double latitude = bdLocation.getLatitude();
            double longitude = bdLocation.getLongitude();

            // 定位的位置和地址
            mCurrentLocation = new LatLng(latitude,longitude);
            mCurrentAddr = bdLocation.getAddrStr();

            Log.i("TAG","定位的位置："+ mCurrentAddr +"经纬度："+latitude+","+longitude);

            // 地图上设置定位数据
            MyLocationData locationData = new MyLocationData.Builder()
                    // 设置定位的经纬度
                    .latitude(latitude)
                    .longitude(longitude)
                    .accuracy(100f)// 定位精度的大小
                    .build();

            mBaiduMap.setMyLocationData(locationData);

            // 第一次进入将地图自动移动到定位的位置
            if (isFirst){
                // 自动移动到定位处
                moveToLocation();
                isFirst = false;
            }
        }
    };

    // 初始化百度地图的操作
    private void initMapView() {

        // 地图的状态
        MapStatus mapstatus = new MapStatus.Builder()
                .rotate(0)// 旋转的角度
                .zoom(15)// 默认是12，范围3-21
                .overlook(0)// 俯仰的角度
                .build();

        // 设置地图的信息
        BaiduMapOptions options = new BaiduMapOptions()
                .mapStatus(mapstatus)
                .compassEnabled(true)// 是否显示指南针，默认显示
                .zoomGesturesEnabled(true)// 是否允许缩放手势
                .scaleControlEnabled(false)// 不显示比例尺
                .zoomControlsEnabled(false)// 不显示缩放的控件
                ;

        // 创建地图控件
        mMapView = new MapView(getContext(), options);

        // 在布局中添加地图的控件：0，放置在第一位
        mMapFrame.addView(mMapView,0);

        // 拿到地图的操作类(设置地图的视图、地图状态变化、添加覆盖物等)
        mBaiduMap = mMapView.getMap();

        // 设置地图状态的监听
        mBaiduMap.setOnMapStatusChangeListener(mStatusChangeListener);

        // 设置地图上的覆盖物的点击监听
        mBaiduMap.setOnMarkerClickListener(mMarkerClickListener);
    }

    // 覆盖物的点击监听
    private BaiduMap.OnMarkerClickListener mMarkerClickListener = new BaiduMap.OnMarkerClickListener() {

        // 点击Marker会触发：marker当前点击的
        @Override
        public boolean onMarkerClick(Marker marker) {

            // 当前点击的Marker先管理判断
            if (mCurrentMarker!=null){
                if (mCurrentMarker!=marker){
                    mCurrentMarker.setVisible(true);// 点击了其他的，把之前的显示出来
                }
            }
            mCurrentMarker = marker;
            // 点击展示InfoWindow，当前的覆盖物不可见
            mCurrentMarker.setVisible(false);

            // 1. 创建InfoWindow
            InfoWindow infoWindow = new InfoWindow(dot_expand, marker.getPosition(), 0, new InfoWindow.OnInfoWindowClickListener() {

                // InfoWindow的点击监听
                @Override
                public void onInfoWindowClick() {
                    // 切换回普通的视图
                    changeUIMode(UI_MODE_NORMAL);
                }
            });

            // 2. 地图上展示
            mBaiduMap.showInfoWindow(infoWindow);

            // 宝藏的信息取出
            int id = marker.getExtraInfo().getInt("id");
            Treasure treasure = TreasureRepo.getInstance().getTreasure(id);
            mTreasureView.bindTreasure(treasure);

            // 切换成宝藏选中的视图
            changeUIMode(UI_MODE_SELECT);

            return false;
        }
    };

    // 地图状态的监听
    private BaiduMap.OnMapStatusChangeListener mStatusChangeListener = new BaiduMap.OnMapStatusChangeListener() {

        // 变化开始的时候
        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {

        }

        // 变化中
        @Override
        public void onMapStatusChange(MapStatus mapStatus) {

        }

        // 变化结束之后
        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {

            // 拿到当前移动后的地图状态所在的位置
            LatLng target = mapStatus.target;

            // 地图状态确实发生变化了
            if(target != MapFragment.this.mCurrentStatus){

                // 根据当前的地图的状态来获取当前的区域内的宝藏数据
                updateMapArea();

                // 在埋藏宝藏的情况下
                if (mUIMode==UI_MODE_HIDE){

                    // 设置反地理编码的参数：位置(当前的经纬度)
                    ReverseGeoCodeOption option = new ReverseGeoCodeOption();
                    option.location(target);

                    // 发起反地理编码：经纬度-->地址
                    mGeoCoder.reverseGeoCode(option);
                }

                // 当前地图的位置
                MapFragment.this.mCurrentStatus = target;
            }
        }
    };

    // 区域的确定和宝藏数据的获取
    private void updateMapArea() {

        // 拿到当前的地图状态
        MapStatus mapStatus = mBaiduMap.getMapStatus();

        // 从中拿到当前地图的经纬度
        double longitude = mapStatus.target.longitude;
        double latitude = mapStatus.target.latitude;

        // 根据当前的经纬度来确定区域
        Area area = new Area();

        // 根据当前经纬度向上和向下取整得到的区域
        area.setMaxLat(Math.ceil(latitude));
        area.setMaxLng(Math.ceil(longitude));
        area.setMinLng(Math.floor(longitude));
        area.setMinLat(Math.floor(latitude));

        // 要根据当前的区域来获取了：进行网络请求
        mMapPresenter.getTreasure(area);
    }

    // 覆盖物图标
    private BitmapDescriptor dot = BitmapDescriptorFactory.fromResource(R.mipmap.treasure_dot);
    private BitmapDescriptor dot_expand = BitmapDescriptorFactory.fromResource(R.mipmap.treasure_expanded);

    // 添加覆盖物的方法
    private void addMarker(LatLng latlng, int treasureId) {
        // 根据宝藏的经纬度、id 添加覆盖物
        MarkerOptions options = new MarkerOptions()
                .position(latlng)// 覆盖物添加的位置
                .icon(dot)// 覆盖物的图标
                .anchor(0.5f,0.5f)// 锚点位置：居中
                ;

        // 将宝藏的id信息也一并存到覆盖物里面
        Bundle bundle = new Bundle();
        bundle.putInt("id",treasureId);
        options.extraInfo(bundle);

        mBaiduMap.addOverlay(options);
    }

    // 将定位的位置返回出去，供其它调用
    public static LatLng getMyLocation(){
        return mCurrentLocation;
    }

    // 将定位的地址信息返回出去
    public static String getLocationAddr(){
        return mCurrentAddr;
    }

    // -----------------------------视图的点击事件------------------------------------
    // 卫星视图和普通视图的切换
    @OnClick(R.id.tv_satellite)
    public void switchMapType(){

        // 先拿到当前的地图的类型
        int mapType = mBaiduMap.getMapType();
        // 切换类型
        mapType = (mapType==BaiduMap.MAP_TYPE_NORMAL)?BaiduMap.MAP_TYPE_SATELLITE:BaiduMap.MAP_TYPE_NORMAL;
        // 文字的变化
        String msg = (mapType==BaiduMap.MAP_TYPE_NORMAL)?"卫星":"普通";
        mBaiduMap.setMapType(mapType);
        mTvSatellite.setText(msg);
    }

    // 指南针
    @OnClick(R.id.tv_compass)
    public void switchCompass(){
        // 当前地图指南针有没有在显示
        boolean enabled = mBaiduMap.getUiSettings().isCompassEnabled();
        mBaiduMap.getUiSettings().setCompassEnabled(!enabled);
    }

    // 地图的缩放
    @OnClick({R.id.iv_scaleDown,R.id.iv_scaleUp})
    public void scaleMap(View view){
        switch (view.getId()){
            case R.id.iv_scaleUp:
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
                break;
            case R.id.iv_scaleDown:
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
                break;
        }
    }

    // 定位按钮：移动到定位的位置
    @OnClick(R.id.tv_located)
    public void moveToLocation(){
        // 更新的是地图的状态
        MapStatus mapStatus = new MapStatus.Builder()
                .target(mCurrentLocation)// 定位的位置
                .rotate(0)
                .overlook(0)
                .zoom(19)
                .build();
        // 更新的状态
        MapStatusUpdate update = MapStatusUpdateFactory.newMapStatus(mapStatus);
        // 利用地图操作类更新地图的状态
        mBaiduMap.animateMapStatus(update);
    }

    // 宝藏信息卡片的点击事件
    @OnClick(R.id.treasureView)
    public void clickTreasureView(){
        // 跳转到详情页，展示宝藏信息，将宝藏数据传递过去
        int id = mCurrentMarker.getExtraInfo().getInt("id");
        Treasure treasure = TreasureRepo.getInstance().getTreasure(id);
        TreasureDetailActivity.open(getContext(),treasure);
    }

    @OnClick(R.id.hide_treasure)
    public void hideTreasure(){
        // 拿到录入的标题
        String title = mEtTreasureTitle.getText().toString();
        // 判断
        if (TextUtils.isEmpty(title)){
            mActivityUtils.showToast("请输入宝藏标题");
            return;
        }
        // 输入了标题：跳转到埋藏宝藏详细页面
        LatLng latLng = mBaiduMap.getMapStatus().target;
        HideTreasureActivity.open(getContext(),title,mGeoCoderAddr,latLng,0);
    }

    /** 视图的切换方法：根据各个控件的显示和隐藏来实现视图的切换
     * 普通的视图
     * 宝藏选中的视图
     * 埋藏宝藏的视图
     */
    private static final int UI_MODE_NORMAL = 0;// 普通视图
    private static final int UI_MODE_SELECT = 1;// 宝藏选中视图
    private static final int UI_MODE_HIDE = 2;// 埋藏宝藏视图

    private static int mUIMode = UI_MODE_NORMAL;// 当前的视图

    public void changeUIMode(int uiMode){
        if (mUIMode==uiMode) return;
        mUIMode = uiMode;

        switch (uiMode){
            // 切换为普通视图
            case UI_MODE_NORMAL:
                if (mCurrentMarker!=null){
                    mCurrentMarker.setVisible(true);
                }
                mBaiduMap.hideInfoWindow();
                mLayoutBottom.setVisibility(View.GONE);
                mCenterLayout.setVisibility(View.GONE);
                break;
            // 切换为选中视图(展示宝藏信息卡片)
            case UI_MODE_SELECT:
                mLayoutBottom.setVisibility(View.VISIBLE);
                mTreasureView.setVisibility(View.VISIBLE);
                mCenterLayout.setVisibility(View.GONE);
                mHideTreasure.setVisibility(View.GONE);
                break;

            // 切换为埋藏宝藏
            case UI_MODE_HIDE:
                if (mCurrentMarker!=null){
                    mCurrentMarker.setVisible(true);
                }
                mBaiduMap.hideInfoWindow();
                mCenterLayout.setVisibility(View.VISIBLE);
                mLayoutBottom.setVisibility(View.GONE);
                mBtnHideHere.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mLayoutBottom.setVisibility(View.VISIBLE);
                        mTreasureView.setVisibility(View.GONE);
                        mHideTreasure.setVisibility(View.VISIBLE);
                    }
                });
                break;
        }
    }


    // -------------------------视图的具体实现--------------------------
    @Override
    public void showMessage(String msg) {
        mActivityUtils.showToast(msg);
    }

    @Override
    public void setTreasureData(List<Treasure> list) {

        // 再次进行网络请求的时候，之前的覆盖物都清除一下
        mBaiduMap.clear();// 清空地图上所有的覆盖物和InfoWindow

        for (Treasure treasure :
                list) {
            // 拿到每一个宝藏数据、将宝藏信息以覆盖物的形式添加到地图上
            LatLng latlng = new LatLng(treasure.getLatitude(),treasure.getLongitude());
            addMarker(latlng,treasure.getId());
        }
    }

    // 处理权限的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case LOCATION_REQUEST_CODE:

                // 用户授权成功了
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    // 定位了
                    mLocationClient.requestLocation();
                }else {
                    // 显示个吐司、提示框
                }
                break;
        }
    }

    // 对外提供一个方法，什么时候可以退出
    public boolean clickBackPressed(){

        if (mUIMode!=UI_MODE_NORMAL){
            changeUIMode(UI_MODE_NORMAL);
            return false;
        }
        return true;
    }
}
