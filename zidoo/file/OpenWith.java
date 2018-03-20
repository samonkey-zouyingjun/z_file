package zidoo.file;

import android.content.Intent;
import java.io.File;

public interface OpenWith {
    Intent getBDMVOpenWith(File file);

    Intent getOpenWith(File file);

    boolean isSupportBDMV();

    void openBDMV(File file);

    void openFile(File file);
}
