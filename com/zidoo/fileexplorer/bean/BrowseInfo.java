package com.zidoo.fileexplorer.bean;

import com.zidoo.fileexplorer.tool.BrowsingListener;
import zidoo.browse.FileIdentifier;

public class BrowseInfo {
    int bgd;
    int clickModel;
    String[] customExtras = null;
    int device;
    int filter = -1;
    int flag;
    int help;
    float horizontalPadding;
    String initialPath;
    BrowsingListener listener;
    String name;
    String pkg;
    float scale;
    int shade;
    int target;
    String title;
    private int verstion;
    float verticalPadding;

    public BrowseInfo(String title, String name, int device, int filter, int target, String[] customExtras, int flag, String pkg, int bgd, int help, float scale, int shade, float horizontalPadding, float verticalPadding, String initialPath, int clickModel, int verstion) {
        this.title = title;
        this.name = name;
        this.device = device;
        this.filter = filter;
        this.target = target;
        this.customExtras = customExtras;
        this.flag = flag;
        this.pkg = pkg;
        this.bgd = bgd;
        this.help = help;
        this.scale = scale;
        this.shade = shade;
        this.horizontalPadding = horizontalPadding;
        this.verticalPadding = verticalPadding;
        this.initialPath = initialPath;
        this.clickModel = clickModel;
        this.verstion = verstion;
    }

    public int getBackgroundResource() {
        return this.bgd;
    }

    public String getPackageName() {
        return this.pkg;
    }

    public void setListener(BrowsingListener listener) {
        this.listener = listener;
    }

    public int getHelp() {
        return this.help;
    }

    public void setHelp(int help) {
        this.help = help;
    }

    public String getTitle() {
        return this.title;
    }

    public String getName() {
        return this.name;
    }

    public int getTarget() {
        return this.target;
    }

    public int getFlag() {
        return this.flag;
    }

    public int getDeviceTag() {
        return this.device;
    }

    public int getFilter() {
        return this.filter;
    }

    public String[] getCustomExtras() {
        return this.customExtras;
    }

    public void onBrowsing(FileIdentifier identifier) {
        if (this.listener != null) {
            this.listener.onBrowsing(identifier);
        }
    }

    public float getScale() {
        return this.scale;
    }

    public int getShade() {
        return this.shade;
    }

    public float getHorizontalPadding() {
        return this.horizontalPadding;
    }

    public float getVerticalPadding() {
        return this.verticalPadding;
    }

    public String getInitialPath() {
        return this.initialPath;
    }

    public int getClickModel() {
        return this.clickModel;
    }
}
