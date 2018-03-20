package com.zidoo.custom.usb;

import java.io.File;

public class FileTypeManager {
    public static final String open_type_apk = "apk";
    public static final String open_type_back = "back";
    public static final String open_type_excel = "excel";
    public static final String open_type_file = "file";
    public static final String open_type_html = "html";
    public static final String open_type_movie = "movie";
    public static final String open_type_music = "music";
    public static final String open_type_ota = "ota";
    public static final String open_type_other = "other";
    public static final String open_type_pdf = "pdf";
    public static final String open_type_pic = "pic";
    public static final String open_type_ppt = "ppt";
    public static final String open_type_subtitile = "subtitile";
    public static final String open_type_txt = "txt";
    public static final String open_type_word = "word";
    public static final String open_type_zip = "zip";

    public static String getFileType(File file) {
        if (file.isDirectory()) {
            return open_type_file;
        }
        String path = file.getAbsolutePath();
        if (isMusicFile(path)) {
            return open_type_music;
        }
        if (isMovieFile(path)) {
            return open_type_movie;
        }
        if (isPictureFile(path)) {
            return open_type_pic;
        }
        if (isTxtFile(path)) {
            return open_type_txt;
        }
        if (isPdfFile(path)) {
            return open_type_pdf;
        }
        if (isWordFile(path)) {
            return open_type_word;
        }
        if (isExcelFile(path)) {
            return open_type_excel;
        }
        if (isPptFile(path)) {
            return open_type_ppt;
        }
        if (isHtml32File(path)) {
            return open_type_html;
        }
        if (isApkFile(path)) {
            return open_type_apk;
        }
        if (isZIPFile(path)) {
            return open_type_zip;
        }
        if (isOtaFile(path)) {
            return open_type_ota;
        }
        if (isSubtitileFile(path)) {
            return open_type_subtitile;
        }
        return open_type_other;
    }

    public static boolean isMusicFile(String path) {
        try {
            String ext = path.substring(path.lastIndexOf(".") + 1);
            if (ext.equalsIgnoreCase("mp3") || ext.equalsIgnoreCase("ogg") || ext.equalsIgnoreCase("wav") || ext.equalsIgnoreCase("wma") || ext.equalsIgnoreCase("m4a") || ext.equalsIgnoreCase("ape") || ext.equalsIgnoreCase("dts") || ext.equalsIgnoreCase("flac") || ext.equalsIgnoreCase("mp1") || ext.equalsIgnoreCase("mp2") || ext.equalsIgnoreCase("aac") || ext.equalsIgnoreCase("midi") || ext.equalsIgnoreCase("mid") || ext.equalsIgnoreCase("mp5") || ext.equalsIgnoreCase("mpga") || ext.equalsIgnoreCase("mpa") || ext.equalsIgnoreCase("m4p") || ext.equalsIgnoreCase("amr") || ext.equalsIgnoreCase("m4r")) {
                return true;
            }
            return false;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public static boolean isMovieFile(String path) {
        try {
            String ext = path.substring(path.lastIndexOf(".") + 1);
            if (ext.equalsIgnoreCase("avi") || ext.equalsIgnoreCase("wmv") || ext.equalsIgnoreCase("rmvb") || ext.equalsIgnoreCase("mkv") || ext.equalsIgnoreCase("m4v") || ext.equalsIgnoreCase("mov") || ext.equalsIgnoreCase("mpg") || ext.equalsIgnoreCase("rm") || ext.equalsIgnoreCase("flv") || ext.equalsIgnoreCase("pmp") || ext.equalsIgnoreCase("vob") || ext.equalsIgnoreCase("dat") || ext.equalsIgnoreCase("asf") || ext.equalsIgnoreCase("psr") || ext.equalsIgnoreCase("3gp") || ext.equalsIgnoreCase("mpeg") || ext.equalsIgnoreCase("ram") || ext.equalsIgnoreCase("divx") || ext.equalsIgnoreCase("m4p") || ext.equalsIgnoreCase("m4b") || ext.equalsIgnoreCase("mp4") || ext.equalsIgnoreCase("f4v") || ext.equalsIgnoreCase("3gpp") || ext.equalsIgnoreCase("3g2") || ext.equalsIgnoreCase("3gpp2") || ext.equalsIgnoreCase("webm") || ext.equalsIgnoreCase("ts") || ext.equalsIgnoreCase("tp") || ext.equalsIgnoreCase("m2ts") || ext.equalsIgnoreCase("m2t") || ext.equalsIgnoreCase("lge") || ext.equalsIgnoreCase("3dv") || ext.equalsIgnoreCase("3dm") || ext.equalsIgnoreCase("iso")) {
                return true;
            }
            return false;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public static boolean isPictureFile(String path) {
        try {
            String ext = path.substring(path.lastIndexOf(".") + 1);
            if (ext.equalsIgnoreCase("png") || ext.equalsIgnoreCase("jpeg") || ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("gif") || ext.equalsIgnoreCase("bmp") || ext.equalsIgnoreCase("jfif") || ext.equalsIgnoreCase("tiff")) {
                return true;
            }
            return false;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public static boolean isTxtFile(String path) {
        try {
            String ext = path.substring(path.lastIndexOf(".") + 1);
            if (ext.equalsIgnoreCase(open_type_txt) || ext.equalsIgnoreCase("epub") || ext.equalsIgnoreCase("pdb") || ext.equalsIgnoreCase("fb2") || ext.equalsIgnoreCase("rtf")) {
                return true;
            }
            return false;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public static boolean isPdfFile(String path) {
        try {
            if (path.substring(path.lastIndexOf(".") + 1).equalsIgnoreCase(open_type_pdf)) {
                return true;
            }
            return false;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public static boolean isWordFile(String path) {
        try {
            String ext = path.substring(path.lastIndexOf(".") + 1);
            if (ext.equalsIgnoreCase("doc") || ext.equalsIgnoreCase("docx")) {
                return true;
            }
            return false;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public static boolean isExcelFile(String path) {
        try {
            String ext = path.substring(path.lastIndexOf(".") + 1);
            if (ext.equalsIgnoreCase("xls") || ext.equalsIgnoreCase("xlsx")) {
                return true;
            }
            return false;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public static boolean isPptFile(String path) {
        try {
            String ext = path.substring(path.lastIndexOf(".") + 1);
            if (ext.equalsIgnoreCase(open_type_ppt) || ext.equalsIgnoreCase("pptx")) {
                return true;
            }
            return false;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public static boolean isHtml32File(String path) {
        try {
            if (path.substring(path.lastIndexOf(".") + 1).equalsIgnoreCase(open_type_html)) {
                return true;
            }
            return false;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public static boolean isApkFile(String path) {
        try {
            if (path.substring(path.lastIndexOf(".") + 1).equalsIgnoreCase(open_type_apk)) {
                return true;
            }
            return false;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public static boolean isZIPFile(String path) {
        try {
            String ext = path.substring(path.lastIndexOf(".") + 1);
            if (ext.equalsIgnoreCase(open_type_zip) || ext.equalsIgnoreCase("rar")) {
                return true;
            }
            return false;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public static boolean isOtaFile(String path) {
        try {
            if (path.substring(path.lastIndexOf(".") + 1).equalsIgnoreCase(open_type_zip)) {
                return true;
            }
            return false;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public static boolean isSubtitileFile(String path) {
        try {
            String ext = path.substring(path.lastIndexOf(".") + 1);
            if (ext.equalsIgnoreCase("srt") || ext.equalsIgnoreCase("smi") || ext.equalsIgnoreCase("ass") || ext.equalsIgnoreCase("ssa") || ext.equalsIgnoreCase("sub") || ext.equalsIgnoreCase("idx") || ext.equalsIgnoreCase("sami") || ext.equalsIgnoreCase(open_type_txt)) {
                return true;
            }
            return false;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public static boolean isBDMV(String path) {
        File bdmv = new File(new StringBuilder(String.valueOf(path)).append("/BDMV/STREAM").toString());
        if ((bdmv.exists() && bdmv.isDirectory()) || new File(path, "VIDEO_TS.IFO").exists()) {
            return true;
        }
        return new File(path, "VIDEO_TS/VIDEO_TS.IFO").exists();
    }

    public static boolean isIso(String path) {
        try {
            if (path.substring(path.lastIndexOf(".") + 1).equalsIgnoreCase("iso")) {
                return true;
            }
            return false;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }
}
