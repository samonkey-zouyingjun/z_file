package com.zidoo.fileexplorer.task;

import android.content.Context;
import android.os.Handler;
import com.zidoo.fileexplorer.bean.Favorite;
import com.zidoo.fileexplorer.bean.TaskParam.CheckedFavorites;
import com.zidoo.fileexplorer.db.FavoriteDatabase;

public class RenameFavoriteTask extends BaseTask<Boolean> {
    CheckedFavorites checkedFavorites;
    Context context;

    public RenameFavoriteTask(Handler handler, int what, Context context, CheckedFavorites checkedFavorites) {
        super(handler, what);
        this.context = context;
        this.checkedFavorites = checkedFavorites;
    }

    protected Boolean doInBackground() {
        boolean result = false;
        Favorite favorite = this.checkedFavorites.getFavorite();
        String name = this.checkedFavorites.getNewName();
        if (FavoriteDatabase.helper(this.context).update(favorite.getId(), name) > 0) {
            favorite.setName(name);
            result = true;
        }
        return Boolean.valueOf(result);
    }
}
