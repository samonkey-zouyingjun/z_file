package com.zidoo.fileexplorer.view;

import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.bean.DeviceInfo;
import com.zidoo.fileexplorer.tool.ViewSelectable;
import java.util.ArrayList;
import java.util.Iterator;
import zidoo.device.DeviceType;
import zidoo.tarot.GLContext;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.kernel.anim.GlAlphaAnimation;
import zidoo.tarot.kernel.anim.GlSetAnimation;
import zidoo.tarot.widget.ViewGroup;

public class DeviceFlickerLayout extends ViewGroup {
    ArrayList<DeviceState> devices = new ArrayList();
    private final int dimen = 50;
    private final int space = 10;

    private class DeviceState {
        ViewSelectable selectable;
        DeviceType type;

        DeviceState(ViewSelectable selectable, DeviceType type) {
            this.selectable = selectable;
            this.type = type;
        }
    }

    public /* bridge */ /* synthetic */ Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public DeviceFlickerLayout(GLContext glContext) {
        super(glContext);
    }

    public void refresh(ArrayList<DeviceInfo> deviceInfos) {
        removeAll();
        this.devices.clear();
        int number = 0;
        NumberDeviceView nd = null;
        Iterator it = deviceInfos.iterator();
        while (it.hasNext()) {
            DeviceType type = ((DeviceInfo) it.next()).getType();
            switch (type) {
                case SPECIAL_A:
                    StateImageView fsiv = new StateImageView(getContext());
                    fsiv.setWidth(50.0f);
                    fsiv.setHeight(50.0f);
                    fsiv.setImageResource(R.drawable.ic_device_favorite_small, R.drawable.ic_device_favorite_small_s);
                    this.devices.add(new DeviceState(fsiv, type));
                    break;
                case FLASH:
                    StateImageView flsiv = new StateImageView(getContext());
                    flsiv.setWidth(50.0f);
                    flsiv.setHeight(50.0f);
                    flsiv.setImageResource(R.drawable.ic_device_flash_small, R.drawable.ic_device_flash_small_s);
                    this.devices.add(new DeviceState(flsiv, type));
                    break;
                case TF:
                case SD:
                case HDD:
                    if (nd == null) {
                        nd = new NumberDeviceView(getContext());
                        nd.setWidth(50.0f);
                        nd.setHeight(50.0f);
                        this.devices.add(new DeviceState(nd, DeviceType.SD));
                    }
                    number++;
                    break;
                case SMB:
                    StateImageView slsiv = new StateImageView(getContext());
                    slsiv.setWidth(50.0f);
                    slsiv.setHeight(50.0f);
                    slsiv.setImageResource(R.drawable.ic_device_smb_small, R.drawable.ic_device_smb_small_s);
                    this.devices.add(new DeviceState(slsiv, type));
                    break;
                case NFS:
                    StateImageView nlsiv = new StateImageView(getContext());
                    nlsiv.setWidth(50.0f);
                    nlsiv.setHeight(50.0f);
                    nlsiv.setImageResource(R.drawable.ic_device_nfs_small, R.drawable.ic_device_nfs_small_s);
                    this.devices.add(new DeviceState(nlsiv, type));
                    break;
                default:
                    break;
            }
        }
        if (nd != null) {
            nd.setNumber(number);
        }
        int size = this.devices.size();
        if (size > 0) {
            int height = (size * 50) + ((size - 1) * 10);
            for (int i = 0; i < size; i++) {
                GameObject view = ((DeviceState) this.devices.get(i)).selectable;
                view.setY((float) (((height / 2) - 25) - (i * 60)));
                addGameObject(view);
            }
        }
    }

    public void selectType(DeviceType type) {
        if (type == DeviceType.TF || type == DeviceType.HDD) {
            type = DeviceType.SD;
        }
        Iterator it = this.devices.iterator();
        while (it.hasNext()) {
            DeviceState state = (DeviceState) it.next();
            state.selectable.setSelected(state.type == type);
        }
        invalidate();
    }

    public void flickerUsb() {
        DeviceState usb = null;
        Iterator it = this.devices.iterator();
        while (it.hasNext()) {
            DeviceState state = (DeviceState) it.next();
            if (state.type == DeviceType.SD) {
                usb = state;
                break;
            }
        }
        if (usb != null) {
            GameObject v = usb.selectable;
            GlSetAnimation set = new GlSetAnimation();
            boolean b = true;
            for (int i = 0; i < 7; i++) {
                GlAlphaAnimation gaa = b ? new GlAlphaAnimation(1.0f, 0.2f) : new GlAlphaAnimation(0.2f, 1.0f);
                gaa.setFillAfter(true);
                gaa.setDuration(100);
                gaa.setStartDelay((long) (i * 100));
                set.addAnimation(gaa);
                if (b) {
                    b = false;
                } else {
                    b = true;
                }
            }
            v.startAnimation(set);
        }
    }
}
