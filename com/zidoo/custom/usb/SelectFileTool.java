package com.zidoo.custom.usb;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.zidoo.custom.usb.ZidooStorageTool.ScanStorageOnListener;
import com.zidoo.fileexplorer.R;
import java.io.File;
import java.util.ArrayList;

public class SelectFileTool {
    private static final int SELECT_MODE_SELECT_DIRECTORY = 1;
    private static final int SELECT_MODE_SELECT_FILE = 0;
    private static SelectFileTool mZidooSelectFileTool = null;
    private boolean isScanCompelet = false;
    private boolean isSetFileName = true;
    private Context mContext = null;
    private Dialog mDialog = null;
    private ListView mListView = null;
    private View mLoadingView = null;
    private ArrayList<String> mPathList = new ArrayList();
    private TextView mPathView = null;
    private SelectFileListener mSelectFileListener = null;
    private String mSelectFileType = FileTypeManager.open_type_file;
    private int mSelectMode = 0;
    private SotrageAdapter mSotrageAdapter = null;
    private ArrayList<FileObject> mStorageInfoList = new ArrayList();
    private TextView mTitileView = null;
    private ZidooStorageTool mZidooStorageTool = null;

    public interface SelectFileListener {
        void selectPath(String str);
    }

    class SotrageAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<FileInfo> mFileInfoList = new ArrayList();

        class FileAdapterView {
            ImageView iconView;
            TextView titleView;

            FileAdapterView() {
            }
        }

        public SotrageAdapter(Context mContext, ArrayList<FileInfo> mFileInfoList) {
            this.mFileInfoList = mFileInfoList;
            this.mContext = mContext;
        }

        public void setFileInfoList(ArrayList<FileInfo> fileInfoList) {
            this.mFileInfoList = fileInfoList;
            notifyDataSetChanged();
        }

        public FileInfo getFileInfo(int position) {
            return (FileInfo) this.mFileInfoList.get(position);
        }

        public int getCount() {
            return this.mFileInfoList.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            FileAdapterView fileAdapterView;
            if (convertView == null) {
                fileAdapterView = new FileAdapterView();
                convertView = View.inflate(this.mContext, R.attr.accountAccountNameColor, null);
                fileAdapterView.iconView = (ImageView) convertView.findViewById(2131296272);
                fileAdapterView.titleView = (TextView) convertView.findViewById(2131296273);
                convertView.setTag(fileAdapterView);
            } else {
                fileAdapterView = (FileAdapterView) convertView.getTag();
            }
            FileInfo fileInfo = (FileInfo) this.mFileInfoList.get(position);
            if (fileInfo.getFileType().equals(FileTypeManager.open_type_file)) {
                fileAdapterView.iconView.setImageResource(2130837509);
            } else if (fileInfo.getFileType().equals(FileTypeManager.open_type_subtitile)) {
                fileAdapterView.iconView.setImageResource(2130837512);
            } else {
                fileAdapterView.iconView.setImageResource(R.array.usb_devices);
            }
            fileAdapterView.titleView.setText(fileInfo.getFileName());
            fileAdapterView.titleView.setSelected(true);
            return convertView;
        }
    }

    public static SelectFileTool getInstance(Context context) {
        if (mZidooSelectFileTool == null) {
            mZidooSelectFileTool = new SelectFileTool(context);
        }
        return mZidooSelectFileTool;
    }

    public void showSelectFileDialog(SelectFileListener mSelectFileListener) {
        this.mSelectFileListener = mSelectFileListener;
        this.mSelectMode = 0;
        setView();
        if (this.mDialog != null && !this.mDialog.isShowing()) {
            this.mDialog.show();
        }
    }

    public void showSelectFileDialog() {
        showSelectFileDialog(this.mSelectFileListener);
    }

    public void showSelectDirectoryDialog(boolean isSetFileName, SelectFileListener mSelectFileListener) {
        this.mSelectFileListener = mSelectFileListener;
        this.mSelectMode = 1;
        this.isSetFileName = isSetFileName;
        setView();
        if (this.mDialog != null && !this.mDialog.isShowing()) {
            this.mDialog.show();
        }
    }

    public void showSelectDirectoryDialog(boolean isSetFileName) {
        showSelectDirectoryDialog(isSetFileName, this.mSelectFileListener);
    }

    public void showSelectDirectoryDialog() {
        showSelectDirectoryDialog(true);
    }

    public SelectFileTool(Context mContext) {
        this.mContext = mContext;
        this.mDialog = initDialogView();
        init();
    }

    public SelectFileTool setTitle(String titile) {
        this.mTitileView.setText(titile);
        return this;
    }

    public SelectFileTool setSelectFileType(String selectFileType) {
        this.mSelectFileType = selectFileType;
        return this;
    }

    public SelectFileTool setSelectFileListener(SelectFileListener mSelectFileListener) {
        this.mSelectFileListener = mSelectFileListener;
        return this;
    }

    private Dialog initDialogView() {
        View view = View.inflate(this.mContext, R.attr.accountAccountLogoutColor, null);
        initView(view);
        Dialog dialog = new Dialog(this.mContext, R.id.action_divider);
        dialog.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() != 0 || (keyCode != 4 && keyCode != 111)) {
                    return false;
                }
                SelectFileTool.this.backFileDirectory();
                return true;
            }
        });
        dialog.setContentView(view);
        return dialog;
    }

    private void initView(View view) {
        this.mLoadingView = view.findViewById(2131296270);
        this.mTitileView = (TextView) view.findViewById(2131296267);
        this.mPathView = (TextView) view.findViewById(2131296268);
        this.mListView = (ListView) view.findViewById(2131296269);
        this.mListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                FileObject fileListObject = (FileObject) SelectFileTool.this.mStorageInfoList.get(SelectFileTool.this.mStorageInfoList.size() - 1);
                FileInfo fileInfo = (FileInfo) fileListObject.getFileInfo_List().get(position);
                String path;
                if (fileInfo.getFileType().equals(FileTypeManager.open_type_file)) {
                    fileListObject.setCurrentIndex(position);
                    if (SelectFileTool.this.mStorageInfoList.size() == 1) {
                        SelectFileTool.this.mPathList.add(fileInfo.getFileName());
                    } else {
                        path = fileInfo.getPath();
                        SelectFileTool.this.mPathList.add(path.substring(path.lastIndexOf("/") + 1));
                    }
                    SelectFileTool.this.setPath();
                    SelectFileTool.this.openFileDirectory(fileInfo.getPath());
                } else if (fileInfo.getFileType().equals(FileTypeManager.open_type_back)) {
                    SelectFileTool.this.backFileDirectory();
                } else {
                    path = fileInfo.getPath();
                    if (SelectFileTool.this.mSelectFileListener != null) {
                        SelectFileTool.this.mSelectFileListener.selectPath(path);
                    }
                    SelectFileTool.this.dismiss();
                }
            }
        });
    }

    private void openFileDirectory(String path) {
        ArrayList<FileInfo> fileInfo_List = scanFile(path);
        this.mStorageInfoList.add(new FileObject(0, path, fileInfo_List));
        setAdapter(fileInfo_List, 0);
    }

    private void backFileDirectory() {
        int szie = this.mStorageInfoList.size();
        if (szie < 2) {
            dismiss();
            return;
        }
        FileObject fileListObject = (FileObject) this.mStorageInfoList.get(szie - 2);
        setAdapter(fileListObject.getFileInfo_List(), fileListObject.getCurrentIndex());
        this.mStorageInfoList.remove(szie - 1);
        this.mPathList.remove(this.mPathList.size() - 1);
        setPath();
    }

    private void setAdapter(ArrayList<FileInfo> fileInfoList, int currentIndex) {
        if (this.mSotrageAdapter == null) {
            this.mSotrageAdapter = new SotrageAdapter(this.mContext, fileInfoList);
            this.mListView.setAdapter(this.mSotrageAdapter);
        } else {
            this.mSotrageAdapter.setFileInfoList(fileInfoList);
        }
        this.mListView.setSelection(currentIndex);
        this.mListView.requestFocus();
    }

    private void setView() {
        if (this.isScanCompelet) {
            FileObject fileListObject = (FileObject) this.mStorageInfoList.get(0);
            this.mStorageInfoList.clear();
            this.mStorageInfoList.add(fileListObject);
            setAdapter(fileListObject.getFileInfo_List(), 0);
        }
    }

    public void dismiss() {
        if (this.mDialog != null && this.mDialog.isShowing()) {
            this.mDialog.dismiss();
        }
    }

    private FileInfo getDeviceInfo(String path, int storageType) {
        String title = "";
        int typeCount = getTypeCOunt(storageType);
        if (storageType == 0) {
            title = typeCount == 0 ? "USB" : "USB " + typeCount;
        } else if (storageType == 1) {
            title = typeCount == 0 ? "Sdcard" : "Sdcard " + typeCount;
        } else if (storageType == 2) {
            title = "Flash";
        } else if (storageType == 3) {
            title = typeCount == 0 ? "Sata" : "Sata " + typeCount;
        } else if (storageType == 4) {
            title = typeCount == 0 ? "HDD" : "HDD " + typeCount;
        }
        return new FileInfo(new File(path), path, FileTypeManager.open_type_file, title, storageType, typeCount);
    }

    private int getTypeCOunt(int storageType) {
        int count = -1;
        for (int i = 0; i < ((FileObject) this.mStorageInfoList.get(0)).getFileInfo_List().size(); i++) {
            if (((FileInfo) ((FileObject) this.mStorageInfoList.get(0)).getFileInfo_List().get(i)).getFlashType() == storageType) {
                int typeCount = ((FileInfo) ((FileObject) this.mStorageInfoList.get(0)).getFileInfo_List().get(i)).getTypeCount();
                if (typeCount > count) {
                    count = typeCount;
                }
            }
        }
        return count + 1;
    }

    private void setPath() {
        String path = "";
        for (int i = 0; i < this.mPathList.size(); i++) {
            path = new StringBuilder(String.valueOf(path)).append("/").append((String) this.mPathList.get(i)).toString();
        }
        this.mPathView.setText(path);
    }

    private void init() {
        this.mZidooStorageTool = new ZidooStorageTool(this.mContext);
        this.mZidooStorageTool.setScanStorageOnListener(new ScanStorageOnListener() {
            public void onInitScanStart() {
                SelectFileTool.this.isScanCompelet = false;
                SelectFileTool.this.mLoadingView.setVisibility(0);
                SelectFileTool.this.mListView.setVisibility(8);
                SelectFileTool.this.mPathList.clear();
                SelectFileTool.this.mPathList.add("mnt");
                SelectFileTool.this.setPath();
                SelectFileTool.this.mStorageInfoList.clear();
                if (SelectFileTool.this.mSotrageAdapter != null) {
                    SelectFileTool.this.mSotrageAdapter.notifyDataSetChanged();
                }
                ArrayList<FileInfo> fileInfo_List = new ArrayList();
                fileInfo_List.add(new FileInfo(null, "-17", FileTypeManager.open_type_back, SelectFileTool.this.mContext.getString(R.drawable.bg)));
                SelectFileTool.this.mStorageInfoList.add(new FileObject(0, "/mnt", fileInfo_List));
            }

            public void onInitScanEnd() {
                SelectFileTool.this.mLoadingView.setVisibility(8);
                SelectFileTool.this.mListView.setVisibility(0);
                SelectFileTool.this.setAdapter(((FileObject) SelectFileTool.this.mStorageInfoList.get(0)).getFileInfo_List(), 0);
                SelectFileTool.this.isScanCompelet = true;
            }

            public void onInitScan(String path, int storageType) {
                ((FileObject) SelectFileTool.this.mStorageInfoList.get(0)).getFileInfo_List().add(SelectFileTool.this.getDeviceInfo(path, storageType));
            }

            public void onExitStorage(String path, int storageType) {
                Log.v("bob", "onExitStorage = " + path);
                FileObject fileListObject = (FileObject) SelectFileTool.this.mStorageInfoList.get(0);
                int size = fileListObject.getFileInfo_List().size();
                for (int i = 0; i < size; i++) {
                    if (((FileInfo) fileListObject.getFileInfo_List().get(i)).getPath().equals(path)) {
                        fileListObject.getFileInfo_List().remove(i);
                        if (fileListObject.getCurrentIndex() == i) {
                            fileListObject.setCurrentIndex(0);
                            SelectFileTool.this.mStorageInfoList.clear();
                            SelectFileTool.this.mStorageInfoList.add(fileListObject);
                            SelectFileTool.this.setAdapter(fileListObject.getFileInfo_List(), 0);
                            return;
                        } else if (SelectFileTool.this.mSotrageAdapter != null) {
                            SelectFileTool.this.mSotrageAdapter.notifyDataSetChanged();
                            return;
                        } else {
                            return;
                        }
                    }
                }
            }

            public void onAddStorage(String path, int storageType) {
                Log.v("bob", "onAddStorage = " + path);
                int size = ((FileObject) SelectFileTool.this.mStorageInfoList.get(0)).getFileInfo_List().size();
                int i = 0;
                while (i < size) {
                    if (!((FileInfo) ((FileObject) SelectFileTool.this.mStorageInfoList.get(0)).getFileInfo_List().get(i)).getPath().equals(path)) {
                        i++;
                    } else {
                        return;
                    }
                }
                ((FileObject) SelectFileTool.this.mStorageInfoList.get(0)).getFileInfo_List().add(SelectFileTool.this.getDeviceInfo(path, storageType));
                if (SelectFileTool.this.mSotrageAdapter != null) {
                    SelectFileTool.this.mSotrageAdapter.notifyDataSetChanged();
                }
            }
        });
        this.mZidooStorageTool.startScanStorage();
    }

    public void release() {
        mZidooSelectFileTool = null;
        this.mZidooStorageTool.release();
    }

    private ArrayList<FileInfo> scanFile(String path) {
        ArrayList<FileInfo> fileInfo_List = new ArrayList();
        File file = new File(path);
        if (file != null && file.exists()) {
            File[] file_list = file.listFiles();
            if (file_list != null) {
                int file_size = file_list.length;
                ArrayList<FileInfo> file_FileInfo_List = new ArrayList();
                ArrayList<FileInfo> ota_FileInfo_List = new ArrayList();
                int j = 0;
                while (j < file_size) {
                    try {
                        File file_list_file = file_list[j];
                        if (!(file_list_file.getName().contains("$") || file_list_file.getName().equals("LOST.DIR") || file_list_file.getName().substring(0, 1).equals(".") || !file_list_file.canRead())) {
                            if (file_list_file.isDirectory()) {
                                file_FileInfo_List.add(new FileInfo(file_list_file, file_list_file.getAbsolutePath(), FileTypeManager.open_type_file, file_list_file.getName()));
                            } else if (this.mSelectMode != 1) {
                                String type = FileTypeManager.getFileType(file_list_file);
                                if (this.mSelectFileType.equals(FileTypeManager.open_type_file)) {
                                    ota_FileInfo_List.add(new FileInfo(file_list_file, file_list_file.getAbsolutePath(), type, file_list_file.getName()));
                                } else if (this.mSelectFileType.equals(type)) {
                                    ota_FileInfo_List.add(new FileInfo(file_list_file, file_list_file.getAbsolutePath(), type, file_list_file.getName()));
                                }
                            } else {
                                continue;
                            }
                        }
                        j++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                fileInfo_List.addAll(ota_FileInfo_List);
                fileInfo_List.addAll(file_FileInfo_List);
            }
        }
        if (fileInfo_List.size() == 0) {
            fileInfo_List.add(new FileInfo(null, "-17", FileTypeManager.open_type_back, this.mContext.getString(R.drawable.bg)));
        } else {
            fileInfo_List.add(0, new FileInfo(null, "-17", FileTypeManager.open_type_back, this.mContext.getString(R.drawable.bg)));
        }
        return fileInfo_List;
    }
}
