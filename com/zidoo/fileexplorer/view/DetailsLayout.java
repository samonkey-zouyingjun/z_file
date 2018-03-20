package com.zidoo.fileexplorer.view;

import com.zidoo.fileexplorer.R;
import zidoo.tarot.GLContext;
import zidoo.tarot.widget.Layout;
import zidoo.tarot.widget.TImageView;
import zidoo.tarot.widget.WrapMarqueeTextView;

public class DetailsLayout extends Layout {
    public static final int FLAG_DIR = 2;
    public static final int FLAG_FAVORITE = 4;
    public static final int FLAG_FILE = 1;
    public static final int FLAG_NONE = 0;
    public static final int FLAG_SMB_DEVICE = 3;
    final int SPACE = 40;
    WrapMarqueeTextView date;
    TImageView favorite;
    int mFlag;
    WrapMarqueeTextView name;
    WrapMarqueeTextView size;
    WrapMarqueeTextView type;

    public /* bridge */ /* synthetic */ Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public DetailsLayout(GLContext glContext) {
        super(glContext);
        init(glContext);
    }

    void init(GLContext glContext) {
        this.name = new WrapMarqueeTextView(glContext);
        this.name.setTextColor(-3355444);
        this.name.setHeight(50.0f);
        this.name.setTextSize(28.0f);
        this.name.setMaxWidth(1000.0f);
        this.name.setY(15.0f);
        addGameObject(this.name);
        this.type = new WrapMarqueeTextView(glContext);
        this.type.setTextColor(-3355444);
        this.type.setHeight(50.0f);
        this.type.setTextSize(28.0f);
        this.type.setMaxWidth(400.0f);
        this.type.setY(15.0f);
        addGameObject(this.type);
        this.favorite = new TImageView(glContext);
        this.favorite.setWidth(30.0f);
        this.favorite.setHeight(30.0f);
        this.favorite.setY(-36.0f);
        this.favorite.setImageResource(R.drawable.ic_favorite_enable);
        this.favorite.setVisibility(false);
        addGameObject(this.favorite);
        this.size = new WrapMarqueeTextView(glContext);
        this.size.setTextColor(-3355444);
        this.size.setHeight(50.0f);
        this.size.setTextSize(28.0f);
        this.size.setMaxWidth(420.0f);
        this.size.setY(-36.0f);
        addGameObject(this.size);
        this.date = new WrapMarqueeTextView(glContext);
        this.date.setTextColor(-3355444);
        this.date.setWidth(600.0f);
        this.date.setHeight(50.0f);
        this.date.setTextSize(28.0f);
        this.date.setY(-36.0f);
        this.date.setMaxWidth(600.0f);
        addGameObject(this.date);
    }

    public void setDetails(boolean isFavor, String s1, String s2, String s3, String s4, int flag) {
        this.favorite.setVisibility(isFavor);
        int fw = 0;
        if (isFavor) {
            fw = 40;
            this.favorite.setX((30.0f - getWidth()) / 2.0f);
        }
        switch (flag) {
            case 0:
                if (this.mFlag != flag) {
                    this.mFlag = flag;
                    this.type.setVisibility(false);
                    this.name.setVisibility(false);
                    this.date.setVisibility(false);
                    this.size.setVisibility(false);
                    return;
                }
                return;
            case 1:
            case 4:
                if (this.mFlag != flag) {
                    this.type.setVisibility(true);
                    this.name.setVisibility(true);
                    this.date.setVisibility(true);
                    this.size.setVisibility(true);
                    this.mFlag = flag;
                }
                this.type.setText(s1);
                this.name.setText(s2);
                this.date.setText(s3);
                this.size.setText(s4);
                float W = getWidth();
                this.type.setX((this.type.getWidth() - W) / 2.0f);
                this.date.setX(((this.date.getWidth() - W) / 2.0f) + ((float) fw));
                this.name.setX((((this.name.getWidth() - W) / 2.0f) + this.type.getWidth()) + 40.0f);
                this.size.setX(((((this.size.getWidth() - W) / 2.0f) + this.date.getWidth()) + 40.0f) + ((float) fw));
                return;
            case 2:
                if (this.mFlag != flag) {
                    this.type.setVisibility(true);
                    this.name.setVisibility(true);
                    this.date.setVisibility(true);
                    this.size.setVisibility(false);
                    this.mFlag = flag;
                }
                this.type.setText(s1);
                this.name.setText(s2);
                this.date.setText(s3);
                float DW = getWidth();
                this.type.setX((this.type.getWidth() - DW) / 2.0f);
                this.date.setX(((this.date.getWidth() - DW) / 2.0f) + ((float) fw));
                this.name.setX((((this.name.getWidth() - DW) / 2.0f) + this.type.getWidth()) + 40.0f);
                return;
            case 3:
                if (this.mFlag != flag) {
                    this.name.setVisibility(true);
                    this.date.setVisibility(true);
                    this.type.setVisibility(true);
                    this.size.setVisibility(false);
                    this.mFlag = flag;
                }
                this.type.setText(s1);
                this.name.setText(s2);
                this.date.setText(s3);
                float SW = getWidth();
                this.type.setX((this.type.getWidth() - SW) / 2.0f);
                this.date.setX(((this.date.getWidth() - SW) / 2.0f) + ((float) fw));
                this.name.setX((((this.name.getWidth() - SW) / 2.0f) + this.type.getWidth()) + 40.0f);
                return;
            default:
                return;
        }
    }
}
