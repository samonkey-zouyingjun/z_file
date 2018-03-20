package com.zidoo.fileexplorer.browse;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.bean.BrowseInfo;
import com.zidoo.fileexplorer.browse.BrowseItem.BrowseResult;
import com.zidoo.fileexplorer.browse.BrowseItem.FileItem;
import com.zidoo.fileexplorer.browse.BrowseItem.RootItem;
import com.zidoo.fileexplorer.browse.BrowseItem.SmbServerItem;
import com.zidoo.fileexplorer.browse.BrowseItem.SmbShareItem;
import com.zidoo.fileexplorer.tool.FileOperater;
import com.zidoo.fileexplorer.tool.Utils;
import java.io.FileFilter;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import zidoo.browse.BrowseConstant;
import zidoo.file.FileType;

public class BrowseActivity extends Activity implements OnClickListener, OnFileListener {
    private final int BACK = 302;
    private final int ENTER = 301;
    private final Runnable SHOW_LOAD_PROGRESS = new Runnable() {
        public void run() {
            if (BrowseActivity.this.mLoadDialog == null) {
                BrowseActivity.this.mLoadDialog = new Dialog(BrowseActivity.this, R.style.defaultDialog);
                BrowseActivity.this.mLoadDialog.setContentView(R.layout.dialog_browse_load);
            }
            BrowseActivity.this.mLoadDialog.show();
        }
    };
    private BrowseAdapter mAdapter = new BrowseAdapter();
    private BrowseInfo mBrowser;
    private Button mCancelBtn = null;
    private FileFilter mFileFilter;
    private RecyclerView mFileList;
    private Handler mHandler = new Handler();
    private boolean mIsLoading = false;
    private Dialog mLoadDialog = null;
    private FileOpenThread mLoadThread = null;
    private TextView mPathText;
    private Stack<BrowseItem> mPaths = new Stack();
    private Button mSelectBtn;

    private class BrowseThread extends Thread {
        private Context context;
        private BrowseItem item;

        BrowseThread(Context context, BrowseItem item) {
            this.context = context;
            this.item = item;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r41 = this;
            r14 = 0;
            r21 = "";
            r16 = 0;
            r0 = r41;
            r0 = com.zidoo.fileexplorer.browse.BrowseActivity.this;	 Catch:{ Exception -> 0x0481 }
            r37 = r0;
            r37 = r37.mPaths;	 Catch:{ Exception -> 0x0481 }
            r38 = 1;
            r6 = r37.get(r38);	 Catch:{ Exception -> 0x0481 }
            r6 = (com.zidoo.fileexplorer.browse.BrowseItem) r6;	 Catch:{ Exception -> 0x0481 }
            r37 = com.zidoo.fileexplorer.browse.BrowseActivity.AnonymousClass4.$SwitchMap$com$zidoo$fileexplorer$browse$BrowseItem$Type;	 Catch:{ Exception -> 0x0481 }
            r38 = r6.getType();	 Catch:{ Exception -> 0x0481 }
            r38 = r38.ordinal();	 Catch:{ Exception -> 0x0481 }
            r37 = r37[r38];	 Catch:{ Exception -> 0x0481 }
            switch(r37) {
                case 1: goto L_0x00a9;
                case 2: goto L_0x00e7;
                case 3: goto L_0x0150;
                case 4: goto L_0x036d;
                case 5: goto L_0x0027;
                case 6: goto L_0x0027;
                case 7: goto L_0x0027;
                case 8: goto L_0x0150;
                case 9: goto L_0x036d;
                default: goto L_0x0027;
            };	 Catch:{ Exception -> 0x0481 }
        L_0x0027:
            if (r14 == 0) goto L_0x00a8;
        L_0x0029:
            r5 = new android.content.Intent;	 Catch:{ Exception -> 0x0481 }
            r5.<init>();	 Catch:{ Exception -> 0x0481 }
            r0 = r41;
            r0 = com.zidoo.fileexplorer.browse.BrowseActivity.this;	 Catch:{ Exception -> 0x0481 }
            r37 = r0;
            r37 = r37.mBrowser;	 Catch:{ Exception -> 0x0481 }
            r37 = r37.getFlag();	 Catch:{ Exception -> 0x0481 }
            if (r37 != 0) goto L_0x0487;
        L_0x003e:
            r37 = r14.getType();	 Catch:{ Exception -> 0x0481 }
            if (r37 == 0) goto L_0x0050;
        L_0x0044:
            r37 = r14.getType();	 Catch:{ Exception -> 0x0481 }
            r38 = 1;
            r0 = r37;
            r1 = r38;
            if (r0 != r1) goto L_0x0439;
        L_0x0050:
            r37 = "path";
            r38 = r14.getExtra();	 Catch:{ Exception -> 0x0481 }
            r0 = r37;
            r1 = r38;
            r5.putExtra(r0, r1);	 Catch:{ Exception -> 0x0481 }
        L_0x005e:
            r37 = "help";
            r38 = 0;
            r0 = r37;
            r1 = r38;
            r5.putExtra(r0, r1);	 Catch:{ Exception -> 0x0481 }
            r37 = "result";
            r38 = 1;
            r0 = r37;
            r1 = r38;
            r5.putExtra(r0, r1);	 Catch:{ Exception -> 0x0481 }
            r37 = "path";
            r0 = r37;
            r1 = r21;
            r5.putExtra(r0, r1);	 Catch:{ Exception -> 0x0481 }
            if (r16 != 0) goto L_0x0086;
        L_0x0082:
            r16 = zidoo.browse.BrowseUtils.identifierToUrl(r14);	 Catch:{ Exception -> 0x0481 }
        L_0x0086:
            r37 = "url";
            r0 = r37;
            r1 = r16;
            r5.putExtra(r0, r1);	 Catch:{ Exception -> 0x0481 }
            r0 = r41;
            r0 = com.zidoo.fileexplorer.browse.BrowseActivity.this;	 Catch:{ Exception -> 0x0481 }
            r37 = r0;
            r38 = -1;
            r0 = r37;
            r1 = r38;
            r0.setResult(r1, r5);	 Catch:{ Exception -> 0x0481 }
            r0 = r41;
            r0 = com.zidoo.fileexplorer.browse.BrowseActivity.this;	 Catch:{ Exception -> 0x0481 }
            r37 = r0;
            r37.finish();	 Catch:{ Exception -> 0x0481 }
        L_0x00a8:
            return;
        L_0x00a9:
            r31 = 0;
            r36 = "Flash";
            r0 = r41;
            r0 = r0.item;	 Catch:{ Exception -> 0x0481 }
            r37 = r0;
            r37 = (com.zidoo.fileexplorer.browse.BrowseItem.FileItem) r37;	 Catch:{ Exception -> 0x0481 }
            r37 = r37.getFile();	 Catch:{ Exception -> 0x0481 }
            r21 = r37.getPath();	 Catch:{ Exception -> 0x0481 }
            r6 = (com.zidoo.fileexplorer.browse.BrowseItem.FileItem) r6;	 Catch:{ Exception -> 0x0481 }
            r37 = r6.getFile();	 Catch:{ Exception -> 0x0481 }
            r7 = r37.getPath();	 Catch:{ Exception -> 0x0481 }
            r37 = r7.length();	 Catch:{ Exception -> 0x0481 }
            r0 = r21;
            r1 = r37;
            r32 = r0.substring(r1);	 Catch:{ Exception -> 0x0481 }
            r15 = new zidoo.browse.FileIdentifier;	 Catch:{ Exception -> 0x0481 }
            r0 = r31;
            r1 = r32;
            r2 = r36;
            r15.<init>(r0, r1, r2);	 Catch:{ Exception -> 0x0481 }
            r0 = r21;
            r15.setExtra(r0);	 Catch:{ Exception -> 0x0491 }
            r14 = r15;
            goto L_0x0027;
        L_0x00e7:
            r31 = 1;
            r0 = r6;
            r0 = (com.zidoo.fileexplorer.browse.BrowseItem.UsbItem) r0;	 Catch:{ Exception -> 0x0481 }
            r34 = r0;
            r36 = r34.getUuid();	 Catch:{ Exception -> 0x0481 }
            r0 = r41;
            r0 = r0.item;	 Catch:{ Exception -> 0x0481 }
            r37 = r0;
            r37 = (com.zidoo.fileexplorer.browse.BrowseItem.FileItem) r37;	 Catch:{ Exception -> 0x0481 }
            r37 = r37.getFile();	 Catch:{ Exception -> 0x0481 }
            r21 = r37.getPath();	 Catch:{ Exception -> 0x0481 }
            r37 = r34.getFile();	 Catch:{ Exception -> 0x0481 }
            r7 = r37.getPath();	 Catch:{ Exception -> 0x0481 }
            r15 = new zidoo.browse.FileIdentifier;	 Catch:{ Exception -> 0x0481 }
            r37 = r7.length();	 Catch:{ Exception -> 0x0481 }
            r0 = r21;
            r1 = r37;
            r37 = r0.substring(r1);	 Catch:{ Exception -> 0x0481 }
            r0 = r31;
            r1 = r37;
            r2 = r36;
            r15.<init>(r0, r1, r2);	 Catch:{ Exception -> 0x0481 }
            r0 = r21;
            r15.setExtra(r0);	 Catch:{ Exception -> 0x0491 }
            r37 = "usb://%s%s?label=%s";
            r38 = 3;
            r0 = r38;
            r0 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x0491 }
            r38 = r0;
            r39 = 0;
            r40 = r15.getUuid();	 Catch:{ Exception -> 0x0491 }
            r38[r39] = r40;	 Catch:{ Exception -> 0x0491 }
            r39 = 1;
            r40 = r15.getUri();	 Catch:{ Exception -> 0x0491 }
            r38[r39] = r40;	 Catch:{ Exception -> 0x0491 }
            r39 = 2;
            r40 = r34.getName();	 Catch:{ Exception -> 0x0491 }
            r38[r39] = r40;	 Catch:{ Exception -> 0x0491 }
            r16 = java.lang.String.format(r37, r38);	 Catch:{ Exception -> 0x0491 }
            r14 = r15;
            goto L_0x0027;
        L_0x0150:
            r0 = r41;
            r0 = r0.item;	 Catch:{ Exception -> 0x0481 }
            r37 = r0;
            r37 = r37.getType();	 Catch:{ Exception -> 0x0481 }
            r38 = com.zidoo.fileexplorer.browse.BrowseItem.Type.SMB_SHARE;	 Catch:{ Exception -> 0x0481 }
            r0 = r37;
            r1 = r38;
            if (r0 != r1) goto L_0x01fd;
        L_0x0162:
            r0 = r41;
            r0 = r0.item;	 Catch:{ Exception -> 0x0481 }
            r26 = r0;
            r26 = (com.zidoo.fileexplorer.browse.BrowseItem.SmbShareItem) r26;	 Catch:{ Exception -> 0x0481 }
            r28 = r26.getSmbShare();	 Catch:{ Exception -> 0x0481 }
            r37 = r28.getURL();	 Catch:{ Exception -> 0x0481 }
            r33 = r37.toString();	 Catch:{ Exception -> 0x0481 }
            r15 = new zidoo.browse.FileIdentifier;	 Catch:{ Exception -> 0x0481 }
            r37 = 2;
            r38 = r26.getIp();	 Catch:{ Exception -> 0x0481 }
            r0 = r37;
            r1 = r33;
            r2 = r38;
            r15.<init>(r0, r1, r2);	 Catch:{ Exception -> 0x0481 }
            r37 = r26.getUser();	 Catch:{ Exception -> 0x0491 }
            r0 = r37;
            r15.setUser(r0);	 Catch:{ Exception -> 0x0491 }
            r37 = r26.getPassword();	 Catch:{ Exception -> 0x0491 }
            r0 = r37;
            r15.setPassword(r0);	 Catch:{ Exception -> 0x0491 }
            r37 = r28.getServer();	 Catch:{ Exception -> 0x0491 }
            r0 = r37;
            r15.setExtra(r0);	 Catch:{ Exception -> 0x0491 }
            r0 = r41;
            r0 = r0.context;	 Catch:{ Exception -> 0x0491 }
            r37 = r0;
            r0 = r41;
            r0 = r0.context;	 Catch:{ Exception -> 0x0491 }
            r38 = r0;
            r38 = zidoo.model.BoxModel.getModelCode(r38);	 Catch:{ Exception -> 0x0491 }
            r23 = zidoo.samba.manager.SambaManager.getManager(r37, r38);	 Catch:{ Exception -> 0x0491 }
            r24 = r28.getShare();	 Catch:{ Exception -> 0x0491 }
            r37 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0491 }
            r37.<init>();	 Catch:{ Exception -> 0x0491 }
            r38 = r26.getIp();	 Catch:{ Exception -> 0x0491 }
            r37 = r37.append(r38);	 Catch:{ Exception -> 0x0491 }
            r38 = "#";
            r37 = r37.append(r38);	 Catch:{ Exception -> 0x0491 }
            r38 = zidoo.tool.ZidooFileUtils.encodeCommand(r24);	 Catch:{ Exception -> 0x0491 }
            r37 = r37.append(r38);	 Catch:{ Exception -> 0x0491 }
            r18 = r37.toString();	 Catch:{ Exception -> 0x0491 }
            r37 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0491 }
            r37.<init>();	 Catch:{ Exception -> 0x0491 }
            r38 = r23.getSmbRoot();	 Catch:{ Exception -> 0x0491 }
            r37 = r37.append(r38);	 Catch:{ Exception -> 0x0491 }
            r38 = "/";
            r37 = r37.append(r38);	 Catch:{ Exception -> 0x0491 }
            r0 = r37;
            r1 = r18;
            r37 = r0.append(r1);	 Catch:{ Exception -> 0x0491 }
            r21 = r37.toString();	 Catch:{ Exception -> 0x0491 }
            r14 = r15;
            goto L_0x0027;
        L_0x01fd:
            r0 = r41;
            r11 = r0.item;	 Catch:{ Exception -> 0x0481 }
            r11 = (com.zidoo.fileexplorer.browse.BrowseItem.FileItem) r11;	 Catch:{ Exception -> 0x0481 }
            r10 = r11.getFile();	 Catch:{ Exception -> 0x0481 }
            r0 = r41;
            r0 = r0.context;	 Catch:{ Exception -> 0x0481 }
            r37 = r0;
            r0 = r41;
            r0 = r0.context;	 Catch:{ Exception -> 0x0481 }
            r38 = r0;
            r38 = zidoo.model.BoxModel.getModelCode(r38);	 Catch:{ Exception -> 0x0481 }
            r4 = zidoo.model.BoxModel.getModel(r37, r38);	 Catch:{ Exception -> 0x0481 }
            r21 = r10.getPath();	 Catch:{ Exception -> 0x0481 }
            r37 = r4.getSmbRoot();	 Catch:{ Exception -> 0x0481 }
            r37 = r37.length();	 Catch:{ Exception -> 0x0481 }
            r37 = r37 + 1;
            r0 = r21;
            r1 = r37;
            r30 = r0.substring(r1);	 Catch:{ Exception -> 0x0481 }
            r37 = 47;
            r0 = r30;
            r1 = r37;
            r13 = r0.indexOf(r1);	 Catch:{ Exception -> 0x0481 }
            r37 = -1;
            r0 = r37;
            if (r13 != r0) goto L_0x02ec;
        L_0x0241:
            r32 = "";
            r25 = r30;
        L_0x0246:
            r37 = 35;
            r0 = r25;
            r1 = r37;
            r19 = r0.indexOf(r1);	 Catch:{ Exception -> 0x0481 }
            r37 = 0;
            r0 = r25;
            r1 = r37;
            r2 = r19;
            r17 = r0.substring(r1, r2);	 Catch:{ Exception -> 0x0481 }
            r37 = r19 + 1;
            r0 = r25;
            r1 = r37;
            r37 = r0.substring(r1);	 Catch:{ Exception -> 0x0481 }
            r24 = zidoo.tool.ZidooFileUtils.decodeCommand(r37);	 Catch:{ Exception -> 0x0481 }
            r37 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0481 }
            r37.<init>();	 Catch:{ Exception -> 0x0481 }
            r38 = "smb://";
            r37 = r37.append(r38);	 Catch:{ Exception -> 0x0481 }
            r0 = r37;
            r1 = r17;
            r37 = r0.append(r1);	 Catch:{ Exception -> 0x0481 }
            r38 = "/";
            r37 = r37.append(r38);	 Catch:{ Exception -> 0x0481 }
            r0 = r37;
            r1 = r24;
            r37 = r0.append(r1);	 Catch:{ Exception -> 0x0481 }
            r33 = r37.toString();	 Catch:{ Exception -> 0x0481 }
            r0 = r41;
            r0 = r0.context;	 Catch:{ Exception -> 0x0481 }
            r37 = r0;
            r37 = com.zidoo.fileexplorer.db.MountHistoryDatabase.helper(r37);	 Catch:{ Exception -> 0x0481 }
            r38 = "url";
            r0 = r37;
            r1 = r38;
            r2 = r33;
            r12 = r0.query(r1, r2);	 Catch:{ Exception -> 0x0481 }
            r12 = (com.zidoo.fileexplorer.bean.MountHistory) r12;	 Catch:{ Exception -> 0x0481 }
            if (r12 == 0) goto L_0x02fe;
        L_0x02ac:
            r35 = r12.getUser();	 Catch:{ Exception -> 0x0481 }
            r20 = r12.getPassword();	 Catch:{ Exception -> 0x0481 }
        L_0x02b4:
            r37 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0481 }
            r37.<init>();	 Catch:{ Exception -> 0x0481 }
            r0 = r37;
            r1 = r33;
            r37 = r0.append(r1);	 Catch:{ Exception -> 0x0481 }
            r0 = r37;
            r1 = r32;
            r37 = r0.append(r1);	 Catch:{ Exception -> 0x0481 }
            r29 = r37.toString();	 Catch:{ Exception -> 0x0481 }
            r15 = new zidoo.browse.FileIdentifier;	 Catch:{ Exception -> 0x0481 }
            r37 = 2;
            r0 = r37;
            r1 = r29;
            r2 = r17;
            r15.<init>(r0, r1, r2);	 Catch:{ Exception -> 0x0481 }
            r0 = r35;
            r15.setUser(r0);	 Catch:{ Exception -> 0x0491 }
            r0 = r20;
            r15.setPassword(r0);	 Catch:{ Exception -> 0x0491 }
            r0 = r17;
            r15.setExtra(r0);	 Catch:{ Exception -> 0x0491 }
            r14 = r15;
            goto L_0x0027;
        L_0x02ec:
            r37 = 0;
            r0 = r30;
            r1 = r37;
            r25 = r0.substring(r1, r13);	 Catch:{ Exception -> 0x0481 }
            r0 = r30;
            r32 = r0.substring(r13);	 Catch:{ Exception -> 0x0481 }
            goto L_0x0246;
        L_0x02fe:
            r0 = r41;
            r0 = r0.context;	 Catch:{ Exception -> 0x0481 }
            r37 = r0;
            r0 = r37;
            r1 = r17;
            r8 = com.zidoo.fileexplorer.tool.SmbDatabaseUtils.queryAll(r0, r1);	 Catch:{ Exception -> 0x0481 }
            r37 = r8.isEmpty();	 Catch:{ Exception -> 0x0481 }
            if (r37 != 0) goto L_0x0365;
        L_0x0312:
            r37 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0481 }
            r37.<init>();	 Catch:{ Exception -> 0x0481 }
            r38 = "smb://.+/";
            r37 = r37.append(r38);	 Catch:{ Exception -> 0x0481 }
            r0 = r37;
            r1 = r24;
            r37 = r0.append(r1);	 Catch:{ Exception -> 0x0481 }
            r38 = ".*";
            r37 = r37.append(r38);	 Catch:{ Exception -> 0x0481 }
            r22 = r37.toString();	 Catch:{ Exception -> 0x0481 }
            r37 = 0;
            r0 = r37;
            r3 = r8.get(r0);	 Catch:{ Exception -> 0x0481 }
            r3 = (zidoo.samba.exs.SambaDevice) r3;	 Catch:{ Exception -> 0x0481 }
            r37 = r8.iterator();	 Catch:{ Exception -> 0x0481 }
        L_0x033f:
            r38 = r37.hasNext();	 Catch:{ Exception -> 0x0481 }
            if (r38 == 0) goto L_0x035b;
        L_0x0345:
            r27 = r37.next();	 Catch:{ Exception -> 0x0481 }
            r27 = (zidoo.samba.exs.SambaDevice) r27;	 Catch:{ Exception -> 0x0481 }
            r38 = r27.getUrl();	 Catch:{ Exception -> 0x0481 }
            r0 = r38;
            r1 = r22;
            r38 = r0.matches(r1);	 Catch:{ Exception -> 0x0481 }
            if (r38 == 0) goto L_0x033f;
        L_0x0359:
            r3 = r27;
        L_0x035b:
            r35 = r3.getUser();	 Catch:{ Exception -> 0x0481 }
            r20 = r3.getPassWord();	 Catch:{ Exception -> 0x0481 }
            goto L_0x02b4;
        L_0x0365:
            r35 = "guest";
            r20 = "";
            goto L_0x02b4;
        L_0x036d:
            r0 = r41;
            r0 = r0.item;	 Catch:{ Exception -> 0x0481 }
            r37 = r0;
            r37 = r37.getType();	 Catch:{ Exception -> 0x0481 }
            r38 = com.zidoo.fileexplorer.browse.BrowseItem.Type.NFS_SHARE;	 Catch:{ Exception -> 0x0481 }
            r0 = r37;
            r1 = r38;
            if (r0 != r1) goto L_0x041c;
        L_0x037f:
            r0 = r41;
            r0 = r0.item;	 Catch:{ Exception -> 0x0481 }
            r37 = r0;
            r37 = (com.zidoo.fileexplorer.browse.BrowseItem.NfsShareItem) r37;	 Catch:{ Exception -> 0x0481 }
            r11 = r37.getMountFile();	 Catch:{ Exception -> 0x0481 }
        L_0x038b:
            r10 = r11.getFile();	 Catch:{ Exception -> 0x0481 }
            r0 = r41;
            r0 = r0.context;	 Catch:{ Exception -> 0x0481 }
            r37 = r0;
            r4 = zidoo.model.BoxModel.getModel(r37);	 Catch:{ Exception -> 0x0481 }
            r21 = r10.getPath();	 Catch:{ Exception -> 0x0481 }
            r37 = r4.getNfsRoot();	 Catch:{ Exception -> 0x0481 }
            r37 = r37.length();	 Catch:{ Exception -> 0x0481 }
            r37 = r37 + 1;
            r0 = r21;
            r1 = r37;
            r30 = r0.substring(r1);	 Catch:{ Exception -> 0x0481 }
            r37 = 47;
            r0 = r30;
            r1 = r37;
            r13 = r0.indexOf(r1);	 Catch:{ Exception -> 0x0481 }
            r37 = -1;
            r0 = r37;
            if (r13 != r0) goto L_0x0428;
        L_0x03bf:
            r32 = "";
            r25 = r30;
        L_0x03c4:
            r37 = 35;
            r0 = r25;
            r1 = r37;
            r19 = r0.indexOf(r1);	 Catch:{ Exception -> 0x0481 }
            r37 = 0;
            r0 = r25;
            r1 = r37;
            r2 = r19;
            r17 = r0.substring(r1, r2);	 Catch:{ Exception -> 0x0481 }
            r37 = r19 + 1;
            r0 = r25;
            r1 = r37;
            r37 = r0.substring(r1);	 Catch:{ Exception -> 0x0481 }
            r24 = zidoo.tool.ZidooFileUtils.decodeCommand(r37);	 Catch:{ Exception -> 0x0481 }
            r37 = java.util.Locale.getDefault();	 Catch:{ Exception -> 0x0481 }
            r38 = "%s/%s%s";
            r39 = 3;
            r0 = r39;
            r0 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x0481 }
            r39 = r0;
            r40 = 0;
            r39[r40] = r17;	 Catch:{ Exception -> 0x0481 }
            r40 = 1;
            r39[r40] = r24;	 Catch:{ Exception -> 0x0481 }
            r40 = 2;
            r39[r40] = r32;	 Catch:{ Exception -> 0x0481 }
            r33 = java.lang.String.format(r37, r38, r39);	 Catch:{ Exception -> 0x0481 }
            r15 = new zidoo.browse.FileIdentifier;	 Catch:{ Exception -> 0x0481 }
            r37 = 3;
            r0 = r37;
            r1 = r33;
            r2 = r17;
            r15.<init>(r0, r1, r2);	 Catch:{ Exception -> 0x0481 }
            r0 = r17;
            r15.setExtra(r0);	 Catch:{ Exception -> 0x0491 }
            r14 = r15;
            goto L_0x0027;
        L_0x041c:
            r0 = r41;
            r0 = r0.item;	 Catch:{ Exception -> 0x0481 }
            r37 = r0;
            r37 = (com.zidoo.fileexplorer.browse.BrowseItem.FileItem) r37;	 Catch:{ Exception -> 0x0481 }
            r11 = r37;
            goto L_0x038b;
        L_0x0428:
            r37 = 0;
            r0 = r30;
            r1 = r37;
            r25 = r0.substring(r1, r13);	 Catch:{ Exception -> 0x0481 }
            r0 = r30;
            r32 = r0.substring(r13);	 Catch:{ Exception -> 0x0481 }
            goto L_0x03c4;
        L_0x0439:
            r37 = "url";
            r38 = r14.getUri();	 Catch:{ Exception -> 0x0481 }
            r0 = r37;
            r1 = r38;
            r5.putExtra(r0, r1);	 Catch:{ Exception -> 0x0481 }
            r37 = "user";
            r38 = r14.getUser();	 Catch:{ Exception -> 0x0481 }
            r0 = r37;
            r1 = r38;
            r5.putExtra(r0, r1);	 Catch:{ Exception -> 0x0481 }
            r37 = "password";
            r38 = r14.getPassword();	 Catch:{ Exception -> 0x0481 }
            r0 = r37;
            r1 = r38;
            r5.putExtra(r0, r1);	 Catch:{ Exception -> 0x0481 }
            r37 = "host";
            r38 = r14.getExtra();	 Catch:{ Exception -> 0x0481 }
            r0 = r37;
            r1 = r38;
            r5.putExtra(r0, r1);	 Catch:{ Exception -> 0x0481 }
            r37 = "ip";
            r38 = r14.getPassword();	 Catch:{ Exception -> 0x0481 }
            r0 = r37;
            r1 = r38;
            r5.putExtra(r0, r1);	 Catch:{ Exception -> 0x0481 }
            goto L_0x005e;
        L_0x0481:
            r9 = move-exception;
        L_0x0482:
            r9.printStackTrace();
            goto L_0x00a8;
        L_0x0487:
            r37 = "identifier";
            r0 = r37;
            r5.putExtra(r0, r14);	 Catch:{ Exception -> 0x0481 }
            goto L_0x005e;
        L_0x0491:
            r9 = move-exception;
            r14 = r15;
            goto L_0x0482;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.zidoo.fileexplorer.browse.BrowseActivity.BrowseThread.run():void");
        }
    }

    private class FocusFindRunnable implements Runnable {
        private RecyclerView list;
        private int position;

        private FocusFindRunnable(RecyclerView list, int position) {
            this.list = list;
            this.position = position;
        }

        public void run() {
            View focus = this.list.getLayoutManager().findViewByPosition(this.position);
            boolean focused = false;
            if (focus != null) {
                focused = focus.requestFocus();
            }
            if (BrowseActivity.this.mSelectBtn != null && BrowseActivity.this.mCancelBtn != null) {
                BrowseActivity.this.mSelectBtn.setFocusable(true);
                BrowseActivity.this.mCancelBtn.setFocusable(true);
                if (!focused && !BrowseActivity.this.mSelectBtn.requestFocus()) {
                    BrowseActivity.this.mCancelBtn.requestFocus();
                }
            }
        }
    }

    private class SetupListRunnable implements Runnable {
        private int mode;
        private BrowseItem parent;
        private BrowseResult result;

        SetupListRunnable(BrowseItem parent, BrowseResult result, int mode) {
            this.parent = parent;
            this.result = result;
            this.mode = mode;
        }

        public void run() {
            BrowseActivity.this.hideLoadProgress();
            switch (this.result.getResult()) {
                case 0:
                case 2:
                    BrowseActivity.this.mAdapter.setItems(this.result.getItems());
                    BrowseActivity.this.mFileList.scrollToPosition(this.result.getRequestPosition());
                    BrowseActivity.this.mHandler.post(new FocusFindRunnable(BrowseActivity.this.mFileList, this.result.getRequestPosition()));
                    if (!(BrowseActivity.this.mSelectBtn == null || BrowseActivity.this.mCancelBtn == null)) {
                        BrowseActivity.this.mSelectBtn.setFocusable(false);
                        BrowseActivity.this.mCancelBtn.setFocusable(false);
                    }
                    if (this.mode == 301) {
                        BrowseActivity.this.mPaths.push(this.parent);
                        BrowseActivity.this.setupPaths();
                        if (BrowseActivity.this.mSelectBtn != null) {
                            BrowseActivity.this.mSelectBtn.setEnabled(BrowseActivity.this.canBrowse(this.parent));
                            return;
                        }
                        return;
                    } else if (this.mode == 302) {
                        BrowseActivity.this.mPaths.pop();
                        BrowseActivity.this.setupPaths();
                        if (BrowseActivity.this.mSelectBtn != null) {
                            BrowseActivity.this.mSelectBtn.setEnabled(BrowseActivity.this.canBrowse(this.parent));
                            return;
                        }
                        return;
                    } else {
                        return;
                    }
                case 1:
                    Utils.toast(BrowseActivity.this, (int) R.string.user_pass_error);
                    new SmbLoginDialog(BrowseActivity.this, this.parent).show();
                    return;
                case 4:
                    Utils.toast(BrowseActivity.this.getBaseContext(), (int) R.string.unknow_error);
                    return;
                default:
                    return;
            }
        }
    }

    private class SmbLoginDialog extends Dialog implements OnClickListener {
        private BrowseItem item;

        SmbLoginDialog(@NonNull Context context, BrowseItem item) {
            super(context, R.style.defaultDialog);
            this.item = item;
            setContentView(R.layout.dialog_smb_login);
        }

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EditText etUser = (EditText) findViewById(R.id.et_user);
            EditText etPassword = (EditText) findViewById(R.id.et_password);
            CheckBox prompt = (CheckBox) findViewById(R.id.lock_ac_isport);
            ((TextView) findViewById(R.id.lock_input_hit)).setText(BrowseActivity.this.getString(R.string.connect_smb, new Object[]{this.item.getName()}));
            String user = "";
            String password = "";
            if (this.item instanceof SmbServerItem) {
                SmbServerItem smbServer = this.item;
                user = smbServer.getUser();
                password = smbServer.getPassword();
            } else if (this.item instanceof SmbShareItem) {
                SmbShareItem smbShare = this.item;
                user = smbShare.getUser();
                password = smbShare.getPassword();
            }
            if (!"guest".equals(user)) {
                etUser.setText(user);
                etPassword.setText(password);
            }
            prompt.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    View user = SmbLoginDialog.this.findViewById(R.id.et_user);
                    View password = SmbLoginDialog.this.findViewById(R.id.et_password);
                    if (isChecked) {
                        user.setEnabled(false);
                        password.setEnabled(false);
                        user.setFocusable(false);
                        password.setFocusable(false);
                        return;
                    }
                    user.setEnabled(true);
                    password.setEnabled(true);
                    user.setFocusable(true);
                    password.setFocusable(true);
                }
            });
            findViewById(R.id.lock_input_ok).setOnClickListener(this);
            findViewById(R.id.lock_input_set).setOnClickListener(this);
            etUser.requestFocus();
        }

        public void onClick(View v) {
            if (v.getId() == R.id.lock_input_ok) {
                String user;
                String password;
                EditText etUser = (EditText) findViewById(R.id.et_user);
                EditText etPassword = (EditText) findViewById(R.id.et_password);
                if (((CheckBox) findViewById(R.id.lock_ac_isport)).isChecked()) {
                    user = "guest";
                    password = "";
                } else {
                    user = etUser.getText().toString().trim();
                    password = etPassword.getText().toString().trim();
                    if (user.equals("")) {
                        Utils.toast(getContext(), (int) R.string.input_user);
                        return;
                    }
                }
                if (this.item instanceof SmbServerItem) {
                    SmbServerItem smbServer = this.item;
                    smbServer.setUser(user);
                    smbServer.setPassword(password);
                } else if (this.item instanceof SmbShareItem) {
                    SmbShareItem smbShare = this.item;
                    smbShare.setUser(user);
                    smbShare.setPassword(password);
                }
                BrowseActivity.this.load(null, this.item);
            }
            dismiss();
        }
    }

    private class FileItemDecoration extends ItemDecoration {
        private final int space;

        private FileItemDecoration() {
            this.space = (int) (-(3.0f * BrowseActivity.this.getResources().getDisplayMetrics().density));
        }

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position == 0) {
                outRect.set(0, 0, 0, this.space);
            } else if (position == parent.getAdapter().getItemCount() - 1) {
                outRect.set(0, this.space, 0, this.space);
            } else {
                outRect.set(0, this.space, 0, 0);
            }
        }
    }

    private class FileOpenThread extends Thread implements OnItemListListener {
        protected boolean cancel;
        private BrowseItem current;
        private int mode;
        private BrowseItem parent;
        private BrowseResult result;
        private boolean waiting;

        private FileOpenThread(BrowseItem current) {
            this.cancel = false;
            this.waiting = false;
            this.current = current;
            this.mode = 301;
            this.parent = current;
        }

        private FileOpenThread(BrowseItem parent, BrowseItem current) {
            this.cancel = false;
            this.waiting = false;
            this.parent = parent;
            this.current = current;
            this.mode = 302;
        }

        public void run() {
            BrowseActivity.this.delayShowLoadProgress();
            this.parent.setOnItemListListener(this);
            this.result = this.parent.listFiles(BrowseActivity.this.getBaseContext(), BrowseActivity.this.mFileFilter);
            if (this.result.getResult() == 0) {
                Collections.sort(this.result.getItems());
            }
            if (this.mode == 302) {
                List<BrowseItem> items = this.result.getItems();
                for (int i = 0; i < items.size(); i++) {
                    if (((BrowseItem) items.get(i)).getName().equals(this.current.getName())) {
                        this.result.setRequestPosition(i);
                        break;
                    }
                }
            }
            if (!this.cancel) {
                if (this.result.getResult() == 2 && this.result.getItems().size() == 0) {
                    this.waiting = true;
                } else {
                    BrowseActivity.this.mHandler.post(new SetupListRunnable(this.parent, this.result, this.mode));
                }
            }
        }

        void cancel() {
            this.cancel = true;
            this.parent.setOnItemListListener(null);
            try {
                interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void onAddedOne() {
            if (this.waiting) {
                this.waiting = false;
                BrowseActivity.this.mHandler.post(new SetupListRunnable(this.parent, this.result, this.mode));
                return;
            }
            BrowseActivity.this.mAdapter.onAddedOne();
        }

        public void onComplete() {
            if (this.waiting) {
                this.waiting = false;
                BrowseActivity.this.mHandler.post(new SetupListRunnable(this.parent, this.result, this.mode));
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        init();
        initView();
        loadData();
    }

    private void init() {
        String title;
        String name;
        int device;
        int filter;
        int target;
        String pkg;
        int bgd;
        int help;
        float scale;
        int shade;
        int hrpd;
        int vtpd;
        int version;
        int clickModel;
        this.mAdapter.setOnFileListener(this);
        Bundle extras = getIntent().getExtras();
        int flag = extras.getInt(BrowseConstant.EXTRA_FLAG, 0);
        String[] customExtras = null;
        float density = getResources().getDisplayMetrics().density;
        String initialPath = null;
        if (flag == 0) {
            title = extras.getString(BrowseConstant.EXTRA_TITLE);
            name = extras.getString("name");
            device = 13;
            filter = 4;
            target = 1;
            pkg = extras.getString(BrowseConstant.EXTRA_PACKAGE_NAME);
            bgd = -1;
            help = extras.getInt(BrowseConstant.EXTRA_HELP, -1);
            scale = 1.0f;
            shade = -16777216;
            hrpd = 17;
            vtpd = 16;
            version = 1;
            clickModel = 12;
        } else {
            title = extras.getString(BrowseConstant.EXTRA_TITLE);
            name = extras.getString("name");
            device = extras.getInt(BrowseConstant.EXTRA_DEVICE, 31);
            filter = extras.getInt(BrowseConstant.EXTRA_FILTER, 0);
            customExtras = extras.getStringArray(BrowseConstant.EXTRA_CUSTOM_EXTRAS);
            target = extras.getInt(BrowseConstant.EXTRA_TARGET, BrowseConstant.TARGET_ALL);
            pkg = extras.getString(BrowseConstant.EXTRA_PACKAGE_NAME);
            bgd = extras.getInt(BrowseConstant.EXTRA_BGD, -1);
            help = extras.getInt(BrowseConstant.EXTRA_HELP, 2);
            scale = extras.getFloat(BrowseConstant.EXTRA_SCALE, 0.8f);
            shade = extras.getInt(BrowseConstant.EXTRA_SHADE, BrowseConstant.SHADE_DEFAULT);
            hrpd = extras.getInt(BrowseConstant.EXTRA_HORIZONTAL_PADDING, 17);
            vtpd = extras.getInt(BrowseConstant.EXTRA_VERTICAL_PADDING, 16);
            initialPath = extras.getString(BrowseConstant.EXTRA_IDENTIFIER);
            clickModel = extras.getInt(BrowseConstant.EXTRA_CLICK_MODEL, 0);
            version = extras.getInt(BrowseConstant.EXTRA_VERSION, 2);
        }
        this.mBrowser = new BrowseInfo(title, name, device, filter, target, customExtras, flag, pkg, bgd, help, scale, shade, ((float) hrpd) * density, ((float) vtpd) * density, initialPath, clickModel, version);
        if (customExtras != null) {
            FileType.registerFiletype(20, customExtras);
        }
        this.mFileFilter = FileOperater.buildBrowsingFileFilter(this.mBrowser.getFilter());
    }

    private void initView() {
        this.mFileList = (RecyclerView) findViewById(R.id.file_list);
        this.mFileList.setLayoutManager(new LinearLayoutManager(this, 1, false));
        this.mFileList.setAdapter(this.mAdapter);
        this.mFileList.addItemDecoration(new FileItemDecoration());
        ((TextView) findViewById(R.id.title)).setText(this.mBrowser.getTitle());
        this.mPathText = (TextView) findViewById(R.id.path);
        if ((this.mBrowser.getTarget() & 1) == 0) {
            View layout = findViewById(R.id.main_layout);
            findViewById(R.id.right_line).setVisibility(8);
            findViewById(R.id.select_directory).setVisibility(8);
            findViewById(R.id.cancel).setVisibility(8);
            LayoutParams lp = (LayoutParams) layout.getLayoutParams();
            lp.width = (int) (840.0f * getResources().getDisplayMetrics().density);
            layout.setLayoutParams(lp);
            return;
        }
        this.mSelectBtn = (Button) findViewById(R.id.select_directory);
        this.mCancelBtn = (Button) findViewById(R.id.cancel);
        this.mSelectBtn.setEnabled(false);
        this.mSelectBtn.setOnClickListener(this);
        this.mSelectBtn.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                BrowseActivity.this.mPathText.setSelected(hasFocus);
            }
        });
        findViewById(R.id.cancel).setOnClickListener(this);
    }

    private void loadData() {
        load(null, new RootItem(this.mBrowser.getDeviceTag()));
    }

    public void onClick(View v) {
        if (v.getId() == R.id.select_directory) {
            BrowseItem item = (BrowseItem) this.mPaths.peek();
            if (canBrowse(item)) {
                new BrowseThread(this, item).start();
                return;
            }
            return;
        }
        finish();
    }

    private void delayShowLoadProgress() {
        this.mIsLoading = true;
        this.mHandler.postDelayed(this.SHOW_LOAD_PROGRESS, 500);
    }

    private void hideLoadProgress() {
        this.mIsLoading = false;
        this.mHandler.removeCallbacks(this.SHOW_LOAD_PROGRESS);
        if (this.mLoadDialog != null && this.mLoadDialog.isShowing()) {
            this.mLoadDialog.dismiss();
        }
    }

    private void setupPaths() {
        if (this.mPaths.size() > 1) {
            StringBuilder sb = new StringBuilder();
            sb.append(((BrowseItem) this.mPaths.get(1)).getDisplayPath());
            for (int i = 2; i < this.mPaths.size(); i++) {
                sb.append("/").append(((BrowseItem) this.mPaths.get(i)).getDisplayPath());
            }
            this.mPathText.setText(getString(R.string.browse_path, new Object[]{sb.toString()}));
            return;
        }
        this.mPathText.setText("");
    }

    private void cancelLoad() {
        if (this.mLoadThread != null) {
            this.mLoadThread.cancel();
            this.mLoadThread = null;
        }
        hideLoadProgress();
    }

    private boolean backUp() {
        if (this.mPaths.size() <= 1) {
            return false;
        }
        load((BrowseItem) this.mPaths.get(this.mPaths.size() - 2), (BrowseItem) this.mPaths.get(this.mPaths.size() - 1));
        return true;
    }

    private void load(BrowseItem parent, BrowseItem current) {
        if (this.mLoadThread != null) {
            this.mLoadThread.cancel();
        }
        if (parent == null) {
            this.mLoadThread = new FileOpenThread(current);
        } else {
            this.mLoadThread = new FileOpenThread(parent, current);
        }
        this.mLoadThread.start();
    }

    public void onFileClick(BrowseItem item) {
        if (item.isDirectory()) {
            load(null, item);
        } else if (canBrowse(item)) {
            new BrowseThread(this, item).start();
        }
    }

    private boolean canBrowse(BrowseItem item) {
        int target;
        switch (item.getType()) {
            case FLASH:
            case USB:
            case SMB_MOUNT:
            case NFS_MOUNT:
            case SMB_SHARE:
            case NFS_SHARE:
                target = 1;
                break;
            case FILE:
                target = 1 << ((FileItem) item).getFileType();
                break;
            default:
                target = 0;
                break;
        }
        if ((this.mBrowser.getTarget() & target) != 0) {
            return true;
        }
        return false;
    }

    public void onBack() {
        backUp();
    }

    public void onTop() {
        ((RecyclerView) findViewById(R.id.file_list)).scrollToPosition(0);
        this.mHandler.post(new Runnable() {
            public void run() {
                ViewHolder holder = ((RecyclerView) BrowseActivity.this.findViewById(R.id.file_list)).findViewHolderForAdapterPosition(0);
                if (holder != null) {
                    holder.itemView.requestFocus();
                }
            }
        });
    }

    public boolean onFileItemKey(View v, int position, int keyCode, KeyEvent event) {
        if (event.getAction() != 0 || keyCode != 4) {
            return false;
        }
        if (!this.mIsLoading) {
            return backUp();
        }
        cancelLoad();
        return true;
    }

    public void onBackPressed() {
        if (!backUp()) {
            super.onBackPressed();
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() != 0 || event.getKeyCode() != 82) {
            return super.dispatchKeyEvent(event);
        }
        finish();
        return true;
    }
}
