package com.zidoo.fileexplorer.task;

import android.content.Context;
import android.os.Handler;
import com.zidoo.fileexplorer.bean.Favorite;
import com.zidoo.fileexplorer.db.FavoriteDatabase;

public class RemoveFavoriteTask extends BaseTask<Boolean> {
    Context context;
    Favorite[] favorites;

    public RemoveFavoriteTask(Handler handler, int what, Context context, Favorite... favorites) {
        super(handler, what);
        this.context = context;
        this.favorites = favorites;
    }

    protected Boolean doInBackground() {
        return Boolean.valueOf(FavoriteDatabase.helper(this.context).delete(this.favorites) > 0);
    }
}
