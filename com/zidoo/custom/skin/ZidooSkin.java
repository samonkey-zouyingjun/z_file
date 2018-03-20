package com.zidoo.custom.skin;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import com.umeng.common.util.e;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

@SuppressLint({"SdCardPath", "WorldReadableFiles", "NewApi", "WorldWriteableFiles"})
public class ZidooSkin {
    public static final String ROOTPATH = "/mnt/sdcard/.ZidooSkin/";
    public static ZidooSkin sMe = null;
    private final String CHANGE_BROADCAST = "zidoo_skin_broadcast";
    private final String KEY_THEME = "theme";
    private final String SHARE_NAME = "skin";
    private Context mContext;
    private BroadcastReceiver mReceiver;
    private boolean mRegister;
    private ArrayList<ZidooSkinCallBack> mSkinCallBacks = new ArrayList();
    private SkinParser mSkinParser;

    public class SkinParser {
        Context context;
        String theme;

        private SkinParser(Context context) {
            this.theme = null;
            this.context = context;
            Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.zidoo.ContentProvider.skin/admin"), null, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                this.theme = cursor.getString(1);
            }
        }

        private void setTheme(String theme) {
            this.theme = theme;
        }

        private boolean isSameAs(String theme) {
            return theme.equals(this.theme);
        }

        public Drawable getDrawable(String name, int defaultId) {
            if (this.theme == null) {
                return this.context.getResources().getDrawable(defaultId);
            }
            Drawable drawable = getDrawable(name);
            return drawable == null ? this.context.getResources().getDrawable(defaultId) : drawable;
        }

        public Bitmap getBitmap(String name, int defaultId) {
            String path = searchPictureFile(name);
            return path != null ? BitmapFactory.decodeFile(path) : BitmapFactory.decodeResource(this.context.getResources(), defaultId);
        }

        public ColorStateList getColorStateList(String name, int defaultId) {
            ColorStateList stateList = getColorStateList(name);
            return stateList == null ? this.context.getResources().getColorStateList(defaultId) : stateList;
        }

        private String searchPictureFile(String pre) {
            String path = new StringBuilder(String.valueOf(pre)).append(".jpg").toString();
            if (!new File(path).exists()) {
                path = new StringBuilder(String.valueOf(pre)).append(".png").toString();
                if (!new File(path).exists()) {
                    path = new StringBuilder(String.valueOf(pre)).append(".9.png").toString();
                    if (!new File(path).exists()) {
                        return null;
                    }
                }
            }
            return path;
        }

        public Drawable getDrawable(String name) {
            String rootpath = new StringBuilder(ZidooSkin.ROOTPATH).append(this.theme).toString();
            String path = searchPictureFile(new StringBuilder(String.valueOf(rootpath)).append("/drawable-mdpi/").append(name).toString());
            if (path != null) {
                return new BitmapDrawable(null, BitmapFactory.decodeFile(path));
            }
            path = new StringBuilder(String.valueOf(rootpath)).append("/drawable/").append(name).append(".xml").toString();
            return new File(path).exists() ? parseXml(path) : null;
        }

        public Bitmap getBitmap(String name) {
            String path = searchPictureFile(new StringBuilder(ZidooSkin.ROOTPATH).append(this.theme).append("/drawable-mdpi/").append(name).toString());
            return path != null ? BitmapFactory.decodeFile(path) : null;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private android.graphics.drawable.Drawable parseXml(java.lang.String r17) {
            /*
            r16 = this;
            r2 = 0;
            r12 = 0;
            r10 = new java.util.ArrayList;	 Catch:{ XmlPullParserException -> 0x00f2, IOException -> 0x00f0 }
            r10.<init>();	 Catch:{ XmlPullParserException -> 0x00f2, IOException -> 0x00f0 }
            r14 = org.xmlpull.v1.XmlPullParserFactory.newInstance();	 Catch:{ XmlPullParserException -> 0x00f2, IOException -> 0x00f0 }
            r8 = r14.newPullParser();	 Catch:{ XmlPullParserException -> 0x00f2, IOException -> 0x00f0 }
            r14 = new java.io.FileInputStream;	 Catch:{ XmlPullParserException -> 0x00f2, IOException -> 0x00f0 }
            r0 = r17;
            r14.<init>(r0);	 Catch:{ XmlPullParserException -> 0x00f2, IOException -> 0x00f0 }
            r15 = "UTF-8";
            r8.setInput(r14, r15);	 Catch:{ XmlPullParserException -> 0x00f2, IOException -> 0x00f0 }
            r5 = r8.getEventType();	 Catch:{ XmlPullParserException -> 0x00f2, IOException -> 0x00f0 }
            r3 = r2;
        L_0x0021:
            r14 = 1;
            if (r5 != r14) goto L_0x0026;
        L_0x0024:
            r2 = r3;
        L_0x0025:
            return r2;
        L_0x0026:
            switch(r5) {
                case 0: goto L_0x0030;
                case 1: goto L_0x0029;
                case 2: goto L_0x0032;
                case 3: goto L_0x00c4;
                default: goto L_0x0029;
            };	 Catch:{ XmlPullParserException -> 0x00f2, IOException -> 0x00f0 }
        L_0x0029:
            r2 = r3;
        L_0x002a:
            r5 = r8.next();	 Catch:{ XmlPullParserException -> 0x00f2, IOException -> 0x00f0 }
            r3 = r2;
            goto L_0x0021;
        L_0x0030:
            r2 = r3;
            goto L_0x002a;
        L_0x0032:
            r14 = "selector";
            r15 = r8.getName();	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            r14 = r14.equals(r15);	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            if (r14 == 0) goto L_0x0045;
        L_0x003f:
            r2 = new android.graphics.drawable.StateListDrawable;	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            r2.<init>();	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            goto L_0x002a;
        L_0x0045:
            r14 = "item";
            r15 = r8.getName();	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            r14 = r14.equals(r15);	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            if (r14 == 0) goto L_0x0029;
        L_0x0052:
            r1 = r8.getAttributeCount();	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            r6 = 0;
        L_0x0057:
            if (r6 < r1) goto L_0x005b;
        L_0x0059:
            r2 = r3;
            goto L_0x002a;
        L_0x005b:
            r7 = r8.getAttributeName(r6);	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            r14 = "android:drawable";
            r14 = r7.equals(r14);	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            if (r14 == 0) goto L_0x0075;
        L_0x0068:
            r14 = r8.getAttributeValue(r6);	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            r0 = r16;
            r12 = r0.parseDrawable(r14);	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
        L_0x0072:
            r6 = r6 + 1;
            goto L_0x0057;
        L_0x0075:
            r14 = "android:state_focused";
            r14 = r7.equals(r14);	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            if (r14 == 0) goto L_0x009c;
        L_0x007e:
            r13 = r8.getAttributeValue(r6);	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            r14 = "true";
            r14 = r13.equals(r14);	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            if (r14 == 0) goto L_0x0072;
        L_0x008b:
            r14 = 16842908; // 0x101009c float:2.3693995E-38 double:8.321502E-317;
            r14 = java.lang.Integer.valueOf(r14);	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            r10.add(r14);	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            goto L_0x0072;
        L_0x0096:
            r4 = move-exception;
            r2 = r3;
        L_0x0098:
            r4.printStackTrace();
            goto L_0x0025;
        L_0x009c:
            r14 = "android:state_pressed";
            r14 = r7.equals(r14);	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            if (r14 == 0) goto L_0x0072;
        L_0x00a5:
            r13 = r8.getAttributeValue(r6);	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            r14 = "true";
            r14 = r13.equals(r14);	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            if (r14 == 0) goto L_0x0072;
        L_0x00b2:
            r14 = 16842919; // 0x10100a7 float:2.3694026E-38 double:8.3215077E-317;
            r14 = java.lang.Integer.valueOf(r14);	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            r10.add(r14);	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            goto L_0x0072;
        L_0x00bd:
            r4 = move-exception;
            r2 = r3;
        L_0x00bf:
            r4.printStackTrace();
            goto L_0x0025;
        L_0x00c4:
            if (r12 == 0) goto L_0x00da;
        L_0x00c6:
            r14 = r3 instanceof android.graphics.drawable.StateListDrawable;	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            if (r14 == 0) goto L_0x00da;
        L_0x00ca:
            r9 = r10.size();	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            r11 = new int[r9];	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            r6 = 0;
        L_0x00d1:
            if (r6 < r9) goto L_0x00e1;
        L_0x00d3:
            r0 = r3;
            r0 = (android.graphics.drawable.StateListDrawable) r0;	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            r14 = r0;
            r14.addState(r11, r12);	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
        L_0x00da:
            r12 = 0;
            r10.clear();	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            r2 = r3;
            goto L_0x002a;
        L_0x00e1:
            r14 = r10.get(r6);	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            r14 = (java.lang.Integer) r14;	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            r14 = r14.intValue();	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            r11[r6] = r14;	 Catch:{ XmlPullParserException -> 0x0096, IOException -> 0x00bd }
            r6 = r6 + 1;
            goto L_0x00d1;
        L_0x00f0:
            r4 = move-exception;
            goto L_0x00bf;
        L_0x00f2:
            r4 = move-exception;
            goto L_0x0098;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.zidoo.custom.skin.ZidooSkin.SkinParser.parseXml(java.lang.String):android.graphics.drawable.Drawable");
        }

        public ColorStateList getColorStateList(String filename) {
            ArrayList<int[]> stateList = new ArrayList();
            ArrayList<Integer> colors = new ArrayList();
            try {
                int i;
                String path = new StringBuilder(ZidooSkin.ROOTPATH).append(this.theme).append("/drawable/").append(filename).append(".xml").toString();
                XmlPullParser pullParser = XmlPullParserFactory.newInstance().newPullParser();
                pullParser.setInput(new FileInputStream(path), e.f);
                Integer color = null;
                int[] states = null;
                for (int eventType = pullParser.getEventType(); eventType != 1; eventType = pullParser.next()) {
                    switch (eventType) {
                        case 2:
                            if (!"selector".equals(pullParser.getName()) && "item".equals(pullParser.getName())) {
                                int count = pullParser.getAttributeCount();
                                Object tempStates = new int[count];
                                i = 0;
                                int length = 0;
                                while (i < count) {
                                    int length2;
                                    String name = pullParser.getAttributeName(i);
                                    if (name.equals("android:color")) {
                                        color = Integer.valueOf(parseColor(pullParser.getAttributeValue(i)));
                                        length2 = length;
                                    } else {
                                        if (name.equals("android:state_focused")) {
                                            if (pullParser.getAttributeValue(i).equals("true")) {
                                                length2 = length + 1;
                                                tempStates[length] = 16842908;
                                            }
                                        } else if (name.equals("android:state_pressed") && pullParser.getAttributeValue(i).equals("true")) {
                                            length2 = length + 1;
                                            tempStates[length] = 16842919;
                                        }
                                        length2 = length;
                                    }
                                    i++;
                                    length = length2;
                                }
                                Object states2 = new int[length];
                                System.arraycopy(tempStates, 0, states2, 0, length);
                                break;
                            }
                        case 3:
                            if (!(color == null || states == null)) {
                                stateList.add(states);
                                colors.add(color);
                            }
                            color = null;
                            states = null;
                            break;
                        default:
                            break;
                    }
                }
                int size = stateList.size();
                int[][] s = new int[size][];
                for (i = 0; i < size; i++) {
                    s[i] = (int[]) stateList.get(i);
                }
                int[] c = new int[size];
                for (i = 0; i < size; i++) {
                    c[i] = ((Integer) colors.get(i)).intValue();
                }
                return new ColorStateList(s, c);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e2) {
                e2.printStackTrace();
                return null;
            }
        }

        private Drawable parseDrawable(String value) {
            Matcher m = Pattern.compile("@[a-z]*/").matcher(value);
            if (m.find()) {
                String tag = m.group();
                if (tag.equals("@drawable/")) {
                    return getDrawable(value.substring(tag.length()));
                }
                if (tag.equals("@color/")) {
                    int color = getColor(value.substring(tag.length()));
                    if (color != -1) {
                        return new ColorDrawable(color);
                    }
                }
            }
            return null;
        }

        private int parseColor(String value) {
            if (value.charAt(0) == '#') {
                return Color.parseColor(value);
            }
            Matcher m = Pattern.compile("@[a-z|:]*/").matcher(value);
            if (!m.find()) {
                return 0;
            }
            String tag = m.group();
            if (tag.equals("@android:color/")) {
                return getColorByAndroid(value.substring(tag.length()));
            }
            if (tag.equals("@color/")) {
                return getColor(value.substring(tag.length()));
            }
            return 0;
        }

        public int getColor(String name) {
            try {
                String path = new StringBuilder(ZidooSkin.ROOTPATH).append(this.theme).append("/colors.xml").toString();
                XmlPullParser pullParser = XmlPullParserFactory.newInstance().newPullParser();
                pullParser.setInput(new FileInputStream(path), e.f);
                for (int eventType = pullParser.getEventType(); eventType != 1; eventType = pullParser.next()) {
                    switch (eventType) {
                        case 2:
                            if ("color".equals(pullParser.getName()) && pullParser.getAttributeValue("", "name").equals(name)) {
                                return Color.parseColor(pullParser.nextText());
                            }
                        default:
                            break;
                    }
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            return 0;
        }

        private int getColorByAndroid(String name) {
            String[] key = new String[]{"background_dark", "background_light", "black", "darker_gray", "holo_blue_bright", "holo_blue_dark", "holo_blue_light", "holo_green_dark", "holo_green_light", "holo_orange_dark", "holo_orange_light", "holo_purple", "holo_red_dark", "holo_red_light", "transparent", "white"};
            String[] value = new String[]{"#ff000000", "#ffffffff", "#ff000000", "#aaaaaa", "#ff00ddff", "#ff0099cc", "#ff33b5e5", "#ff669900", "#ff99cc00", "#ffff8800", "#ffffbb33", "#ffaa66cc", "#ffcc0000", "#ffff4444", "#00000000", "#ffffffff"};
            for (int i = 0; i < value.length; i++) {
                if (name.equals(key[i])) {
                    return Color.parseColor(value[i]);
                }
            }
            return 0;
        }
    }

    public interface ZidooSkinCallBack {
        void setSkin(SkinParser skinParser);
    }

    public static ZidooSkin getInstance(Context context) {
        if (sMe == null) {
            sMe = new ZidooSkin(context);
        }
        return sMe;
    }

    private ZidooSkin(Context context) {
        this.mContext = context;
        this.mSkinParser = new SkinParser(context);
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    String theme = intent.getStringExtra("theme");
                    if (theme != null) {
                        ZidooSkin.this.mSkinParser.setTheme(theme);
                        Iterator it = ZidooSkin.this.mSkinCallBacks.iterator();
                        while (it.hasNext()) {
                            ((ZidooSkinCallBack) it.next()).setSkin(ZidooSkin.this.mSkinParser);
                        }
                    }
                }
            }
        };
        this.mContext.registerReceiver(this.mReceiver, new IntentFilter("zidoo_skin_broadcast"));
    }

    public void registerCallBack(ZidooSkinCallBack callBack) {
        this.mSkinCallBacks.add(callBack);
        callBack.setSkin(this.mSkinParser);
    }

    public void unRegisterCallBack(ZidooSkinCallBack callBack) {
        Iterator<ZidooSkinCallBack> iterator = this.mSkinCallBacks.iterator();
        while (iterator.hasNext()) {
            if (((ZidooSkinCallBack) iterator.next()).equals(callBack)) {
                iterator.remove();
                return;
            }
        }
    }

    public void changeSkin(String theme) {
        if (!theme.isEmpty() && !this.mSkinParser.isSameAs(theme)) {
            Editor editor = this.mContext.getSharedPreferences("skin", 2).edit();
            editor.putString("theme", theme);
            editor.commit();
            ContentResolver contentResolver = this.mContext.getContentResolver();
            Uri insertUri = Uri.parse("content://com.zidoo.ContentProvider.skin/admin");
            ContentValues values = new ContentValues();
            values.put("theme", theme);
            contentResolver.update(insertUri, values, " id = ? ", new String[]{"1"});
            Intent intent = new Intent("zidoo_skin_broadcast");
            intent.putExtra("theme", theme);
            this.mContext.sendBroadcast(intent);
        }
    }

    public void destroy() {
        try {
            sMe = null;
            if (this.mReceiver != null) {
                this.mContext.unregisterReceiver(this.mReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
