package com.zidoo.fileexplorer.task;

import android.os.Handler;
import com.zidoo.fileexplorer.bean.BrowseInfo;
import com.zidoo.fileexplorer.bean.Favorite;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.tool.CompareTool;
import com.zidoo.fileexplorer.tool.Utils;
import java.util.ArrayList;

public class QueryFavoriteTask extends BaseTask<Favorite[]> {
    BrowseInfo browser;
    Favorite[] favorites;
    int screen;

    public QueryFavoriteTask(Handler handler, int what, Favorite[] favorites, BrowseInfo browser, int screen) {
        super(handler, what);
        this.favorites = favorites;
        this.browser = browser;
        this.screen = screen;
    }

    protected Favorite[] doInBackground() {
        this.favorites = filterFavorite(this.screen, this.favorites);
        sortFavorite(this.favorites, AppConstant.sPrefereancesSortWay);
        return this.favorites;
    }

    private Favorite[] filterFavorite(int screen, Favorite[] favorites) {
        int i = 0;
        ArrayList<Favorite> list = new ArrayList();
        Favorite favorite;
        int type;
        if (this.browser != null) {
            for (Favorite favorite2 : favorites) {
                boolean receive = false;
                switch (favorite2.getTag()) {
                    case 0:
                        if ((this.browser.getDeviceTag() & 2) != 0) {
                            receive = true;
                        } else {
                            receive = false;
                        }
                        break;
                    case 1:
                        if ((this.browser.getDeviceTag() & 4) != 0) {
                            receive = true;
                        } else {
                            receive = false;
                        }
                        break;
                    case 2:
                    case 3:
                    case 6:
                        if ((this.browser.getDeviceTag() & 8) != 0) {
                            receive = true;
                        } else {
                            receive = false;
                        }
                        break;
                    case 4:
                    case 5:
                        if ((this.browser.getDeviceTag() & 16) != 0) {
                            receive = true;
                        } else {
                            receive = false;
                        }
                        break;
                }
                if (receive) {
                    int filter = this.browser.getFilter() & -2097153;
                    if (favorite2.getFileType() == -1 || favorite2.getFileType() == 0 || filter == 0) {
                        list.add(favorite2);
                    } else {
                        type = favorite2.getFileType();
                        if ((this.browser.getFilter() & 1048576) != 0) {
                            type = Utils.getFavoriteType(favorite2);
                        }
                        if (((1 << type) & filter) != 0) {
                            list.add(favorite2);
                        }
                    }
                }
            }
        } else {
            type = 0;
            switch (screen) {
                case 1:
                    type = 2;
                    break;
                case 2:
                    type = 1;
                    break;
                case 3:
                    type = 3;
                    break;
                case 4:
                    type = 5;
                    break;
            }
            if (type == 0) {
                return favorites;
            }
            int length = favorites.length;
            while (i < length) {
                favorite2 = favorites[i];
                if (favorite2.getFileType() <= 0 || favorite2.getFileType() == type) {
                    list.add(favorite2);
                }
                i++;
            }
        }
        Favorite[] newArray = new Favorite[list.size()];
        list.toArray(newArray);
        return newArray;
    }

    private void sortFavorite(Favorite[] favorites, int sortWay) {
        switch (sortWay) {
            case 0:
                CompareTool.sortByName(favorites, true);
                return;
            case 1:
                CompareTool.sortBySize(favorites, true);
                return;
            case 2:
                CompareTool.sortByDate(favorites, true);
                return;
            case 3:
                CompareTool.sortByType(favorites, true);
                return;
            case 4:
                CompareTool.sortByName(favorites, false);
                return;
            case 5:
                CompareTool.sortBySize(favorites, false);
                return;
            case 6:
                CompareTool.sortByDate(favorites, false);
                return;
            case 7:
                CompareTool.sortByType(favorites, false);
                return;
            default:
                return;
        }
    }
}
