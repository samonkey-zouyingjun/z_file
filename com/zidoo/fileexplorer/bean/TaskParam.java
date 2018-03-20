package com.zidoo.fileexplorer.bean;

import java.util.ArrayList;

public class TaskParam {

    public static final class CheckedFavorites {
        Favorite favorite;
        String name;
        int position;

        public CheckedFavorites(int position, Favorite favorite, String name) {
            this.position = position;
            this.favorite = favorite;
            this.name = name;
        }

        public int getPosition() {
            return this.position;
        }

        public Favorite getFavorite() {
            return this.favorite;
        }

        public String getNewName() {
            return this.name;
        }
    }

    public static final class Data {
        ArrayList<DeviceInfo> devices;
        Favorite[] favorites;

        public Data(ArrayList<DeviceInfo> devices, Favorite[] favorites) {
            this.devices = devices;
            this.favorites = favorites;
        }

        public ArrayList<DeviceInfo> getDevices() {
            return this.devices;
        }

        public Favorite[] getFavorites() {
            return this.favorites;
        }
    }
}
