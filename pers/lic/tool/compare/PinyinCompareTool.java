package pers.lic.tool.compare;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PinyinCompareTool {
    private static final int END = 40869;
    private static final int[] NUMBERS = new int[20902];
    private static final int START = 19968;

    static {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(PinyinCompareTool.class.getClassLoader().getResourceAsStream("assets/index.txt")));
            int n = 0;
            while (true) {
                String line = reader.readLine();
                if (line != null) {
                    String[] ss = line.split(" ");
                    int length = ss.length;
                    int i = 0;
                    int n2 = n;
                    while (i < length) {
                        n = n2 + 1;
                        NUMBERS[n2] = Integer.parseInt(ss[i]);
                        i++;
                        n2 = n;
                    }
                    n = n2;
                } else {
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
    }

    public static int compareCharSequence(CharSequence lc, CharSequence rc) {
        int length = lc.length() < rc.length() ? lc.length() : rc.length();
        for (int i = 0; i < length; i++) {
            if (lc.charAt(i) != rc.charAt(i)) {
                return compareChar(lc.charAt(i), rc.charAt(i));
            }
        }
        return lc.length() - rc.length();
    }

    public static int compareChar(char lc, char rc) {
        int lt = getPinyinIndex(lc);
        int rt = getPinyinIndex(rc);
        if (lt != rt) {
            return lt - rt;
        }
        lt = lc;
        rt = rc;
        boolean b = false;
        if ('`' < lt && lt < '{') {
            b = true;
            lt -= 32;
        }
        if ('`' < rt && rt < '{') {
            b = true;
            rt -= 32;
        }
        int result = lt - rt;
        if (result == 0 && b) {
            return rc - lc;
        }
        return result;
    }

    private static int getPinyinIndex(char c) {
        return (c < '一' || c > '龥') ? -1 : NUMBERS[c - 19968];
    }
}
