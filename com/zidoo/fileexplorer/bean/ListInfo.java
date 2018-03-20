package com.zidoo.fileexplorer.bean;

import android.content.Context;
import com.zidoo.fileexplorer.tool.SmbDatabaseUtils;
import com.zidoo.fileexplorer.tool.Utils;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import zidoo.nfs.NfsDevice;
import zidoo.samba.exs.SambaDevice;
import zidoo.samba.manager.SambaManager;

public class ListInfo {
    int checkNum;
    boolean[] checks = new boolean[0];
    File[] children = new File[0];
    Favorite[] favorites;
    HashMap<String, MountFile[]> mNfsShareDirs = new HashMap();
    HashMap<String, MountFile[]> mSmbShareDirs = new HashMap();
    ArrayList<NfsDevice> nfsDevices = new ArrayList();
    ArrayList<SambaDevice> savedSmbDevices = new ArrayList();
    ArrayList<SambaDevice> smbDevices = new ArrayList();
    boolean smbQueryed = false;

    public File[] getChildren() {
        return this.children;
    }

    public void setChildren(File[] children) {
        this.children = children;
    }

    public File getChild(int position) {
        return this.children[position];
    }

    public int childCount() {
        return this.children.length;
    }

    public boolean[] getChecks() {
        return this.checks;
    }

    public int getCheckNumber() {
        return this.checkNum;
    }

    public boolean isSavedSmb(int position) {
        return position < this.savedSmbDevices.size();
    }

    public SambaDevice getSmbDevice(int position) {
        return position < this.savedSmbDevices.size() ? (SambaDevice) this.savedSmbDevices.get(position) : (SambaDevice) this.smbDevices.get(position - this.savedSmbDevices.size());
    }

    public void check(int position) {
        if (this.checks[position]) {
            this.checks[position] = false;
            this.checkNum--;
            return;
        }
        this.checks[position] = true;
        this.checkNum++;
    }

    public boolean isSmbEmpty() {
        return this.savedSmbDevices.size() == 0 && this.smbDevices.size() == 0;
    }

    public void addSmb(SambaDevice device) {
        this.smbDevices.add(device);
    }

    public int smbSize(int tag) {
        if (tag == 0) {
            return this.savedSmbDevices.size() + this.smbDevices.size();
        }
        if (tag == 1) {
            return this.savedSmbDevices.size();
        }
        return this.smbDevices.size();
    }

    public void setSavedSmb(ArrayList<SambaDevice> devices) {
        Iterator it = this.savedSmbDevices.iterator();
        while (it.hasNext()) {
            SambaDevice smb = (SambaDevice) it.next();
            Iterator<SambaDevice> iterator = devices.iterator();
            while (iterator.hasNext()) {
                if (SambaManager.isSameDevice(smb, (SambaDevice) iterator.next())) {
                    iterator.remove();
                }
            }
        }
        this.savedSmbDevices.addAll(devices);
        this.smbQueryed = true;
    }

    public void fillCheck(boolean check) {
        if (check) {
            Arrays.fill(this.checks, true);
            this.checkNum = this.checks.length;
            return;
        }
        Arrays.fill(this.checks, false);
        this.checkNum = 0;
    }

    public void resetChecks(int length) {
        this.checks = new boolean[length];
        this.checkNum = 0;
    }

    public void check(int position, boolean check) {
        if (this.checks[position] != check) {
            check(position);
        }
    }

    public void resetSmb() {
        this.smbDevices.clear();
    }

    public ArrayList<SambaDevice> getSavedSmbDevices() {
        return this.savedSmbDevices;
    }

    public ArrayList<SambaDevice> getSmbDevices() {
        return this.smbDevices;
    }

    public SambaDevice removeSavedSmb(int index) {
        return (SambaDevice) this.savedSmbDevices.remove(index);
    }

    public void addSmb(int index, SambaDevice device) {
        this.smbDevices.add(0, device);
    }

    public boolean saveSmb(SambaDevice device) {
        Iterator it = this.savedSmbDevices.iterator();
        while (it.hasNext()) {
            if (((SambaDevice) it.next()).getUrl().equals(device.getUrl())) {
                return false;
            }
        }
        this.savedSmbDevices.add(device);
        if (device.getType() == 4) {
            Iterator<SambaDevice> iterator = this.smbDevices.iterator();
            while (iterator.hasNext()) {
                SambaDevice sambaDevice = (SambaDevice) iterator.next();
                if (sambaDevice.getType() == 4 && sambaDevice.getIp().equals(device.getIp())) {
                    iterator.remove();
                    break;
                }
            }
        }
        return true;
    }

    public SambaDevice getSavedSmbShare(String ip, String share) {
        Iterator it = this.savedSmbDevices.iterator();
        while (it.hasNext()) {
            SambaDevice device = (SambaDevice) it.next();
            if (device.getType() == 8 && device.getIp().equals(ip) && Utils.getSmbShare(device.getUrl()).equals(share)) {
                return device;
            }
        }
        return null;
    }

    public SambaDevice getSavedDevice(SambaDevice root, String name) {
        Iterator it = this.savedSmbDevices.iterator();
        while (it.hasNext()) {
            SambaDevice device = (SambaDevice) it.next();
            if (device.getType() == 8 && (device.getUrl().equals("smb://" + root.getIp() + "/" + name + "/") || device.getUrl().equals("smb://" + root.getHost() + "/" + name + "/"))) {
                return device;
            }
        }
        return null;
    }

    public SambaDevice getSavedSmbDevice(String url) {
        Iterator it = this.savedSmbDevices.iterator();
        while (it.hasNext()) {
            SambaDevice sambaDevice = (SambaDevice) it.next();
            if (sambaDevice.getUrl().equals(url)) {
                return sambaDevice;
            }
        }
        return null;
    }

    public NfsDevice getNfs(int index) {
        return (NfsDevice) this.nfsDevices.get(index);
    }

    public int nfsSize() {
        return this.nfsDevices.size();
    }

    public void addNfs(NfsDevice device) {
        Iterator it = this.nfsDevices.iterator();
        while (it.hasNext()) {
            if (((NfsDevice) it.next()).ip.equals(device.ip)) {
                return;
            }
        }
        this.nfsDevices.add(device);
    }

    public void resetNfs() {
        this.nfsDevices.clear();
    }

    public void removeSmb(int position) {
        if (position < this.savedSmbDevices.size()) {
            this.savedSmbDevices.remove(position);
        } else {
            this.smbDevices.remove(position - this.savedSmbDevices.size());
        }
    }

    public boolean isCheck(int position) {
        return this.checks[position];
    }

    public Favorite[] getFavorites() {
        return this.favorites;
    }

    public void setFavorite(Favorite[] newList) {
        this.favorites = newList;
    }

    public int favoriteCount() {
        return this.favorites.length;
    }

    public Favorite getFavorite(int index) {
        return this.favorites[index];
    }

    public Object[] getDeviceAndPosition(Context context, SambaDevice src) {
        checkSmbQuery(context);
        for (int i = 0; i < this.savedSmbDevices.size(); i++) {
            if (SambaManager.isSameDevice((SambaDevice) this.savedSmbDevices.get(i), src)) {
                return new Object[]{(SambaDevice) this.savedSmbDevices.get(i), Integer.valueOf(i), Boolean.valueOf(true)};
            }
        }
        Iterator<SambaDevice> iterator;
        SambaDevice device;
        if (src.getType() == 4) {
            iterator = this.smbDevices.iterator();
            while (iterator.hasNext()) {
                device = (SambaDevice) iterator.next();
                if (device.getType() != 4 || !device.getIp().equals(src.getIp())) {
                    if (device.getHost().equals(src.getHost())) {
                    }
                }
                this.savedSmbDevices.add(src);
                iterator.remove();
                return new Object[]{src, Integer.valueOf(this.savedSmbDevices.size() - 1), Boolean.valueOf(false)};
            }
        }
        String uri = Utils.getSmbUri(src.getUrl());
        iterator = this.smbDevices.iterator();
        while (iterator.hasNext()) {
            device = (SambaDevice) iterator.next();
            if (device.getType() == src.getType() && ((device.getIp().equals(src.getIp()) || device.getHost().equals(src.getHost())) && Utils.getSmbUri(device.getUrl()).equals(uri))) {
                this.savedSmbDevices.add(src);
                iterator.remove();
                return new Object[]{src, Integer.valueOf(this.savedSmbDevices.size() - 1), Boolean.valueOf(false)};
            }
        }
        this.savedSmbDevices.add(src);
        return new Object[]{src, Integer.valueOf(this.savedSmbDevices.size() - 1), Boolean.valueOf(false)};
    }

    public Object[] getSmbShareDeviceAndPosition(Context context, SambaDevice src) {
        checkSmbQuery(context);
        String uri = Utils.getSmbUri(src.getUrl());
        for (int i = 0; i < this.savedSmbDevices.size(); i++) {
            SambaDevice device = (SambaDevice) this.savedSmbDevices.get(i);
            if (device.getType() != 4 && device.getIp().equals(src.getIp()) && uri.startsWith(Utils.getSmbUri(device.getUrl())) && device.getUser().equals(src.getUser()) && device.getPassWord().equals(src.getPassWord())) {
                return new Object[]{device, Integer.valueOf(i), Boolean.valueOf(true)};
            }
        }
        Iterator<SambaDevice> iterator = this.smbDevices.iterator();
        while (iterator.hasNext()) {
            device = (SambaDevice) iterator.next();
            if (device.getType() != 4 && device.getIp().equals(src.getIp()) && uri.startsWith(Utils.getSmbUri(device.getUrl()))) {
                device.setUser(src.getUser());
                device.setPassWord(src.getPassWord());
                this.savedSmbDevices.add(device);
                iterator.remove();
                return new Object[]{device, Integer.valueOf(this.savedSmbDevices.size() - 1), Boolean.valueOf(false)};
            }
        }
        this.savedSmbDevices.add(src);
        return new Object[]{src, Integer.valueOf(this.savedSmbDevices.size() - 1), Boolean.valueOf(false)};
    }

    public int addToList(SambaDevice device) {
        this.savedSmbDevices.add(device);
        return this.savedSmbDevices.size() - 1;
    }

    public int addToList(String ip) {
        for (int i = 0; i < this.nfsDevices.size(); i++) {
            if (((NfsDevice) this.nfsDevices.get(i)).ip.equals(ip)) {
                return i;
            }
        }
        this.nfsDevices.add(new NfsDevice(ip));
        return this.nfsDevices.size() - 1;
    }

    public boolean isCheckAll() {
        return this.checkNum == this.checks.length;
    }

    public void addFavorite(Favorite favorite) {
        int len = this.favorites.length;
        Favorite[] temp = new Favorite[(len + 1)];
        System.arraycopy(this.favorites, 0, temp, 0, len);
        temp[len] = favorite;
        this.favorites = temp;
    }

    public HashMap<String, MountFile[]> getSmbShareDirs() {
        return this.mSmbShareDirs;
    }

    public HashMap<String, MountFile[]> getNfsShareDirs() {
        return this.mNfsShareDirs;
    }

    public void saveSmbList(ArrayList<SambaDevice> smbs) {
        this.savedSmbDevices = smbs;
    }

    private void checkSmbQuery(Context context) {
        if (!this.smbQueryed) {
            this.savedSmbDevices = SmbDatabaseUtils.selectByAll(context);
            this.smbQueryed = true;
        }
    }
}
