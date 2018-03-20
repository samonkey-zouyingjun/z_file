package org.mediainfo.android;

public class MediaParser {
    public static int isCanceled = 0;

    public enum InfoKind {
        NAME,
        TEXT,
        MEASURE,
        OPTIONS,
        NAME_TEXT,
        MEASURE_TEXT,
        INFO,
        HOWTO,
        DOMAIN,
        MAX
    }

    public enum StreamKind {
        GENERAL,
        VIDEO,
        AUDIO,
        TEXT,
        OTHER,
        IMAGE,
        MENU,
        MAX
    }

    private native int countGet(String str, int i, int i2);

    private native String getById(String str, int i, int i2, int i3);

    private native String getByIdDetail(String str, int i, int i2, int i3, int i4);

    private native String getByName(String str, int i, int i2, String str2);

    private native String getByNameDetail(String str, int i, int i2, String str2, int i3, int i4);

    private native String getMediaInfo(String str);

    private native String getMediaInfoOption(String str);

    static {
        System.loadLibrary("mediainfo");
    }

    public MediaParser() {
        System.out.println("MediaParser created");
    }

    public int getIsCanceled() {
        return isCanceled;
    }

    public String get(String filename, StreamKind streamKind, int streamNum, int parameter) {
        return getById(filename, streamKind.ordinal(), streamNum, parameter);
    }

    public String test(String filename) {
        return getMediaInfo(filename);
    }

    public String get(String filename, StreamKind streamKind, int streamNum, int parameter, InfoKind infoKind) {
        return getByIdDetail(filename, streamKind.ordinal(), streamNum, parameter, infoKind.ordinal());
    }

    public String get(String filename, StreamKind streamKind, int streamNum, String parameter) {
        return getByName(filename, streamKind.ordinal(), streamNum, parameter);
    }

    public String get(String filename, StreamKind streamKind, int streamNum, String parameter, InfoKind infoKind) {
        return getByNameDetail(filename, streamKind.ordinal(), streamNum, parameter, infoKind.ordinal(), InfoKind.NAME.ordinal());
    }

    public String get(String filename, StreamKind streamKind, int streamNum, String parameter, InfoKind infoKind, InfoKind searchKind) {
        return getByNameDetail(filename, streamKind.ordinal(), streamNum, parameter, infoKind.ordinal(), searchKind.ordinal());
    }

    public int countGet(String filename, StreamKind streamKind) {
        return countGet(filename, streamKind.ordinal(), -1);
    }

    public int countGet(String filename, StreamKind streamKind, int streamNumber) {
        return countGet(filename, streamKind.ordinal(), streamNumber);
    }

    public String getMI(String filename) {
        return getMediaInfo(filename);
    }

    public String getMIOption(String param) {
        return getMediaInfoOption(param);
    }
}
