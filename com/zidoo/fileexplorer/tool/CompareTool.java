package com.zidoo.fileexplorer.tool;

import com.zidoo.fileexplorer.bean.Favorite;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import pers.lic.tool.PinyinCompareTool;
import zidoo.file.FileType;

public class CompareTool {
    public static void sortByName(File[] filelists, final boolean increase) {
        Arrays.sort(filelists, new Comparator<File>() {
            public int compare(File lf, File rf) {
                return lf.isDirectory() ? rf.isDirectory() ? CompareTool.compareCharSequence(lf.getName(), rf.getName(), increase) : -1 : rf.isDirectory() ? 1 : CompareTool.compareCharSequence(lf.getName(), rf.getName(), increase);
            }
        });
    }

    public static void sortBySize(File[] filelists, final boolean increase) {
        Arrays.sort(filelists, new Comparator<File>() {
            public int compare(File lf, File rf) {
                if (lf.isDirectory()) {
                    return rf.isDirectory() ? CompareTool.compareCharSequence(lf.getName(), rf.getName(), increase) : -1;
                } else {
                    if (rf.isDirectory()) {
                        return 1;
                    }
                    if ((lf.length() < rf.length()) == increase) {
                        return -1;
                    }
                    return 1;
                }
            }
        });
    }

    public static void sortByDate(File[] filelists, final boolean increase) {
        new HanyuPinyinOutputFormat().setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        Arrays.sort(filelists, new Comparator<File>() {
            public int compare(File lf, File rf) {
                boolean z = false;
                if (lf.isDirectory()) {
                    if (!rf.isDirectory()) {
                        return -1;
                    }
                    if (lf.lastModified() < rf.lastModified()) {
                        z = true;
                    }
                    return z == increase ? -1 : 1;
                } else if (rf.isDirectory()) {
                    return 1;
                } else {
                    if (lf.lastModified() < rf.lastModified()) {
                        z = true;
                    }
                    if (z == increase) {
                        return -1;
                    }
                    return 1;
                }
            }
        });
    }

    public static void sortByType(File[] filelists, final boolean increase) {
        new HanyuPinyinOutputFormat().setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        Arrays.sort(filelists, new Comparator<File>() {
            public int compare(File lf, File rf) {
                if (lf.isDirectory()) {
                    return rf.isDirectory() ? CompareTool.compareCharSequence(lf.getName(), rf.getName(), increase) : -1;
                } else {
                    if (rf.isDirectory()) {
                        return 1;
                    }
                    return increase ? FileType.getType(lf) - FileType.getType(rf) : FileType.getType(rf) - FileType.getType(lf);
                }
            }
        });
    }

    static int compareFirstChar(File lf, File rf, boolean increase) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        int l = getFirstCharAscall(lf.getName().charAt(0), format);
        int r = getFirstCharAscall(rf.getName().charAt(0), format);
        if (96 < l && l < 123) {
            l -= 32;
        }
        if (r > 96 && r < 123) {
            r -= 32;
        }
        return increase ? l - r : r - l;
    }

    private static int compareCharSequence(String ln, String rn, boolean increase) {
        int result = PinyinCompareTool.compareCharSequence(ln, rn);
        return increase ? result : -result;
    }

    int compareName(String ln, String rn, boolean increase) {
        int length = ln.length() < rn.length() ? ln.length() : rn.length();
        for (int i = 0; i < length; i++) {
            if (ln.charAt(i) != rn.charAt(i)) {
                int result = compareChar(ln.charAt(i), rn.charAt(i), increase);
                if (result != 0) {
                    return result;
                }
            }
        }
        return increase ? ln.length() - rn.length() : rn.length() - ln.length();
    }

    private static int compareChar(char lc, char rc, boolean increase) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        String[] lp = null;
        String[] rp = null;
        try {
            lp = PinyinHelper.toHanyuPinyinStringArray(lc, format);
            rp = PinyinHelper.toHanyuPinyinStringArray(rc, format);
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        if (lp == null && rp == null) {
            boolean b = false;
            int lt = lc;
            int rt = rc;
            if ('`' < lt && lt < '{') {
                b = true;
                lt -= 32;
            }
            if ('`' < rt && rt < '{') {
                b = true;
                rt -= 32;
            }
            int result = lt - rt;
            if (result != 0 || !b) {
                if (!increase) {
                    result = -result;
                }
                return result;
            } else if (increase) {
                return rc - lc;
            } else {
                return lc - rc;
            }
        } else if (lp != null && rp == null) {
            return 1;
        } else {
            if (lp == null && rp != null) {
                return -1;
            }
            String ls = lp[0];
            String rs = rp[0];
            int length = ls.length() < rs.length() ? ls.length() : rs.length();
            int i = 0;
            while (i < length) {
                if (ls.charAt(i) == rs.charAt(i)) {
                    i++;
                } else if (increase) {
                    return ls.charAt(i) - rs.charAt(i);
                } else {
                    return rs.charAt(i) - ls.charAt(i);
                }
            }
            return increase ? ls.length() - rs.length() : rs.length() - ls.length();
        }
    }

    @Deprecated
    public static void sortByNamexxx(String[] filelists, final boolean increase) {
        final HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        Arrays.sort(filelists, new Comparator<String>() {
            public int compare(String lf, String rf) {
                int l = CompareTool.getFirstCharAscall(lf.charAt(0), format);
                int r = CompareTool.getFirstCharAscall(rf.charAt(0), format);
                if (96 < l && l < 123) {
                    l -= 32;
                }
                if (r > 96 && r < 123) {
                    r -= 32;
                }
                return increase ? l - r : r - l;
            }
        });
    }

    @Deprecated
    public static void sortByDatexxx(String[] filelists, final boolean increase) {
        new HanyuPinyinOutputFormat().setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        Arrays.sort(filelists, new Comparator<String>() {
            public int compare(String lp, String rp) {
                File lf = new File(lp);
                File rf = new File(rp);
                if (increase) {
                    if (lf.lastModified() - rf.lastModified() > 0) {
                        return 1;
                    }
                    return -1;
                } else if (rf.lastModified() - lf.lastModified() <= 0) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }

    private static int getFirstCharAscall(char c, HanyuPinyinOutputFormat format) {
        try {
            String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
            if (pinyin != null) {
                return pinyin[0].charAt(0);
            }
            return c;
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
            return '\u0000';
        }
    }

    public static void sortByDate(String[] fileNameLists, final boolean increase, final String path) {
        new HanyuPinyinOutputFormat().setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        Arrays.sort(fileNameLists, new Comparator<String>() {
            public int compare(String lp, String rp) {
                File lf = new File(path + "/" + lp);
                File rf = new File(path + "/" + rp);
                if (increase) {
                    if (lf.lastModified() - rf.lastModified() > 0) {
                        return 1;
                    }
                    return -1;
                } else if (rf.lastModified() - lf.lastModified() <= 0) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }

    public static void sortByName(Favorite[] favorites, final boolean increase) {
        Arrays.sort(favorites, new Comparator<Favorite>() {
            public int compare(Favorite lhs, Favorite rhs) {
                int lft = lhs.getFileType();
                int rft = rhs.getFileType();
                if (lft == rft || (lft > 0 && rft > 0)) {
                    return CompareTool.compareCharSequence(lhs.getName(), rhs.getName(), increase);
                }
                return lft - rft;
            }
        });
    }

    public static void sortBySize(Favorite[] favorites, final boolean increase) {
        Arrays.sort(favorites, new Comparator<Favorite>() {
            public int compare(Favorite lhs, Favorite rhs) {
                int lft = lhs.getFileType();
                int rft = rhs.getFileType();
                if (lft > 0 && rft > 0) {
                    if (((lhs.getFileLength() > rhs.getFileLength() ? 1 : 0) ^ increase) != 0) {
                        return -1;
                    }
                    return 1;
                } else if (lft == rft) {
                    return CompareTool.compareCharSequence(lhs.getName(), rhs.getName(), increase);
                } else {
                    return lft - rft;
                }
            }
        });
    }

    public static void sortByDate(Favorite[] favorites, boolean increase) {
        Arrays.sort(favorites, new Comparator<Favorite>() {
            public int compare(Favorite lhs, Favorite rhs) {
                int lft = lhs.getFileType();
                int rft = rhs.getFileType();
                if (lft == rft || (lft > 0 && rft > 0)) {
                    return lhs.getId() - rhs.getId();
                }
                return lft - rft;
            }
        });
    }

    public static void sortByType(Favorite[] favorites, final boolean increase) {
        Arrays.sort(favorites, new Comparator<Favorite>() {
            public int compare(Favorite lhs, Favorite rhs) {
                int lft = lhs.getFileType();
                int rft = rhs.getFileType();
                if (lft > 0 && rft > 0) {
                    return increase ? lft - rft : rft - lft;
                } else {
                    if (lft == rft) {
                        return CompareTool.compareCharSequence(lhs.getName(), rhs.getName(), increase);
                    }
                    return lft - rft;
                }
            }
        });
    }
}
