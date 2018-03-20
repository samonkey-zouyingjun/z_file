package com.zidoo.custom.app;

import android.os.Bundle;
import java.io.Serializable;

public class ZidooStartAppInfo implements Serializable {
    public static final int action_AC = 1;
    public static final int action_BR = 4;
    public static final int package_AC = 0;
    public static final int package_action_AC = 2;
    public static final int package_activity_AC = 3;
    private String action = null;
    private String activity = null;
    private Bundle bundle = null;
    private String[] key_boolean = null;
    private String[] key_double = null;
    private String[] key_float = null;
    private String[] key_int = null;
    private String[] key_str = null;
    private int openType = -1;
    private String pckName = null;
    private boolean[] value_boolean = null;
    private double[] value_double = null;
    private float[] value_float = null;
    private int[] value_int = null;
    private String[] value_str = null;

    public ZidooStartAppInfo(String pckName, int openType) {
        this.pckName = pckName;
        this.openType = openType;
    }

    public ZidooStartAppInfo(String pckName) {
        this.pckName = pckName;
        this.openType = 0;
    }

    public String getPckName() {
        return this.pckName;
    }

    public void setPckName(String pckName) {
        this.pckName = pckName;
    }

    public String[] getKey_str() {
        return this.key_str;
    }

    public void setKey_str(String[] key_str) {
        this.key_str = key_str;
    }

    public String[] getValue_str() {
        return this.value_str;
    }

    public void setValue_str(String[] value_str) {
        this.value_str = value_str;
    }

    public String[] getKey_int() {
        return this.key_int;
    }

    public void setKey_int(String[] key_int) {
        this.key_int = key_int;
    }

    public int[] getValue_int() {
        return this.value_int;
    }

    public void setValue_int(int[] value_int) {
        this.value_int = value_int;
    }

    public Bundle getBundle() {
        return this.bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String[] getKey_boolean() {
        return this.key_boolean;
    }

    public void setKey_boolean(String[] key_boolean) {
        this.key_boolean = key_boolean;
    }

    public boolean[] getValue_boolean() {
        return this.value_boolean;
    }

    public void setValue_boolean(boolean[] value_boolean) {
        this.value_boolean = value_boolean;
    }

    public String[] getKey_double() {
        return this.key_double;
    }

    public void setKey_double(String[] key_double) {
        this.key_double = key_double;
    }

    public double[] getValue_double() {
        return this.value_double;
    }

    public void setValue_double(double[] value_double) {
        this.value_double = value_double;
    }

    public String[] getKey_float() {
        return this.key_float;
    }

    public void setKey_float(String[] key_float) {
        this.key_float = key_float;
    }

    public float[] getValue_float() {
        return this.value_float;
    }

    public void setValue_float(float[] value_float) {
        this.value_float = value_float;
    }

    public String getActivity() {
        return this.activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public int getOpenType() {
        return this.openType;
    }

    public void setOpenType(int openType) {
        this.openType = openType;
    }
}
