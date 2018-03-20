package com.zidoo.custom.weather;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import com.zidoo.custom.init.ZidooJarPermissions;
import com.zidoo.custom.net.ZidooNetStatusTool;
import com.zidoo.custom.net.ZidooNetStatusTool.OnlyNetWorkListener;

public class ZidoWeather {
    public static final int CHINA = 1;
    private static final String TENENTWEATHER_CHINA = "launcher.3188.weather.broad.action.name";
    private static final String TENENTWEATHER_WORLD = "launcher.weather.broadreceive";
    private static final String UPDATEWEATHER_CHINA = "com.kaiboer.broadcast.remote";
    private static final String UPDATEWEATHER_WORLD = "com.zidoo.weather.broadcast";
    public static final int WORLD = 0;
    private BroadcastReceiver mBroadcastReceiver = null;
    private Context mContext = null;
    private ZidooNetStatusTool mNetTool = null;
    private int mWeatherType = 1;
    private WeatherUpdateListner mWeatherUpdateListner = null;

    public interface WeatherUpdateListner {
        void update(int i, String str, String str2, String str3, String str4, String str5);
    }

    public ZidoWeather(Context context, WeatherUpdateListner weatherUpdateListner, ZidooNetStatusTool netTool, int weatherType) {
        ZidooJarPermissions.checkZidooPermissions();
        this.mContext = context;
        this.mWeatherUpdateListner = weatherUpdateListner;
        this.mNetTool = netTool;
        this.mWeatherType = weatherType;
        initData();
    }

    private void initData() {
        if (this.mNetTool != null) {
            this.mNetTool.addOnlyNetWorkListener(new OnlyNetWorkListener() {
                public void netWorkConnected(boolean isConnected) {
                    if (isConnected) {
                        ZidoWeather.this.refreshWeather();
                    }
                }
            });
        }
        this.mBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                try {
                    String action = intent.getAction();
                    System.out.println("bob  weather action = " + action);
                    if (action.equals(ZidoWeather.TENENTWEATHER_CHINA)) {
                        ContentValues values = (ContentValues) intent.getParcelableExtra("weather_info");
                        int num = values.getAsInteger("img_weather").intValue();
                        String tempera = values.getAsString("todayTempStr");
                        String tempera_str = values.getAsString("weather");
                        String city_str = values.getAsString("city");
                        String p_tempStr = values.getAsString("p_tempStr");
                        if (city_str.contains("市")) {
                            city_str = city_str.replace("市", "");
                        }
                        if (ZidoWeather.this.mWeatherUpdateListner != null) {
                            ZidoWeather.this.mWeatherUpdateListner.update(num, p_tempStr != null ? p_tempStr : "", tempera != null ? tempera : "", tempera_str != null ? tempera_str : "", city_str != null ? city_str : "", "中国");
                        }
                    } else if (action.equals(ZidoWeather.TENENTWEATHER_WORLD)) {
                        try {
                            Bundle bundle = intent.getBundleExtra("weather_info");
                            int code = Integer.valueOf(bundle.getString("code")).intValue();
                            String temp = bundle.getString("temp");
                            String currenttemp = bundle.getString("currenttemp");
                            String text = bundle.getString("text");
                            String city = bundle.getString("city");
                            String country = bundle.getString("country");
                            if (ZidoWeather.this.mWeatherUpdateListner != null) {
                                String str;
                                WeatherUpdateListner access$0 = ZidoWeather.this.mWeatherUpdateListner;
                                String str2 = currenttemp != null ? currenttemp : "";
                                String str3 = temp != null ? temp : "";
                                String str4 = text != null ? text : "";
                                if (city != null) {
                                    str = city;
                                } else {
                                    str = "";
                                }
                                access$0.update(code, str2, str3, str4, str, country != null ? country : "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        };
        IntentFilter infilter = new IntentFilter();
        if (this.mWeatherType == 0) {
            infilter.addAction(TENENTWEATHER_WORLD);
        } else {
            infilter.addAction(TENENTWEATHER_CHINA);
        }
        this.mContext.registerReceiver(this.mBroadcastReceiver, infilter);
        refreshWeather();
    }

    public void refreshWeather() {
        if (this.mWeatherType == 0) {
            this.mContext.sendBroadcast(new Intent(UPDATEWEATHER_WORLD));
            return;
        }
        System.out.println("bob send weather provide 1 = com.kaiboer.broadcast.remote");
        initApp(this.mContext, "com.zidoo.tool.weather.provide");
    }

    public void initApp(Context context, String url) {
        try {
            context.getContentResolver().getType(ContentUris.withAppendedId(Uri.parse("content://" + url + "/"), 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void release() {
        try {
            if (this.mNetTool != null) {
                this.mNetTool.release();
            }
            if (this.mBroadcastReceiver != null) {
                this.mContext.unregisterReceiver(this.mBroadcastReceiver);
                this.mBroadcastReceiver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
