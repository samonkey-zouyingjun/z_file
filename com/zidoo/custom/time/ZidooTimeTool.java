package com.zidoo.custom.time;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import com.zidoo.custom.init.ZidooJarPermissions;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ZidooTimeTool {
    private static int COUNTDATA = 5000;
    private static int UPDATATIME = 1000;
    private Context mContext = null;
    private String mCurrentTime = "";
    private DataListener mDataListener = null;
    private Handler mHandler = null;
    private String[] mWeekValue = null;

    public interface DataListener {
        void updateClock(int i, int i2, int i3);

        void updateDate(ZidooTimeInfo zidooTimeInfo);
    }

    public ZidooTimeTool(Context mContext, String[] mWeekValue, DataListener dataListener) {
        ZidooJarPermissions.checkZidooPermissions();
        this.mContext = mContext;
        this.mWeekValue = mWeekValue;
        this.mDataListener = dataListener;
        initData();
        startData();
    }

    public void setUpdateTime(int updataTime) {
        UPDATATIME = updataTime;
    }

    public String getWeek_String() {
        if (this.mWeekValue == null || this.mWeekValue.length != 7) {
            return "";
        }
        return this.mWeekValue[getWeek() - 1];
    }

    private void initData() {
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        ZidooTimeInfo zidooDataInfo = ZidooTimeTool.this.getTime();
                        if (!zidooDataInfo.getCurrentTime().equals(ZidooTimeTool.this.mCurrentTime)) {
                            ZidooTimeTool.this.mCurrentTime = zidooDataInfo.getCurrentTime();
                            if (ZidooTimeTool.this.mDataListener != null) {
                                ZidooTimeTool.this.mDataListener.updateDate(zidooDataInfo);
                            }
                        }
                        ZidooTimeTool.this.mHandler.sendEmptyMessageDelayed(0, (long) ZidooTimeTool.COUNTDATA);
                        return;
                    case 1:
                        Calendar ca = Calendar.getInstance();
                        int hour = ca.get(10);
                        int minute = ca.get(12);
                        int second = ca.get(13);
                        if (ZidooTimeTool.this.mDataListener != null) {
                            ZidooTimeTool.this.mDataListener.updateClock(hour, minute, second);
                        }
                        ZidooTimeTool.this.mHandler.sendEmptyMessageDelayed(1, (long) ZidooTimeTool.UPDATATIME);
                        return;
                    default:
                        return;
                }
            }
        };
    }

    public void release() {
        try {
            stopData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopData() {
        this.mHandler.removeMessages(0);
        this.mHandler.removeMessages(1);
    }

    public void startData() {
        stopData();
        this.mHandler.sendEmptyMessage(0);
        this.mHandler.sendEmptyMessage(1);
    }

    private ZidooTimeInfo getTime() {
        Calendar ca = Calendar.getInstance();
        int year = ca.get(1);
        int month = ca.get(2) + 1;
        int day = ca.get(5);
        int hour = ca.get(10);
        int minute = ca.get(12);
        int second = ca.get(13);
        int week = ca.get(7);
        String date = new StringBuilder(String.valueOf(day)).append("/").append(month).append("/").append(year).toString();
        String am_pm = null;
        if (!DateFormat.is24HourFormat(this.mContext)) {
            if (ca.get(9) == 0) {
                am_pm = "AM";
            } else {
                am_pm = "PM";
            }
            if (hour == 0) {
                hour = 12;
            }
        } else if (ca.get(9) != 0) {
            hour += 12;
        }
        String currentWeek = "";
        if (this.mWeekValue == null || this.mWeekValue.length != 7) {
            currentWeek = "";
        } else {
            currentWeek = this.mWeekValue[week - 1];
        }
        String time = new StringBuilder(String.valueOf(hour)).append(":").toString();
        if (minute < 10) {
            time = new StringBuilder(String.valueOf(time)).append("0").append(minute).toString();
        } else {
            time = new StringBuilder(String.valueOf(time)).append(minute).toString();
        }
        CalendarUtil calendarUtil = new CalendarUtil(ca);
        ZidooTimeInfo zidooDataInfo = new ZidooTimeInfo(year, month, day, hour, minute, second, week, time, date, currentWeek, am_pm);
        zidooDataInfo.setCurrentLuchDay(calendarUtil.toString());
        zidooDataInfo.setLuchMonth(calendarUtil.getLuchMonth());
        zidooDataInfo.setLuchDay(calendarUtil.getLuchDay());
        return zidooDataInfo;
    }

    public static int getYear() {
        return Calendar.getInstance().get(1);
    }

    public static int getMonth() {
        return Calendar.getInstance().get(2) + 1;
    }

    public static int getDay() {
        return Calendar.getInstance().get(5);
    }

    public static int getHour() {
        return Calendar.getInstance().get(10);
    }

    public static int getMinute() {
        return Calendar.getInstance().get(12);
    }

    public static int getSecond() {
        return Calendar.getInstance().get(13);
    }

    public static int getWeek() {
        return Calendar.getInstance().get(7);
    }

    public static String getWeek_String(String[] mWeekValue) {
        if (mWeekValue != null && mWeekValue.length == 7) {
            return mWeekValue[getWeek() - 1];
        }
        throw new RuntimeException("zidoo mWeekValue is error");
    }

    public static String getCurrentTime() {
        return new SimpleDateFormat("HH:mm").format(new Date());
    }

    public static String getCurrentData() {
        Calendar ca = Calendar.getInstance();
        return ca.get(5) + "/" + (ca.get(2) + 1) + "/" + ca.get(1);
    }

    public static String getCurrentMonthOrDay() {
        Calendar ca = Calendar.getInstance();
        return ca.get(5) + "/" + (ca.get(2) + 1);
    }

    public static String getCurrentTime(SimpleDateFormat sTimeFormat) {
        return sTimeFormat.format(new Date());
    }

    public static String getCurrentData(SimpleDateFormat sTimeFormat) {
        return sTimeFormat.format(new Date());
    }

    public static int getLuchMonth() {
        return new CalendarUtil(Calendar.getInstance()).getLuchMonth();
    }

    public static String getLuch() {
        return new CalendarUtil(Calendar.getInstance()).toString();
    }

    public static int getLuchDay() {
        return new CalendarUtil(Calendar.getInstance()).getLuchDay();
    }

    public static String getPlayTimeString(long time) {
        if (time == -1) {
            return "00:00:00";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(Long.valueOf(time));
    }

    public static String formatTime(long time, String format) {
        String timeStr = "";
        try {
            timeStr = new SimpleDateFormat(format).format(new Date(time));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeStr;
    }

    public static String getCurrentTime(String format) {
        String timeStr = "";
        try {
            timeStr = new SimpleDateFormat(format).format(new Date(System.currentTimeMillis()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeStr;
    }
}
