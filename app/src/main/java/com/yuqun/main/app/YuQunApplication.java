package com.yuqun.main.app;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yuqun.main.manager.SharePreferenceManager;
import com.yuqun.main.ui.model.TagModel;
import com.yuqun.main.utils.LogN;
import com.yuqun.main.utils.Utils;
import com.yuqun.main.wxapi.WeChatConstants;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class YuQunApplication extends Application {

    public static YuQunApplication instance;
    public static List<TagModel> modelList = new ArrayList<>();
    // 经度
    public static double latitude = 0.0;
    // 纬度
    public static double longitude = 0.0;
    // 当前城市
    public static String currentCity;
    public LocationClient mLocationClient;
    public MyLocationListenner myListener ;
    //    public NotifyLister mNotifyer=null;
       /*微信*/
    public static IWXAPI api;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        try {
            SharePreferenceManager.getInstance().init(this);
            if(!Utils.isNetworkAvailable(this)){
                Toast.makeText(YuQunApplication.this, "请检查网络连接", Toast.LENGTH_SHORT).show();
                return;
            }
            myListener=  new MyLocationListenner();
            mLocationClient = new LocationClient(this);
            mLocationClient.registerLocationListener(myListener);

            api = WXAPIFactory.createWXAPI(this, WeChatConstants.APP_ID, true);
            api.registerApp(WeChatConstants.APP_ID);


            JPushInterface.setDebugMode(true); // 设置开启日志,发布时请关闭日志
            JPushInterface.init(this); //
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            latitude = location.getLatitude();

            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            longitude = location.getLongitude();
            new Thread(new Runnable() {

                @Override
                public void run() {
                    String json = Utils.getCityByLocation(latitude, longitude);
                    String cityByLocation = Utils.getCityName(json);
                    System.out.println("当前城市=" + cityByLocation);
                    YuQunApplication.currentCity = cityByLocation;
                }
            }).start();
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
//				sb.append("\n省：");
//				sb.append(location.getProvince());
//				sb.append("\n市：");
//				sb.append(location.getCity());
//				sb.append("\n区/县：");
//				sb.append(location.getDistrict());
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
            }
            sb.append("\nsdk version : ");
            sb.append(mLocationClient.getVersion());
            sb.append("\nisCellChangeFlag : ");
            sb.append(location.isCellChangeFlag());
            LogN.d("location", sb.toString());
        }

        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null) {
                return;
            }
            StringBuffer sb = new StringBuffer(256);
            sb.append("Poi time : ");
            sb.append(poiLocation.getTime());
            sb.append("\nerror code : ");
            sb.append(poiLocation.getLocType());
            sb.append("\nlatitude : ");
            sb.append(poiLocation.getLatitude());
            latitude = poiLocation.getLatitude();

            sb.append("\nlontitude : ");
            sb.append(poiLocation.getLongitude());
            longitude = poiLocation.getLongitude();
            sb.append("\nradius : ");
            sb.append(poiLocation.getRadius());
            if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                sb.append("\naddr : ");
                sb.append(poiLocation.getAddrStr());
            }
            if (poiLocation.hasPoi()) {
                sb.append("\nPoi:");
                sb.append(poiLocation.getPoi());
            } else {
                sb.append("noPoi information");
            }
            Log.d("ztt_location", sb.toString());
//            logMsg(sb.toString());
        }
    }

}
