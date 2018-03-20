package com.zidoo.control.center.http.tool;

import java.util.HashMap;

public class ZidooHttpParmInfo {
    public String mElementName = null;
    public HashMap<String, String> mParmsHashMap = new HashMap();
    public String mUri = null;

    public String toString() {
        return "ZidooHttpParmInfo [mParmsHashMap=" + this.mParmsHashMap + ", mElementName=" + this.mElementName + ", mUri=" + this.mUri + "]";
    }
}
