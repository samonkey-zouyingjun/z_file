package com.zidoo.fileexplorer.gl;

import com.zidoo.fileexplorer.adapter.MenuAdapter;
import com.zidoo.fileexplorer.menu.FileMenu;
import zidoo.tarot.GLContext;

public class MenuView {
    MenuAdapter mAdapter;
    int mMenuType = -1;
    MyView mView;

    public MenuView(GLContext glContext, MyView view) {
        this.mView = view;
        this.mView.initMenu(glContext);
        this.mAdapter = new MenuAdapter(glContext);
        this.mView.menu.setAdapter(this.mAdapter);
    }

    public void showMenu(int type, FileMenu[] menuInfos) {
        boolean visible = false;
        this.mAdapter.setMenuInfos(menuInfos);
        this.mAdapter.notifyDataSetChanged();
        if (type != this.mMenuType) {
            this.mMenuType = type;
            this.mView.menu.setSelection(0);
            if (menuInfos.length > 6) {
                visible = true;
            }
            this.mView.imgMenuUp.setVisibility(visible);
            this.mView.imgMenuDown.setVisibility(visible);
        }
    }

    public FileMenu getMenuInfo(int position) {
        return this.mAdapter.getItem(position);
    }

    public int getCount() {
        return this.mAdapter.getCount();
    }

    public int getType() {
        return this.mMenuType;
    }
}
