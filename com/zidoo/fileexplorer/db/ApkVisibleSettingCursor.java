package com.zidoo.fileexplorer.db;

import android.database.AbstractCursor;

public class ApkVisibleSettingCursor extends AbstractCursor {
    public int visible;

    public ApkVisibleSettingCursor(boolean visible) {
        this.visible = visible ? 1 : 0;
    }

    public int getCount() {
        return 1;
    }

    public String[] getColumnNames() {
        return new String[]{"visible"};
    }

    public String getString(int column) {
        return String.valueOf(this.visible);
    }

    public short getShort(int column) {
        return (short) this.visible;
    }

    public int getInt(int column) {
        return this.visible;
    }

    public long getLong(int column) {
        return (long) this.visible;
    }

    public float getFloat(int column) {
        return (float) this.visible;
    }

    public double getDouble(int column) {
        return (double) this.visible;
    }

    public boolean isNull(int column) {
        return column > 0;
    }
}
