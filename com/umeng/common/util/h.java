package com.umeng.common.util;

import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

/* compiled from: NetUtil */
public class h {
    public static String a(Map<String, Object> map, String str) {
        if (map == null || map.isEmpty()) {
            return str;
        }
        StringBuilder stringBuilder = new StringBuilder(str);
        Set<String> keySet = map.keySet();
        if (!str.endsWith("?")) {
            stringBuilder.append("?");
        }
        for (String str2 : keySet) {
            stringBuilder.append(URLEncoder.encode(str2) + "=" + URLEncoder.encode(map.get(str2) == null ? "" : map.get(str2).toString()) + "&");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }
}
