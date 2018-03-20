package com.zidoo.fileexplorer.adapter;

import com.zidoo.fileexplorer.bean.ListInfo;
import zidoo.tarot.GLContext;
import zidoo.tarot.adapter.AdjustableAdapter;

public abstract class BaseFileAdapter extends AdjustableAdapter {
    GLContext glContext;
    ListInfo listInfo;
    boolean multiChoose = false;
    final int pageBound;

    public abstract int contentCount();

    protected abstract boolean isList();

    public BaseFileAdapter(GLContext glContext, ListInfo listInfo) {
        this.glContext = glContext;
        this.listInfo = listInfo;
        this.pageBound = isList() ? 9 : 18;
    }

    public boolean isMultiChoose() {
        return this.multiChoose;
    }

    public void setMultiChoose(boolean multiChoose) {
        this.multiChoose = multiChoose;
    }

    public final int getCount() {
        if (isEmpty()) {
            return 1;
        }
        if (isPageOut()) {
            return contentCount() + 1;
        }
        return contentCount();
    }

    public final boolean isEmpty() {
        return contentCount() == 0;
    }

    public final boolean isPageOut() {
        return contentCount() > this.pageBound;
    }

    public void refreash() {
        super.notifyDataSetChanged();
    }
}
