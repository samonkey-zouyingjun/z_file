package pers.lic.tool.db;

import android.content.Context;
import android.content.ContextWrapper;
import java.io.File;

public class DatabaseContext extends ContextWrapper {
    private String path;

    public DatabaseContext(Context base, String path) {
        super(base);
        this.path = path;
    }

    public File getDatabasePath(String name) {
        return new File(this.path);
    }
}
