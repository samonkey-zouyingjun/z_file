package zidoo.samba.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import jcifs.smb.SmbFile;
import zidoo.samba.exs.SambaDevice;

@SuppressLint({"NewApi"})
public class RockSmbManager extends SambaManager {
    private final String SHELL_HEAD = "#!/system/bin/sh";
    private final String SHELL_LOG_PATH = "/data/etc/log";
    private final String SHELL_PATH = "/data/etc/cifsmanager.sh";
    Method SystemProperties_get;
    Method SystemProperties_set;

    RockSmbManager(Context context) {
        super(context);
        initSystemProperties();
    }

    public String getSmbRoot() {
        return "/data/smb";
    }

    private void initSystemProperties() {
        try {
            Class<?> cSystemProperties = Class.forName("android.os.SystemProperties");
            this.SystemProperties_set = cSystemProperties.getDeclaredMethod("set", new Class[]{String.class, String.class});
            this.SystemProperties_get = cSystemProperties.getDeclaredMethod("get", new Class[]{String.class, String.class});
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e2) {
            e2.printStackTrace();
        }
    }

    public boolean mountSmbs(SmbFile[] smbFiles, SambaDevice device) {
        ArrayList<String> cmds = new ArrayList();
        cmds.add("#!/system/bin/sh");
        for (SmbFile smbFile : smbFiles) {
            String sharePath = "//" + device.getIp() + "/" + smbFile.getName();
            String mountPoint = getSmbRoot() + "/" + smbFile.getName();
            if (sharePath.endsWith("/")) {
                sharePath = sharePath.substring(0, sharePath.length() - 1);
            }
            if (mountPoint.endsWith("/")) {
                mountPoint = mountPoint.substring(0, mountPoint.length() - 1);
            }
            creatMountPoint(mountPoint);
            cmds.add("busybox mount -t cifs -o iocharset=utf8,username=" + device.getUser() + ",password=" + device.getPassWord() + ",uid=1000,gid=1015,file_mode=0775,dir_mode=0775,rw " + sharePath + " " + mountPoint + " > " + "/data/etc/log" + " 2>&1");
        }
        String[] cmd = new String[cmds.size()];
        cmds.toArray(cmd);
        if (!ShellFileWrite(cmd)) {
            return false;
        }
        try {
            this.SystemProperties_set.invoke(null, new Object[]{"ctl.start", "cifsmanager"});
            long startTime = System.currentTimeMillis();
            do {
                String mountResult = (String) this.SystemProperties_get.invoke(null, new Object[]{"init.svc.cifsmanager", ""});
                if (mountResult != null && mountResult.equals("stopped") && ShellLogRead()) {
                    return true;
                }
                Thread.sleep(20);
            } while (System.currentTimeMillis() - startTime < 7000);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        } catch (InterruptedException e4) {
            e4.printStackTrace();
        }
        return false;
    }

    public boolean unMountSmbs(File parent) {
        if (parent == null || parent.isFile()) {
            return false;
        }
        File[] files = parent.listFiles();
        ArrayList<String> cmds = new ArrayList();
        cmds.add("#!/system/bin/sh");
        for (File file : files) {
            if (!file.delete()) {
                String path = file.getPath();
                if (path.charAt(0) != '/') {
                    path = "/" + path;
                }
                cmds.add("busybox umount -fl " + path + " > " + "/data/etc/log" + " 2>&1");
            }
        }
        int size = cmds.size();
        if (size == 1) {
            return true;
        }
        String[] cmd = new String[size];
        cmds.toArray(cmd);
        if (!ShellFileWrite(cmd)) {
            return false;
        }
        try {
            long startTime = System.currentTimeMillis();
            this.SystemProperties_set.invoke(null, new Object[]{"ctl.start", "cifsmanager"});
            do {
                File[] leaveas = parent.listFiles();
                if (leaveas.length == 0) {
                    return true;
                }
                for (File file2 : leaveas) {
                    file2.delete();
                }
                Thread.sleep(20);
            } while (System.currentTimeMillis() - startTime < 5000);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        } catch (InterruptedException e4) {
            e4.printStackTrace();
        }
        if (parent.listFiles().length == 0) {
            return true;
        }
        return false;
    }

    public boolean isMounted(String... param) {
        return SambaManager.isMounted("//" + param[0] + "/" + param[1].replaceAll(" ", "\\\\040"), param[2]);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean mountSmb(java.lang.String r11, java.lang.String r12, java.lang.String r13, java.lang.String r14, java.lang.String r15) {
        /*
        r10 = this;
        r8 = r10.creatMountPoint(r12);
        if (r8 != 0) goto L_0x0008;
    L_0x0006:
        r8 = 0;
    L_0x0007:
        return r8;
    L_0x0008:
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r9 = "busybox mount -t cifs -o iocharset=utf8,username=";
        r8 = r8.append(r9);
        r8 = r8.append(r14);
        r9 = ",password=";
        r8 = r8.append(r9);
        r8 = r8.append(r15);
        r9 = ",uid=1000,gid=1015,file_mode=0775,dir_mode=0775,rw \"";
        r8 = r8.append(r9);
        r8 = r8.append(r11);
        r9 = "\" \"";
        r8 = r8.append(r9);
        r8 = r8.append(r12);
        r9 = "\"";
        r8 = r8.append(r9);
        r0 = r8.toString();
        zidoo.device.DeviceUtils.execute(r0);
        r8 = 47;
        r3 = r11.lastIndexOf(r8);	 Catch:{ IllegalArgumentException -> 0x0078 }
        r8 = -1;
        if (r3 != r8) goto L_0x006c;
    L_0x0050:
        r7 = r11;
    L_0x0051:
        r5 = 0;
        r6 = r5;
    L_0x0053:
        r8 = 50;
        java.lang.Thread.sleep(r8);	 Catch:{ InterruptedException -> 0x0073 }
    L_0x0058:
        r8 = 3;
        r8 = new java.lang.String[r8];	 Catch:{ IllegalArgumentException -> 0x0078 }
        r9 = 0;
        r8[r9] = r13;	 Catch:{ IllegalArgumentException -> 0x0078 }
        r9 = 1;
        r8[r9] = r7;	 Catch:{ IllegalArgumentException -> 0x0078 }
        r9 = 2;
        r8[r9] = r12;	 Catch:{ IllegalArgumentException -> 0x0078 }
        r8 = r10.isMounted(r8);	 Catch:{ IllegalArgumentException -> 0x0078 }
        if (r8 == 0) goto L_0x008d;
    L_0x006a:
        r8 = 1;
        goto L_0x0007;
    L_0x006c:
        r8 = r3 + 1;
        r7 = r11.substring(r8);	 Catch:{ IllegalArgumentException -> 0x0078 }
        goto L_0x0051;
    L_0x0073:
        r2 = move-exception;
        r2.printStackTrace();	 Catch:{ IllegalArgumentException -> 0x0078 }
        goto L_0x0058;
    L_0x0078:
        r1 = move-exception;
        r1.printStackTrace();
    L_0x007c:
        r4 = new java.io.File;
        r4.<init>(r12);
        r8 = r4.exists();
        if (r8 == 0) goto L_0x008a;
    L_0x0087:
        r4.delete();
    L_0x008a:
        r8 = 0;
        goto L_0x0007;
    L_0x008d:
        r5 = r6 + 1;
        r8 = 20;
        if (r6 >= r8) goto L_0x007c;
    L_0x0093:
        r6 = r5;
        goto L_0x0053;
        */
        throw new UnsupportedOperationException("Method not decompiled: zidoo.samba.manager.RockSmbManager.mountSmb(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String):boolean");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean unMountSmb(java.io.File r19) {
        /*
        r18 = this;
        r11 = r19.exists();
        if (r11 != 0) goto L_0x0008;
    L_0x0006:
        r4 = 0;
    L_0x0007:
        return r4;
    L_0x0008:
        r10 = r19.getPath();
        r11 = 0;
        r11 = r10.charAt(r11);
        r14 = 47;
        if (r11 == r14) goto L_0x0029;
    L_0x0015:
        r11 = new java.lang.StringBuilder;
        r11.<init>();
        r14 = "/";
        r11 = r11.append(r14);
        r11 = r11.append(r10);
        r10 = r11.toString();
    L_0x0029:
        r11 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0068 }
        r11.<init>();	 Catch:{ Exception -> 0x0068 }
        r14 = "busybox umount -fl ";
        r11 = r11.append(r14);	 Catch:{ Exception -> 0x0068 }
        r11 = r11.append(r10);	 Catch:{ Exception -> 0x0068 }
        r14 = " > ";
        r11 = r11.append(r14);	 Catch:{ Exception -> 0x0068 }
        r14 = "/data/etc/log";
        r11 = r11.append(r14);	 Catch:{ Exception -> 0x0068 }
        r14 = " 2>&1";
        r11 = r11.append(r14);	 Catch:{ Exception -> 0x0068 }
        r3 = r11.toString();	 Catch:{ Exception -> 0x0068 }
        r11 = 2;
        r2 = new java.lang.String[r11];	 Catch:{ Exception -> 0x0068 }
        r11 = 0;
        r14 = "#!/system/bin/sh";
        r2[r11] = r14;	 Catch:{ Exception -> 0x0068 }
        r11 = 1;
        r2[r11] = r3;	 Catch:{ Exception -> 0x0068 }
        r0 = r18;
        r11 = r0.ShellFileWrite(r2);	 Catch:{ Exception -> 0x0068 }
        if (r11 != 0) goto L_0x006b;
    L_0x0066:
        r4 = 0;
        goto L_0x0007;
    L_0x0068:
        r6 = move-exception;
        r4 = 0;
        goto L_0x0007;
    L_0x006b:
        r0 = r18;
        r11 = r0.SystemProperties_set;	 Catch:{ IllegalArgumentException -> 0x00e4, IllegalAccessException -> 0x00e9, InvocationTargetException -> 0x00ee, InterruptedException -> 0x00f3 }
        r14 = 0;
        r15 = 2;
        r15 = new java.lang.Object[r15];	 Catch:{ IllegalArgumentException -> 0x00e4, IllegalAccessException -> 0x00e9, InvocationTargetException -> 0x00ee, InterruptedException -> 0x00f3 }
        r16 = 0;
        r17 = "ctl.start";
        r15[r16] = r17;	 Catch:{ IllegalArgumentException -> 0x00e4, IllegalAccessException -> 0x00e9, InvocationTargetException -> 0x00ee, InterruptedException -> 0x00f3 }
        r16 = 1;
        r17 = "cifsmanager";
        r15[r16] = r17;	 Catch:{ IllegalArgumentException -> 0x00e4, IllegalAccessException -> 0x00e9, InvocationTargetException -> 0x00ee, InterruptedException -> 0x00f3 }
        r11.invoke(r14, r15);	 Catch:{ IllegalArgumentException -> 0x00e4, IllegalAccessException -> 0x00e9, InvocationTargetException -> 0x00ee, InterruptedException -> 0x00f3 }
        r12 = java.lang.System.currentTimeMillis();	 Catch:{ IllegalArgumentException -> 0x00e4, IllegalAccessException -> 0x00e9, InvocationTargetException -> 0x00ee, InterruptedException -> 0x00f3 }
    L_0x0088:
        r0 = r18;
        r11 = r0.SystemProperties_get;	 Catch:{ IllegalArgumentException -> 0x00e4, IllegalAccessException -> 0x00e9, InvocationTargetException -> 0x00ee, InterruptedException -> 0x00f3 }
        r14 = 0;
        r15 = 2;
        r15 = new java.lang.Object[r15];	 Catch:{ IllegalArgumentException -> 0x00e4, IllegalAccessException -> 0x00e9, InvocationTargetException -> 0x00ee, InterruptedException -> 0x00f3 }
        r16 = 0;
        r17 = "init.svc.cifsmanager";
        r15[r16] = r17;	 Catch:{ IllegalArgumentException -> 0x00e4, IllegalAccessException -> 0x00e9, InvocationTargetException -> 0x00ee, InterruptedException -> 0x00f3 }
        r16 = 1;
        r17 = "";
        r15[r16] = r17;	 Catch:{ IllegalArgumentException -> 0x00e4, IllegalAccessException -> 0x00e9, InvocationTargetException -> 0x00ee, InterruptedException -> 0x00f3 }
        r7 = r11.invoke(r14, r15);	 Catch:{ IllegalArgumentException -> 0x00e4, IllegalAccessException -> 0x00e9, InvocationTargetException -> 0x00ee, InterruptedException -> 0x00f3 }
        r7 = (java.lang.String) r7;	 Catch:{ IllegalArgumentException -> 0x00e4, IllegalAccessException -> 0x00e9, InvocationTargetException -> 0x00ee, InterruptedException -> 0x00f3 }
        if (r7 == 0) goto L_0x00ce;
    L_0x00a6:
        r11 = "stopped";
        r11 = r7.equals(r11);	 Catch:{ IllegalArgumentException -> 0x00e4, IllegalAccessException -> 0x00e9, InvocationTargetException -> 0x00ee, InterruptedException -> 0x00f3 }
        if (r11 == 0) goto L_0x00ce;
    L_0x00af:
        r11 = r18.ShellLogRead();	 Catch:{ IllegalArgumentException -> 0x00e4, IllegalAccessException -> 0x00e9, InvocationTargetException -> 0x00ee, InterruptedException -> 0x00f3 }
        if (r11 == 0) goto L_0x00ce;
    L_0x00b5:
        r4 = r19.delete();	 Catch:{ IllegalArgumentException -> 0x00e4, IllegalAccessException -> 0x00e9, InvocationTargetException -> 0x00ee, InterruptedException -> 0x00f3 }
        r8 = 0;
        r9 = r8;
    L_0x00bb:
        if (r4 != 0) goto L_0x00fa;
    L_0x00bd:
        r8 = r9 + 1;
        r11 = 20;
        if (r9 >= r11) goto L_0x0007;
    L_0x00c3:
        r14 = 50;
        java.lang.Thread.sleep(r14);	 Catch:{ InterruptedException -> 0x00f8, IllegalArgumentException -> 0x00e4, IllegalAccessException -> 0x00e9, InvocationTargetException -> 0x00ee }
    L_0x00c8:
        r4 = r19.delete();	 Catch:{ IllegalArgumentException -> 0x00e4, IllegalAccessException -> 0x00e9, InvocationTargetException -> 0x00ee, InterruptedException -> 0x00f3 }
        r9 = r8;
        goto L_0x00bb;
    L_0x00ce:
        r14 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        java.lang.Thread.sleep(r14);	 Catch:{ IllegalArgumentException -> 0x00e4, IllegalAccessException -> 0x00e9, InvocationTargetException -> 0x00ee, InterruptedException -> 0x00f3 }
        r14 = java.lang.System.currentTimeMillis();	 Catch:{ IllegalArgumentException -> 0x00e4, IllegalAccessException -> 0x00e9, InvocationTargetException -> 0x00ee, InterruptedException -> 0x00f3 }
        r14 = r14 - r12;
        r16 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r11 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1));
        if (r11 < 0) goto L_0x0088;
    L_0x00de:
        r4 = r19.delete();
        goto L_0x0007;
    L_0x00e4:
        r5 = move-exception;
        r5.printStackTrace();
        goto L_0x00de;
    L_0x00e9:
        r5 = move-exception;
        r5.printStackTrace();
        goto L_0x00de;
    L_0x00ee:
        r5 = move-exception;
        r5.printStackTrace();
        goto L_0x00de;
    L_0x00f3:
        r5 = move-exception;
        r5.printStackTrace();
        goto L_0x00de;
    L_0x00f8:
        r11 = move-exception;
        goto L_0x00c8;
    L_0x00fa:
        r8 = r9;
        goto L_0x0007;
        */
        throw new UnsupportedOperationException("Method not decompiled: zidoo.samba.manager.RockSmbManager.unMountSmb(java.io.File):boolean");
    }

    private boolean ShellFileWrite(String[] cmd) {
        File shell = new File("/data/etc/cifsmanager.sh");
        if (!shell.exists()) {
            try {
                shell.createNewFile();
                shell.setExecutable(true, false);
                shell.setReadable(true, false);
                shell.setWritable(true, false);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            BufferedWriter buffwr = new BufferedWriter(new FileWriter(shell));
            for (String str : cmd) {
                buffwr.write(str);
                buffwr.newLine();
                buffwr.flush();
            }
            buffwr.close();
            return true;
        } catch (IOException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    private boolean creatMountPoint(String path) {
        File mountPoint = new File(path);
        if (mountPoint.exists()) {
            return true;
        }
        return mountPoint.mkdirs();
    }

    private boolean ShellLogRead() {
        boolean result = true;
        File shellLog = new File("/data/etc/log");
        if (!shellLog.exists()) {
            try {
                shellLog.createNewFile();
                shellLog.setReadable(true, false);
                shellLog.setWritable(true, false);
                shellLog.setExecutable(true, false);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            BufferedReader buffrd = new BufferedReader(new FileReader(shellLog));
            StringBuffer sb = new StringBuffer();
            while (true) {
                String temp = buffrd.readLine();
                if (temp == null) {
                    break;
                }
                sb.append(temp);
                result = false;
            }
            if (!result) {
                Log.e("RockSmbManager", "shell log read error:" + sb.toString());
            }
            buffrd.close();
            return result;
        } catch (IOException e2) {
            e2.printStackTrace();
            return false;
        }
    }
}
