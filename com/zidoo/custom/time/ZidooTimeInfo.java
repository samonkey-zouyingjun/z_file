package com.zidoo.custom.time;

import java.io.Serializable;

public class ZidooTimeInfo implements Serializable {
    private String am_pm = null;
    private String currentDay = "";
    private String currentLuchDay = "";
    private String currentTime = "";
    private String currentWeek = "";
    private int day = 0;
    private int hour = 0;
    private int luchDay = 0;
    private int luchMonth = 0;
    private int minute = 0;
    private int month = 0;
    private int second = 0;
    private int week = 0;
    private int year = 0;

    public String getCurrentLuchDay() {
        return this.currentLuchDay;
    }

    public void setCurrentLuchDay(String currentLuchDay) {
        this.currentLuchDay = currentLuchDay;
    }

    public int getYear() {
        return this.year;
    }

    public ZidooTimeInfo(int year, int month, int day, int hour, int minute, int second, int week, String currentTime, String currentDay, String currentWeek, String currentLuchDay, String am_pm) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.week = week;
        this.currentTime = currentTime;
        this.currentDay = currentDay;
        this.currentWeek = currentWeek;
        this.currentLuchDay = currentLuchDay;
        this.am_pm = am_pm;
    }

    public String getAm_pm() {
        return this.am_pm;
    }

    public void setAm_pm(String am_pm) {
        this.am_pm = am_pm;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return this.month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return this.day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return this.hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return this.minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return this.second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public String getCurrentTime() {
        return this.currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getCurrentDay() {
        return this.currentDay;
    }

    public void setCurrentDay(String currentDay) {
        this.currentDay = currentDay;
    }

    public ZidooTimeInfo(int year, int month, int day, int hour, int minute, int second, int week, String currentTime, String currentDay, String currentWeek, String am_pm) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.week = week;
        this.currentTime = currentTime;
        this.currentDay = currentDay;
        this.currentWeek = currentWeek;
        this.am_pm = am_pm;
    }

    public int getWeek() {
        return this.week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public String getCurrentWeek() {
        return this.currentWeek;
    }

    public void setCurrentWeek(String currentWeek) {
        this.currentWeek = currentWeek;
    }

    public int getLuchMonth() {
        return this.luchMonth;
    }

    public void setLuchMonth(int luchMonth) {
        this.luchMonth = luchMonth;
    }

    public int getLuchDay() {
        return this.luchDay;
    }

    public void setLuchDay(int luchDay) {
        this.luchDay = luchDay;
    }

    public ZidooTimeInfo(int year, int month, int day, int hour, int minute, int second, int week, int luchMonth, int luchDay, String currentTime, String currentDay, String currentWeek, String currentLuchDay, String am_pm) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.week = week;
        this.luchMonth = luchMonth;
        this.luchDay = luchDay;
        this.currentTime = currentTime;
        this.currentDay = currentDay;
        this.currentWeek = currentWeek;
        this.currentLuchDay = currentLuchDay;
        this.am_pm = am_pm;
    }

    public String toString() {
        return "ZidooDataInfo [year=" + this.year + ", month=" + this.month + ", day=" + this.day + ", hour=" + this.hour + ", minute=" + this.minute + ", second=" + this.second + ", week=" + this.week + ", luchMonth=" + this.luchMonth + ", luchDay=" + this.luchDay + ", currentTime=" + this.currentTime + ", currentDay=" + this.currentDay + ", currentWeek=" + this.currentWeek + ", currentLuchDay=" + this.currentLuchDay + "]";
    }
}
