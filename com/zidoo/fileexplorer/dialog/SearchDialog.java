package com.zidoo.fileexplorer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.gl.FileMolder;

public class SearchDialog extends Dialog implements OnClickListener {
    FileMolder fileMolder;
    RelativeLayout[] items;
    int selected = 0;

    public SearchDialog(Context context, int theme, FileMolder fileMolder, int researchModel) {
        super(context, theme);
        this.fileMolder = fileMolder;
        this.selected = researchModel;
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_search);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.items = new RelativeLayout[3];
        this.items[0] = (RelativeLayout) findViewById(R.id.rela_search_accurate);
        this.items[1] = (RelativeLayout) findViewById(R.id.rela_search_normal);
        this.items[2] = (RelativeLayout) findViewById(R.id.rela_search_quick);
        this.items[0].setOnClickListener(this);
        this.items[1].setOnClickListener(this);
        this.items[2].setOnClickListener(this);
        this.items[this.selected].getChildAt(1).setVisibility(0);
        this.items[this.selected].requestFocus();
    }

    private void set(int select) {
        if (this.selected != select) {
            this.items[this.selected].getChildAt(1).setVisibility(8);
            this.selected = select;
            this.items[this.selected].getChildAt(1).setVisibility(0);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rela_search_accurate:
                set(0);
                break;
            case R.id.rela_search_normal:
                set(1);
                break;
            case R.id.rela_search_quick:
                set(2);
                break;
        }
        if (this.fileMolder.researchSmb(this.selected)) {
            dismiss();
        }
        dismiss();
    }
}
