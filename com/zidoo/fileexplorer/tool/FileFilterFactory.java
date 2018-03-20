package com.zidoo.fileexplorer.tool;

import java.io.File;
import java.io.FileFilter;
import zidoo.file.FileType;

public class FileFilterFactory {

    public static class ANDgateFilter implements FileFilter {
        FileFilter x;
        FileFilter y;

        public ANDgateFilter(FileFilter x, FileFilter y) {
            this.x = x;
            this.y = y;
        }

        public boolean accept(File file) {
            return this.x.accept(file) && this.y.accept(file);
        }
    }

    public static final class CustomFilter implements FileFilter {
        private String[] exts;

        public CustomFilter(String[] exts) {
            this.exts = exts;
        }

        public boolean accept(File file) {
            String name = file.getName();
            int e = name.lastIndexOf(".");
            if (e == -1) {
                return false;
            }
            String ext = name.substring(e + 1);
            for (String s : this.exts) {
                if (s.equals(ext)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class MultiFilter implements FileFilter {
        int type;

        public MultiFilter(int type) {
            this.type = type;
        }

        public boolean accept(File file) {
            return ((1 << FileType.getType(file)) & this.type) != 0;
        }
    }

    public static class NOTFilter implements FileFilter {
        FileFilter src;

        public NOTFilter(FileFilter src) {
            this.src = src;
        }

        public boolean accept(File file) {
            return !this.src.accept(file);
        }
    }

    public static class ORFilter implements FileFilter {
        FileFilter x;
        FileFilter y;

        public ORFilter(FileFilter x, FileFilter y) {
            this.x = x;
            this.y = y;
        }

        public boolean accept(File file) {
            return this.x.accept(file) || this.y.accept(file);
        }
    }

    public static FileFilter obtainCombinationFilter(FileFilter x, FileFilter y, boolean and) {
        return and ? new ANDgateFilter(x, y) : new ORFilter(x, y);
    }

    public static FileFilter obtainReadableFilter() {
        return new FileFilter() {
            public boolean accept(File file) {
                return file.canRead();
            }
        };
    }

    public static FileFilter obtainDisHiddenFilter() {
        return new FileFilter() {
            public boolean accept(File file) {
                return !file.getName().startsWith(".");
            }
        };
    }

    public static FileFilter obtainMultiFilter(int type) {
        return new MultiFilter(type);
    }

    public static FileFilter obtainNotFilter(FileFilter src) {
        return new NOTFilter(src);
    }

    public static FileFilter obtainSingleFilter(int type) {
        switch (type) {
            case 0:
                return new FileFilter() {
                    public boolean accept(File file) {
                        return file.isDirectory();
                    }
                };
            case 1:
                return new FileFilter() {
                    public boolean accept(File file) {
                        return FileType.isAudioFile(file);
                    }
                };
            case 2:
                return new FileFilter() {
                    public boolean accept(File file) {
                        return FileType.isMovieFile(file);
                    }
                };
            case 3:
                return new FileFilter() {
                    public boolean accept(File file) {
                        return FileType.isPictureFile(file.getName());
                    }
                };
            case 4:
                return new FileFilter() {
                    public boolean accept(File file) {
                        return FileType.isTxtFile(file.getName());
                    }
                };
            case 5:
                return new FileFilter() {
                    public boolean accept(File file) {
                        return FileType.isApkFile(file.getName());
                    }
                };
            case 6:
                return new FileFilter() {
                    public boolean accept(File file) {
                        return FileType.isPdfFile(file.getName());
                    }
                };
            case 7:
                return new FileFilter() {
                    public boolean accept(File file) {
                        return FileType.isWordFile(file.getName());
                    }
                };
            case 8:
                return new FileFilter() {
                    public boolean accept(File file) {
                        return FileType.isExcelFile(file.getName());
                    }
                };
            case 9:
                return new FileFilter() {
                    public boolean accept(File file) {
                        return FileType.isPptFile(file.getName());
                    }
                };
            case 10:
                return new FileFilter() {
                    public boolean accept(File file) {
                        return FileType.isHtmlFile(file.getName());
                    }
                };
            case 11:
                return new FileFilter() {
                    public boolean accept(File file) {
                        return FileType.isZipFile(file.getName());
                    }
                };
            case 12:
                return new FileFilter() {
                    public boolean accept(File file) {
                        return FileType.getType(file) == 12;
                    }
                };
            default:
                return new FileFilter() {
                    public boolean accept(File file) {
                        return file.isDirectory();
                    }
                };
        }
    }
}
