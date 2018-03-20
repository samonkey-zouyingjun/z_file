package zidoo.browse;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import zidoo.tarot.kernel.input.DefaultGestureRecogniser;

public class BrowseBuilder {
    private Activity activity;
    private int bgdResid = -1;
    private int clickMode = 0;
    private String[] customExtras = null;
    private int devices = 31;
    private int filter = 0;
    private int flag = 1;
    private int help = 2;
    private int horizontalPadding = 17;
    private String initialPath = null;
    private String name = "All";
    private int requestCode = 0;
    private float scale = 0.8f;
    private int shade = BrowseConstant.SHADE_DEFAULT;
    private int targets = 1;
    private String title = "Browse Files";
    private int version = 2;
    private int verticalPadding = 16;

    public BrowseBuilder(Activity activity) {
        this.activity = activity;
    }

    public BrowseBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public BrowseBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public BrowseBuilder setDevices(int devices) {
        this.devices = devices;
        return this;
    }

    public BrowseBuilder setFilter(int filter) {
        this.filter = filter;
        return this;
    }

    public BrowseBuilder setCustomExtras(String[] customExtras) {
        this.customExtras = customExtras;
        return this;
    }

    public BrowseBuilder setTargets(int targets) {
        this.targets = targets;
        return this;
    }

    public BrowseBuilder setFlag(int flag) {
        this.flag = flag;
        return this;
    }

    public BrowseBuilder setBgdResid(int bgdResid) {
        this.bgdResid = bgdResid;
        return this;
    }

    public BrowseBuilder setHelp(int help) {
        this.help = help;
        return this;
    }

    public BrowseBuilder setScale(float scale) {
        this.scale = scale;
        return this;
    }

    public BrowseBuilder setShade(int shade) {
        this.shade = shade;
        return this;
    }

    public BrowseBuilder setHorizontalPadding(int horizontalPadding) {
        this.horizontalPadding = horizontalPadding;
        return this;
    }

    public BrowseBuilder setVerticalPadding(int verticalPadding) {
        this.verticalPadding = verticalPadding;
        return this;
    }

    public BrowseBuilder setRequestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    public BrowseBuilder setInitialPath(String path) {
        this.initialPath = path;
        return this;
    }

    public BrowseBuilder setClickMode(int clickMode) {
        this.clickMode = clickMode;
        return this;
    }

    public BrowseBuilder setVersion(int version) {
        this.version = version;
        return this;
    }

    public boolean browse() {
        try {
            Intent intent = generateIntent();
            if (intent != null) {
                this.activity.startActivityForResult(intent, this.requestCode);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Intent generateIntent() {
        try {
            PackageInfo pkgInfo = this.activity.getPackageManager().getPackageInfo("com.zidoo.fileexplorer", 0);
            if (pkgInfo != null && pkgInfo.versionCode > 100) {
                String action = (this.version != 3 || pkgInfo.versionCode < DefaultGestureRecogniser.SHOW_PRESS_TIME) ? BrowseConstant.FILE_EXPLORER_ACTION : BrowseConstant.FILE_BROWSE_ACTION;
                Intent intent = new Intent(action);
                intent.putExtra(BrowseConstant.EXTRA_TITLE, this.title);
                intent.putExtra("name", this.name);
                intent.putExtra(BrowseConstant.EXTRA_DEVICE, this.devices);
                intent.putExtra(BrowseConstant.EXTRA_FILTER, this.filter);
                intent.putExtra(BrowseConstant.EXTRA_CUSTOM_EXTRAS, this.customExtras);
                intent.putExtra(BrowseConstant.EXTRA_TARGET, this.targets);
                intent.putExtra(BrowseConstant.EXTRA_FLAG, this.flag);
                intent.putExtra(BrowseConstant.EXTRA_PACKAGE_NAME, this.activity.getPackageName());
                intent.putExtra(BrowseConstant.EXTRA_BGD, this.bgdResid);
                intent.putExtra(BrowseConstant.EXTRA_HELP, this.help);
                intent.putExtra(BrowseConstant.EXTRA_SCALE, this.scale);
                intent.putExtra(BrowseConstant.EXTRA_SHADE, this.shade);
                intent.putExtra(BrowseConstant.EXTRA_HORIZONTAL_PADDING, this.horizontalPadding);
                intent.putExtra(BrowseConstant.EXTRA_VERTICAL_PADDING, this.verticalPadding);
                intent.putExtra(BrowseConstant.EXTRA_IDENTIFIER, this.initialPath);
                intent.putExtra(BrowseConstant.EXTRA_CLICK_MODEL, this.clickMode);
                intent.putExtra(BrowseConstant.EXTRA_VERSION, this.version);
                intent.addFlags(32768);
                return intent;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
