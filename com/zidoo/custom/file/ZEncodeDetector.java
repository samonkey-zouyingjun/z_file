package com.zidoo.custom.file;

import android.support.v4.app.FrameMetricsAggregator;
import android.support.v4.view.InputDeviceCompat;
import com.umeng.common.util.e;
import com.zidoo.fileexplorer.BuildConfig;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import jcifs.https.Handler;
import jcifs.netbios.NbtException;
import jcifs.smb.WinError;
import zidoo.http.HTTPStatus;
import zidoo.tarot.kernel.input.DefaultGestureRecogniser;
import zidoo.tarot.kernel.shader.ShaderParameter;

public class ZEncodeDetector {
    static final int ASCII = 1;
    static final int BIG5 = 6;
    static final int EUC_TW = 7;
    static final int GB2312 = 0;
    static final int GBK = 4;
    static final int HZ = 5;
    static final int ISO_2022_CN = 8;
    static final int TOTAL_ENCODINGS = 9;
    static final int UNICODE = 3;
    static final int UTF8 = 2;
    public static String[] codings;
    int[][] Big5Freq;
    int[][] EUC_TWFreq;
    int[][] GBFreq;
    int[][] GBKFreq;

    public static String getCharset(File file) {
        String charset = "GBK";
        switch (new ZEncodeDetector().detectEncoding(file, 8192)) {
            case 0:
                return "GB2312";
            case 1:
                return "ASCII";
            case 2:
                return e.f;
            case 3:
                return "Unicode";
            case 4:
                return "GBK";
            case 5:
                return "HZ";
            case 6:
                return "BIG5";
            case 7:
                return "CNS11643";
            case 8:
                return "ISO2022CN";
            default:
                return charset;
        }
    }

    public ZEncodeDetector() {
        initEncodingFormat();
        initializeFrequencies();
    }

    public void initEncodingFormat() {
        this.GBFreq = (int[][]) Array.newInstance(Integer.TYPE, new int[]{94, 94});
        this.GBKFreq = (int[][]) Array.newInstance(Integer.TYPE, new int[]{126, 191});
        this.Big5Freq = (int[][]) Array.newInstance(Integer.TYPE, new int[]{94, 158});
        this.EUC_TWFreq = (int[][]) Array.newInstance(Integer.TYPE, new int[]{94, 94});
        codings = new String[9];
        codings[0] = "GB2312";
        codings[4] = "GBK";
        codings[5] = "HZ";
        codings[6] = "BIG5";
        codings[7] = "CNS11643";
        codings[8] = "ISO2022CN";
        codings[2] = "UTF8";
        codings[3] = "Unicode";
        codings[1] = "ASCII";
    }

    public int detectEncoding(URL testurl) {
        byte[] rawtext = new byte[10000];
        int byteoffset = 0;
        try {
            InputStream chinesestream = testurl.openStream();
            while (true) {
                int bytesread = chinesestream.read(rawtext, byteoffset, rawtext.length - byteoffset);
                if (bytesread <= 0) {
                    chinesestream.close();
                    return detectEncoding(rawtext);
                }
                byteoffset += bytesread;
            }
        } catch (Exception e) {
            System.err.println("Error loading or using URL " + e.toString());
            return 0;
        }
    }

    public int detectEncoding(File file, int length) {
        byte[] rawtext = new byte[((int) Math.min((long) length, file.length()))];
        try {
            new FileInputStream(file).read(rawtext);
        } catch (Exception e) {
            System.err.println("Error: " + e);
        }
        return detectEncoding(rawtext);
    }

    public int detectEncoding(byte[] rawtext) {
        int maxscore = 0;
        int encoding_guess = 1;
        int[] scores = new int[]{gb2312_probability(rawtext), gbk_probability(rawtext), hz_probability(rawtext), big5_probability(rawtext), euc_tw_probability(rawtext), iso_2022_cn_probability(rawtext), utf8_probability(rawtext), utf16_probability(rawtext), ascii_probability(rawtext)};
        for (int index = 0; index < 9; index++) {
            if (scores[index] > maxscore) {
                encoding_guess = index;
                maxscore = scores[index];
            }
        }
        if (maxscore <= 50) {
            return 0;
        }
        return encoding_guess;
    }

    int gb2312_probability(byte[] rawtext) {
        int dbchars = 1;
        int gbchars = 1;
        long gbfreq = 0;
        long totalfreq = 1;
        int rawtextlen = rawtext.length;
        int i = 0;
        while (i < rawtextlen - 1) {
            if (rawtext[i] < (byte) 0) {
                dbchars++;
                if ((byte) -95 <= rawtext[i] && rawtext[i] <= (byte) -9 && (byte) -95 <= rawtext[i + 1] && rawtext[i + 1] <= (byte) -2) {
                    gbchars++;
                    totalfreq += 500;
                    int row = (rawtext[i] + 256) - 161;
                    int column = (rawtext[i + 1] + 256) - 161;
                    if (this.GBFreq[row][column] != 0) {
                        gbfreq += (long) this.GBFreq[row][column];
                    } else if (15 <= row && row < 55) {
                        gbfreq += 200;
                    }
                }
                i++;
            }
            i++;
        }
        return (int) ((50.0f * (((float) gbchars) / ((float) dbchars))) + (50.0f * (((float) gbfreq) / ((float) totalfreq))));
    }

    int gbk_probability(byte[] rawtext) {
        int dbchars = 1;
        int gbchars = 1;
        long gbfreq = 0;
        long totalfreq = 1;
        int rawtextlen = rawtext.length;
        int i = 0;
        while (i < rawtextlen - 1) {
            if (rawtext[i] < (byte) 0) {
                dbchars++;
                int row;
                int column;
                if ((byte) -95 <= rawtext[i] && rawtext[i] <= (byte) -9 && (byte) -95 <= rawtext[i + 1] && rawtext[i + 1] <= (byte) -2) {
                    gbchars++;
                    totalfreq += 500;
                    row = (rawtext[i] + 256) - 161;
                    column = (rawtext[i + 1] + 256) - 161;
                    if (this.GBFreq[row][column] != 0) {
                        gbfreq += (long) this.GBFreq[row][column];
                    } else if (15 <= row && row < 55) {
                        gbfreq += 200;
                    }
                } else if ((byte) -127 <= rawtext[i] && rawtext[i] <= (byte) -2 && ((Byte.MIN_VALUE <= rawtext[i + 1] && rawtext[i + 1] <= (byte) -2) || ((byte) 64 <= rawtext[i + 1] && rawtext[i + 1] <= (byte) 126))) {
                    gbchars++;
                    totalfreq += 500;
                    row = (rawtext[i] + 256) - 129;
                    if ((byte) 64 > rawtext[i + 1] || rawtext[i + 1] > (byte) 126) {
                        column = (rawtext[i + 1] + 256) - 128;
                    } else {
                        column = rawtext[i + 1] - 64;
                    }
                    if (this.GBKFreq[row][column] != 0) {
                        gbfreq += (long) this.GBKFreq[row][column];
                    }
                }
                i++;
            }
            i++;
        }
        return ((int) ((50.0f * (((float) gbchars) / ((float) dbchars))) + (50.0f * (((float) gbfreq) / ((float) totalfreq))))) - 1;
    }

    int hz_probability(byte[] rawtext) {
        float rangeval;
        int hzchars = 0;
        int dbchars = 1;
        long hzfreq = 0;
        long totalfreq = 1;
        int hzstart = 0;
        int hzend = 0;
        int rawtextlen = rawtext.length;
        int i = 0;
        while (i < rawtextlen) {
            if (rawtext[i] == (byte) 126) {
                if (rawtext[i + 1] == (byte) 123) {
                    hzstart++;
                    i += 2;
                    while (i < rawtextlen - 1 && rawtext[i] != (byte) 10 && rawtext[i] != (byte) 13) {
                        if (rawtext[i] == (byte) 126 && rawtext[i + 1] == (byte) 125) {
                            hzend++;
                            i++;
                            break;
                        }
                        int row;
                        int column;
                        if ((byte) 33 <= rawtext[i] && rawtext[i] <= (byte) 119 && (byte) 33 <= rawtext[i + 1] && rawtext[i + 1] <= (byte) 119) {
                            hzchars += 2;
                            row = rawtext[i] - 33;
                            column = rawtext[i + 1] - 33;
                            totalfreq += 500;
                            if (this.GBFreq[row][column] != 0) {
                                hzfreq += (long) this.GBFreq[row][column];
                            } else if (15 <= row && row < 55) {
                                hzfreq += 200;
                            }
                        } else if ((byte) -95 <= rawtext[i] && rawtext[i] <= (byte) -9 && (byte) -95 <= rawtext[i + 1] && rawtext[i + 1] <= (byte) -9) {
                            hzchars += 2;
                            row = (rawtext[i] + 256) - 161;
                            column = (rawtext[i + 1] + 256) - 161;
                            totalfreq += 500;
                            if (this.GBFreq[row][column] != 0) {
                                hzfreq += (long) this.GBFreq[row][column];
                            } else if (15 <= row && row < 55) {
                                hzfreq += 200;
                            }
                        }
                        dbchars += 2;
                        i += 2;
                    }
                } else if (rawtext[i + 1] == (byte) 125) {
                    hzend++;
                    i++;
                } else if (rawtext[i + 1] == (byte) 126) {
                    i++;
                }
            }
            i++;
        }
        if (hzstart > 4) {
            rangeval = 50.0f;
        } else if (hzstart > 1) {
            rangeval = 41.0f;
        } else if (hzstart > 0) {
            rangeval = 39.0f;
        } else {
            rangeval = 0.0f;
        }
        return (int) (rangeval + (50.0f * (((float) hzfreq) / ((float) totalfreq))));
    }

    int big5_probability(byte[] rawtext) {
        int dbchars = 1;
        int bfchars = 1;
        long bffreq = 0;
        long totalfreq = 1;
        int rawtextlen = rawtext.length;
        int i = 0;
        while (i < rawtextlen - 1) {
            if (rawtext[i] < (byte) 0) {
                dbchars++;
                if ((byte) -95 <= rawtext[i] && rawtext[i] <= (byte) -7 && (((byte) 64 <= rawtext[i + 1] && rawtext[i + 1] <= (byte) 126) || ((byte) -95 <= rawtext[i + 1] && rawtext[i + 1] <= (byte) -2))) {
                    int column;
                    bfchars++;
                    totalfreq += 500;
                    int row = (rawtext[i] + 256) - 161;
                    if ((byte) 64 > rawtext[i + 1] || rawtext[i + 1] > (byte) 126) {
                        column = (rawtext[i + 1] + 256) - 97;
                    } else {
                        column = rawtext[i + 1] - 64;
                    }
                    if (this.Big5Freq[row][column] != 0) {
                        bffreq += (long) this.Big5Freq[row][column];
                    } else if (3 <= row && row <= 37) {
                        bffreq += 200;
                    }
                }
                i++;
            }
            i++;
        }
        return (int) ((50.0f * (((float) bfchars) / ((float) dbchars))) + (50.0f * (((float) bffreq) / ((float) totalfreq))));
    }

    int euc_tw_probability(byte[] rawtext) {
        int dbchars = 1;
        int cnschars = 1;
        long cnsfreq = 0;
        long totalfreq = 1;
        int rawtextlen = rawtext.length;
        int i = 0;
        while (i < rawtextlen - 1) {
            if (rawtext[i] < (byte) 0) {
                dbchars++;
                if (i + 3 < rawtextlen && (byte) -114 == rawtext[i] && (byte) -95 <= rawtext[i + 1] && rawtext[i + 1] <= (byte) -80 && (byte) -95 <= rawtext[i + 2] && rawtext[i + 2] <= (byte) -2 && (byte) -95 <= rawtext[i + 3] && rawtext[i + 3] <= (byte) -2) {
                    cnschars++;
                    i += 3;
                } else if ((byte) -95 <= rawtext[i] && rawtext[i] <= (byte) -2 && (byte) -95 <= rawtext[i + 1] && rawtext[i + 1] <= (byte) -2) {
                    cnschars++;
                    totalfreq += 500;
                    int row = (rawtext[i] + 256) - 161;
                    int column = (rawtext[i + 1] + 256) - 161;
                    if (this.EUC_TWFreq[row][column] != 0) {
                        cnsfreq += (long) this.EUC_TWFreq[row][column];
                    } else if (35 <= row && row <= 92) {
                        cnsfreq += 150;
                    }
                    i++;
                }
            }
            i++;
        }
        return (int) ((50.0f * (((float) cnschars) / ((float) dbchars))) + (50.0f * (((float) cnsfreq) / ((float) totalfreq))));
    }

    int iso_2022_cn_probability(byte[] rawtext) {
        int dbchars = 1;
        int isochars = 1;
        long isofreq = 0;
        long totalfreq = 1;
        int rawtextlen = rawtext.length;
        int i = 0;
        while (i < rawtextlen - 1) {
            if (rawtext[i] == (byte) 27 && i + 3 < rawtextlen) {
                int row;
                int column;
                if (rawtext[i + 1] == (byte) 36 && rawtext[i + 2] == (byte) 41 && rawtext[i + 3] == (byte) 65) {
                    i += 4;
                    while (rawtext[i] != (byte) 27) {
                        dbchars++;
                        if ((byte) 33 <= rawtext[i] && rawtext[i] <= (byte) 119 && (byte) 33 <= rawtext[i + 1] && rawtext[i + 1] <= (byte) 119) {
                            isochars++;
                            row = rawtext[i] - 33;
                            column = rawtext[i + 1] - 33;
                            totalfreq += 500;
                            if (this.GBFreq[row][column] != 0) {
                                isofreq += (long) this.GBFreq[row][column];
                            } else if (15 <= row && row < 55) {
                                isofreq += 200;
                            }
                            i++;
                        }
                        i++;
                    }
                } else if (i + 3 < rawtextlen && rawtext[i + 1] == (byte) 36 && rawtext[i + 2] == (byte) 41 && rawtext[i + 3] == (byte) 71) {
                    i += 4;
                    while (rawtext[i] != (byte) 27) {
                        dbchars++;
                        if ((byte) 33 <= rawtext[i] && rawtext[i] <= (byte) 126 && (byte) 33 <= rawtext[i + 1] && rawtext[i + 1] <= (byte) 126) {
                            isochars++;
                            totalfreq += 500;
                            row = rawtext[i] - 33;
                            column = rawtext[i + 1] - 33;
                            if (this.EUC_TWFreq[row][column] != 0) {
                                isofreq += (long) this.EUC_TWFreq[row][column];
                            } else if (35 <= row && row <= 92) {
                                isofreq += 150;
                            }
                            i++;
                        }
                        i++;
                    }
                }
                if (rawtext[i] == (byte) 27 && i + 2 < rawtextlen && rawtext[i + 1] == (byte) 40 && rawtext[i + 2] == (byte) 66) {
                    i += 2;
                }
            }
            i++;
        }
        return (int) ((50.0f * (((float) isochars) / ((float) dbchars))) + (50.0f * (((float) isofreq) / ((float) totalfreq))));
    }

    int utf8_probability(byte[] rawtext) {
        int goodbytes = 0;
        int asciibytes = 0;
        int rawtextlen = rawtext.length;
        int i = 0;
        while (i < rawtextlen) {
            if ((rawtext[i] & 127) == rawtext[i]) {
                asciibytes++;
            } else if ((byte) -64 <= rawtext[i] && rawtext[i] <= (byte) -33 && i + 1 < rawtextlen && Byte.MIN_VALUE <= rawtext[i + 1] && rawtext[i + 1] <= (byte) -65) {
                goodbytes += 2;
                i++;
            } else if ((byte) -32 <= rawtext[i] && rawtext[i] <= (byte) -17 && i + 2 < rawtextlen && Byte.MIN_VALUE <= rawtext[i + 1] && rawtext[i + 1] <= (byte) -65 && Byte.MIN_VALUE <= rawtext[i + 2] && rawtext[i + 2] <= (byte) -65) {
                goodbytes += 3;
                i += 2;
            }
            i++;
        }
        if (asciibytes == rawtextlen) {
            return 0;
        }
        int score = (int) (100.0f * (((float) goodbytes) / ((float) (rawtextlen - asciibytes))));
        if (score > 98) {
            return score;
        }
        if (score <= 95 || goodbytes <= 30) {
            return 0;
        }
        return score;
    }

    int utf16_probability(byte[] rawtext) {
        if (((byte) -2 == rawtext[0] && (byte) -1 == rawtext[1]) || ((byte) -1 == rawtext[0] && (byte) -2 == rawtext[1])) {
            return 100;
        }
        return 0;
    }

    int ascii_probability(byte[] rawtext) {
        int score = 70;
        int rawtextlen = rawtext.length;
        for (int i = 0; i < rawtextlen; i++) {
            if (rawtext[i] < (byte) 0) {
                score -= 5;
            } else if (rawtext[i] == (byte) 27) {
                score -= 5;
            }
        }
        return score;
    }

    void initializeFrequencies() {
        int i;
        int j;
        for (i = 0; i < 93; i++) {
            for (j = 0; j < 93; j++) {
                this.GBFreq[i][j] = 0;
            }
        }
        for (i = 0; i < 126; i++) {
            for (j = 0; j < 191; j++) {
                this.GBKFreq[i][j] = 0;
            }
        }
        for (i = 0; i < 93; i++) {
            for (j = 0; j < 157; j++) {
                this.Big5Freq[i][j] = 0;
            }
        }
        for (i = 0; i < 93; i++) {
            for (j = 0; j < 93; j++) {
                this.EUC_TWFreq[i][j] = 0;
            }
        }
        this.GBFreq[20][35] = 599;
        this.GBFreq[49][26] = 598;
        this.GBFreq[41][38] = 597;
        this.GBFreq[17][26] = 596;
        this.GBFreq[32][42] = 595;
        this.GBFreq[39][42] = 594;
        this.GBFreq[45][49] = 593;
        this.GBFreq[51][57] = 592;
        this.GBFreq[50][47] = 591;
        this.GBFreq[42][90] = 590;
        this.GBFreq[52][65] = 589;
        this.GBFreq[53][47] = 588;
        this.GBFreq[19][82] = 587;
        this.GBFreq[31][19] = 586;
        this.GBFreq[40][46] = 585;
        this.GBFreq[24][89] = 584;
        this.GBFreq[23][85] = 583;
        this.GBFreq[20][28] = 582;
        this.GBFreq[42][20] = 581;
        this.GBFreq[34][38] = 580;
        this.GBFreq[45][9] = 579;
        this.GBFreq[54][50] = 578;
        this.GBFreq[25][44] = 577;
        this.GBFreq[35][66] = 576;
        this.GBFreq[20][55] = 575;
        this.GBFreq[18][85] = 574;
        this.GBFreq[20][31] = 573;
        this.GBFreq[49][17] = 572;
        this.GBFreq[41][16] = 571;
        this.GBFreq[35][73] = 570;
        this.GBFreq[20][34] = 569;
        this.GBFreq[29][44] = 568;
        this.GBFreq[35][38] = 567;
        this.GBFreq[49][9] = 566;
        this.GBFreq[46][33] = 565;
        this.GBFreq[49][51] = 564;
        this.GBFreq[40][89] = 563;
        this.GBFreq[26][64] = 562;
        this.GBFreq[54][51] = 561;
        this.GBFreq[54][36] = 560;
        this.GBFreq[39][4] = 559;
        this.GBFreq[53][13] = 558;
        this.GBFreq[24][92] = 557;
        this.GBFreq[27][49] = 556;
        this.GBFreq[48][6] = 555;
        this.GBFreq[21][51] = 554;
        this.GBFreq[30][40] = 553;
        this.GBFreq[42][92] = 552;
        this.GBFreq[31][78] = 551;
        this.GBFreq[25][82] = 550;
        this.GBFreq[47][0] = 549;
        this.GBFreq[34][19] = 548;
        this.GBFreq[47][35] = 547;
        this.GBFreq[21][63] = 546;
        this.GBFreq[43][75] = 545;
        this.GBFreq[21][87] = 544;
        this.GBFreq[35][59] = 543;
        this.GBFreq[25][34] = 542;
        this.GBFreq[21][27] = 541;
        this.GBFreq[39][26] = 540;
        this.GBFreq[34][26] = 539;
        this.GBFreq[39][52] = 538;
        this.GBFreq[50][57] = 537;
        this.GBFreq[37][79] = 536;
        this.GBFreq[26][24] = 535;
        this.GBFreq[22][1] = 534;
        this.GBFreq[18][40] = 533;
        this.GBFreq[41][33] = 532;
        this.GBFreq[53][26] = 531;
        this.GBFreq[54][86] = 530;
        this.GBFreq[20][16] = 529;
        this.GBFreq[46][74] = 528;
        this.GBFreq[30][19] = 527;
        this.GBFreq[45][35] = 526;
        this.GBFreq[45][61] = 525;
        this.GBFreq[30][9] = 524;
        this.GBFreq[41][53] = 523;
        this.GBFreq[41][13] = 522;
        this.GBFreq[50][34] = 521;
        this.GBFreq[53][86] = 520;
        this.GBFreq[47][47] = 519;
        this.GBFreq[22][28] = 518;
        this.GBFreq[50][53] = 517;
        this.GBFreq[39][70] = 516;
        this.GBFreq[38][15] = 515;
        this.GBFreq[42][88] = 514;
        this.GBFreq[16][29] = InputDeviceCompat.SOURCE_DPAD;
        this.GBFreq[27][90] = 512;
        this.GBFreq[29][12] = FrameMetricsAggregator.EVERY_DURATION;
        this.GBFreq[44][22] = 510;
        this.GBFreq[34][69] = 509;
        this.GBFreq[24][10] = 508;
        this.GBFreq[44][11] = 507;
        this.GBFreq[39][92] = 506;
        this.GBFreq[49][48] = 505;
        this.GBFreq[31][46] = 504;
        this.GBFreq[19][50] = 503;
        this.GBFreq[21][14] = 502;
        this.GBFreq[32][28] = 501;
        this.GBFreq[18][3] = 500;
        this.GBFreq[53][9] = 499;
        this.GBFreq[34][80] = 498;
        this.GBFreq[48][88] = 497;
        this.GBFreq[46][53] = 496;
        this.GBFreq[22][53] = 495;
        this.GBFreq[28][10] = 494;
        this.GBFreq[44][65] = 493;
        this.GBFreq[20][10] = 492;
        this.GBFreq[40][76] = 491;
        this.GBFreq[47][8] = 490;
        this.GBFreq[50][74] = 489;
        this.GBFreq[23][62] = 488;
        this.GBFreq[49][65] = 487;
        this.GBFreq[28][87] = 486;
        this.GBFreq[15][48] = 485;
        this.GBFreq[22][7] = 484;
        this.GBFreq[19][42] = 483;
        this.GBFreq[41][20] = 482;
        this.GBFreq[26][55] = 481;
        this.GBFreq[21][93] = 480;
        this.GBFreq[31][76] = 479;
        this.GBFreq[34][31] = 478;
        this.GBFreq[20][66] = 477;
        this.GBFreq[51][33] = 476;
        this.GBFreq[34][86] = 475;
        this.GBFreq[37][67] = 474;
        this.GBFreq[53][53] = 473;
        this.GBFreq[40][88] = 472;
        this.GBFreq[39][10] = 471;
        this.GBFreq[24][3] = 470;
        this.GBFreq[27][25] = 469;
        this.GBFreq[26][15] = 468;
        this.GBFreq[21][88] = 467;
        this.GBFreq[52][62] = 466;
        this.GBFreq[46][81] = 465;
        this.GBFreq[38][72] = 464;
        this.GBFreq[17][30] = 463;
        this.GBFreq[52][92] = 462;
        this.GBFreq[34][90] = 461;
        this.GBFreq[21][7] = 460;
        this.GBFreq[36][13] = 459;
        this.GBFreq[45][41] = 458;
        this.GBFreq[32][5] = 457;
        this.GBFreq[26][89] = 456;
        this.GBFreq[23][87] = 455;
        this.GBFreq[20][39] = 454;
        this.GBFreq[27][23] = 453;
        this.GBFreq[25][59] = 452;
        this.GBFreq[49][20] = 451;
        this.GBFreq[54][77] = 450;
        this.GBFreq[27][67] = 449;
        this.GBFreq[47][33] = 448;
        this.GBFreq[41][17] = 447;
        this.GBFreq[19][81] = 446;
        this.GBFreq[16][66] = SmbConstants.DEFAULT_PORT;
        this.GBFreq[45][26] = 444;
        this.GBFreq[49][81] = Handler.DEFAULT_HTTPS_PORT;
        this.GBFreq[53][55] = 442;
        this.GBFreq[16][26] = 441;
        this.GBFreq[54][62] = 440;
        this.GBFreq[20][70] = 439;
        this.GBFreq[42][35] = 438;
        this.GBFreq[20][57] = 437;
        this.GBFreq[34][36] = 436;
        this.GBFreq[46][63] = 435;
        this.GBFreq[19][45] = 434;
        this.GBFreq[21][10] = 433;
        this.GBFreq[52][93] = 432;
        this.GBFreq[25][2] = 431;
        this.GBFreq[30][57] = 430;
        this.GBFreq[41][24] = 429;
        this.GBFreq[28][43] = 428;
        this.GBFreq[45][86] = 427;
        this.GBFreq[51][56] = 426;
        this.GBFreq[37][28] = 425;
        this.GBFreq[52][69] = 424;
        this.GBFreq[43][92] = 423;
        this.GBFreq[41][31] = 422;
        this.GBFreq[37][87] = 421;
        this.GBFreq[47][36] = 420;
        this.GBFreq[16][16] = 419;
        this.GBFreq[40][56] = 418;
        this.GBFreq[24][55] = 417;
        this.GBFreq[17][1] = HTTPStatus.INVALID_RANGE;
        this.GBFreq[35][57] = 415;
        this.GBFreq[27][50] = 414;
        this.GBFreq[26][14] = 413;
        this.GBFreq[50][40] = HTTPStatus.PRECONDITION_FAILED;
        this.GBFreq[39][19] = 411;
        this.GBFreq[19][89] = 410;
        this.GBFreq[29][91] = 409;
        this.GBFreq[17][89] = 408;
        this.GBFreq[39][74] = 407;
        this.GBFreq[46][39] = 406;
        this.GBFreq[40][28] = 405;
        this.GBFreq[45][68] = HTTPStatus.NOT_FOUND;
        this.GBFreq[43][10] = 403;
        this.GBFreq[42][13] = 402;
        this.GBFreq[44][81] = 401;
        this.GBFreq[41][47] = HTTPStatus.BAD_REQUEST;
        this.GBFreq[48][58] = 399;
        this.GBFreq[43][68] = 398;
        this.GBFreq[16][79] = 397;
        this.GBFreq[19][5] = 396;
        this.GBFreq[54][59] = 395;
        this.GBFreq[17][36] = 394;
        this.GBFreq[18][0] = 393;
        this.GBFreq[41][5] = 392;
        this.GBFreq[41][72] = 391;
        this.GBFreq[16][39] = 390;
        this.GBFreq[54][0] = 389;
        this.GBFreq[51][16] = 388;
        this.GBFreq[29][36] = 387;
        this.GBFreq[47][5] = 386;
        this.GBFreq[47][51] = 385;
        this.GBFreq[44][7] = 384;
        this.GBFreq[35][30] = 383;
        this.GBFreq[26][9] = 382;
        this.GBFreq[16][7] = 381;
        this.GBFreq[32][1] = 380;
        this.GBFreq[33][76] = 379;
        this.GBFreq[34][91] = 378;
        this.GBFreq[52][36] = 377;
        this.GBFreq[26][77] = 376;
        this.GBFreq[35][48] = 375;
        this.GBFreq[40][80] = 374;
        this.GBFreq[41][92] = 373;
        this.GBFreq[27][93] = 372;
        this.GBFreq[15][17] = 371;
        this.GBFreq[16][76] = 370;
        this.GBFreq[51][12] = 369;
        this.GBFreq[18][20] = 368;
        this.GBFreq[15][54] = 367;
        this.GBFreq[50][5] = 366;
        this.GBFreq[33][22] = 365;
        this.GBFreq[37][57] = 364;
        this.GBFreq[28][47] = 363;
        this.GBFreq[42][31] = 362;
        this.GBFreq[18][2] = 361;
        this.GBFreq[43][64] = 360;
        this.GBFreq[23][47] = 359;
        this.GBFreq[28][79] = 358;
        this.GBFreq[25][45] = 357;
        this.GBFreq[23][91] = 356;
        this.GBFreq[22][19] = 355;
        this.GBFreq[25][46] = 354;
        this.GBFreq[22][36] = 353;
        this.GBFreq[54][85] = 352;
        this.GBFreq[46][20] = 351;
        this.GBFreq[27][37] = 350;
        this.GBFreq[26][81] = 349;
        this.GBFreq[42][29] = 348;
        this.GBFreq[31][90] = 347;
        this.GBFreq[41][59] = 346;
        this.GBFreq[24][65] = 345;
        this.GBFreq[44][84] = 344;
        this.GBFreq[24][90] = 343;
        this.GBFreq[38][54] = 342;
        this.GBFreq[28][70] = 341;
        this.GBFreq[27][15] = 340;
        this.GBFreq[28][80] = 339;
        this.GBFreq[29][8] = 338;
        this.GBFreq[45][80] = 337;
        this.GBFreq[53][37] = 336;
        this.GBFreq[28][65] = 335;
        this.GBFreq[23][86] = 334;
        this.GBFreq[39][45] = 333;
        this.GBFreq[53][32] = 332;
        this.GBFreq[38][68] = 331;
        this.GBFreq[45][78] = 330;
        this.GBFreq[43][7] = 329;
        this.GBFreq[46][82] = 328;
        this.GBFreq[27][38] = 327;
        this.GBFreq[16][62] = 326;
        this.GBFreq[24][17] = 325;
        this.GBFreq[22][70] = 324;
        this.GBFreq[52][28] = 323;
        this.GBFreq[23][40] = 322;
        this.GBFreq[28][50] = 321;
        this.GBFreq[42][91] = 320;
        this.GBFreq[47][76] = 319;
        this.GBFreq[15][42] = 318;
        this.GBFreq[43][55] = 317;
        this.GBFreq[29][84] = 316;
        this.GBFreq[44][90] = 315;
        this.GBFreq[53][16] = 314;
        this.GBFreq[22][93] = 313;
        this.GBFreq[34][10] = 312;
        this.GBFreq[32][53] = 311;
        this.GBFreq[43][65] = 310;
        this.GBFreq[28][7] = 309;
        this.GBFreq[35][46] = 308;
        this.GBFreq[21][39] = 307;
        this.GBFreq[44][18] = 306;
        this.GBFreq[40][10] = 305;
        this.GBFreq[54][53] = 304;
        this.GBFreq[38][74] = 303;
        this.GBFreq[28][26] = 302;
        this.GBFreq[15][13] = 301;
        this.GBFreq[39][34] = 300;
        this.GBFreq[39][46] = 299;
        this.GBFreq[42][66] = 298;
        this.GBFreq[33][58] = 297;
        this.GBFreq[15][56] = 296;
        this.GBFreq[18][51] = 295;
        this.GBFreq[49][68] = 294;
        this.GBFreq[30][37] = 293;
        this.GBFreq[51][84] = 292;
        this.GBFreq[51][9] = 291;
        this.GBFreq[40][70] = 290;
        this.GBFreq[41][84] = 289;
        this.GBFreq[28][64] = 288;
        this.GBFreq[32][88] = 287;
        this.GBFreq[24][5] = 286;
        this.GBFreq[53][23] = 285;
        this.GBFreq[42][27] = 284;
        this.GBFreq[22][38] = 283;
        this.GBFreq[32][86] = 282;
        this.GBFreq[34][30] = 281;
        this.GBFreq[38][63] = 280;
        this.GBFreq[24][59] = 279;
        this.GBFreq[22][81] = 278;
        this.GBFreq[32][11] = 277;
        this.GBFreq[51][21] = 276;
        this.GBFreq[54][41] = 275;
        this.GBFreq[21][50] = 274;
        this.GBFreq[23][89] = 273;
        this.GBFreq[19][87] = 272;
        this.GBFreq[26][7] = 271;
        this.GBFreq[30][75] = 270;
        this.GBFreq[43][84] = 269;
        this.GBFreq[51][25] = 268;
        this.GBFreq[16][67] = 267;
        this.GBFreq[32][9] = 266;
        this.GBFreq[48][51] = 265;
        this.GBFreq[39][7] = 264;
        this.GBFreq[44][88] = 263;
        this.GBFreq[52][24] = 262;
        this.GBFreq[23][34] = 261;
        this.GBFreq[32][75] = 260;
        this.GBFreq[19][10] = ShaderParameter.VARYING_PARAMETER;
        this.GBFreq[28][91] = ShaderParameter.ATTRIBUTE_PARAMETER;
        this.GBFreq[32][83] = 257;
        this.GBFreq[25][75] = 256;
        this.GBFreq[53][45] = 255;
        this.GBFreq[29][85] = 254;
        this.GBFreq[53][59] = 253;
        this.GBFreq[16][2] = 252;
        this.GBFreq[19][78] = 251;
        this.GBFreq[15][75] = 250;
        this.GBFreq[51][42] = 249;
        this.GBFreq[45][67] = 248;
        this.GBFreq[15][74] = 247;
        this.GBFreq[25][81] = 246;
        this.GBFreq[37][62] = 245;
        this.GBFreq[16][55] = 244;
        this.GBFreq[18][38] = 243;
        this.GBFreq[23][23] = 242;
        this.GBFreq[38][30] = 241;
        this.GBFreq[17][28] = 240;
        this.GBFreq[44][73] = 239;
        this.GBFreq[23][78] = 238;
        this.GBFreq[40][77] = 237;
        this.GBFreq[38][87] = 236;
        this.GBFreq[27][19] = 235;
        this.GBFreq[38][82] = WinError.ERROR_MORE_DATA;
        this.GBFreq[37][22] = WinError.ERROR_PIPE_NOT_CONNECTED;
        this.GBFreq[41][30] = WinError.ERROR_NO_DATA;
        this.GBFreq[54][9] = WinError.ERROR_PIPE_BUSY;
        this.GBFreq[32][30] = WinError.ERROR_BAD_PIPE;
        this.GBFreq[30][52] = 229;
        this.GBFreq[40][84] = 228;
        this.GBFreq[53][57] = 227;
        this.GBFreq[27][27] = 226;
        this.GBFreq[38][64] = 225;
        this.GBFreq[18][43] = 224;
        this.GBFreq[23][69] = 223;
        this.GBFreq[28][12] = 222;
        this.GBFreq[50][78] = 221;
        this.GBFreq[50][1] = 220;
        this.GBFreq[26][88] = 219;
        this.GBFreq[36][40] = 218;
        this.GBFreq[33][89] = 217;
        this.GBFreq[41][28] = 216;
        this.GBFreq[31][77] = 215;
        this.GBFreq[46][1] = 214;
        this.GBFreq[47][19] = 213;
        this.GBFreq[35][55] = 212;
        this.GBFreq[41][21] = 211;
        this.GBFreq[27][10] = 210;
        this.GBFreq[32][77] = 209;
        this.GBFreq[26][37] = 208;
        this.GBFreq[20][33] = 207;
        this.GBFreq[41][52] = HTTPStatus.PARTIAL_CONTENT;
        this.GBFreq[32][18] = 205;
        this.GBFreq[38][13] = 204;
        this.GBFreq[20][18] = 203;
        this.GBFreq[20][24] = 202;
        this.GBFreq[45][19] = 201;
        this.GBFreq[18][53] = 200;
        this.Big5Freq[9][89] = 600;
        this.Big5Freq[11][15] = 599;
        this.Big5Freq[3][66] = 598;
        this.Big5Freq[6][121] = 597;
        this.Big5Freq[3][0] = 596;
        this.Big5Freq[5][82] = 595;
        this.Big5Freq[3][42] = 594;
        this.Big5Freq[5][34] = 593;
        this.Big5Freq[3][8] = 592;
        this.Big5Freq[3][6] = 591;
        this.Big5Freq[3][67] = 590;
        this.Big5Freq[7][139] = 589;
        this.Big5Freq[23][137] = 588;
        this.Big5Freq[12][46] = 587;
        this.Big5Freq[4][8] = 586;
        this.Big5Freq[4][41] = 585;
        this.Big5Freq[18][47] = 584;
        this.Big5Freq[12][114] = 583;
        this.Big5Freq[6][1] = 582;
        this.Big5Freq[22][60] = 581;
        this.Big5Freq[5][46] = 580;
        this.Big5Freq[11][79] = 579;
        this.Big5Freq[3][23] = 578;
        this.Big5Freq[7][114] = 577;
        this.Big5Freq[29][102] = 576;
        this.Big5Freq[19][14] = 575;
        this.Big5Freq[4][133] = 574;
        this.Big5Freq[3][29] = 573;
        this.Big5Freq[4][109] = 572;
        this.Big5Freq[14][127] = 571;
        this.Big5Freq[5][48] = 570;
        this.Big5Freq[13][104] = 569;
        this.Big5Freq[3][132] = 568;
        this.Big5Freq[26][64] = 567;
        this.Big5Freq[7][19] = 566;
        this.Big5Freq[4][12] = 565;
        this.Big5Freq[11][124] = 564;
        this.Big5Freq[7][89] = 563;
        this.Big5Freq[15][124] = 562;
        this.Big5Freq[4][108] = 561;
        this.Big5Freq[19][66] = 560;
        this.Big5Freq[3][21] = 559;
        this.Big5Freq[24][12] = 558;
        this.Big5Freq[28][111] = 557;
        this.Big5Freq[12][107] = 556;
        this.Big5Freq[3][112] = 555;
        this.Big5Freq[8][113] = 554;
        this.Big5Freq[5][40] = 553;
        this.Big5Freq[26][145] = 552;
        this.Big5Freq[3][48] = 551;
        this.Big5Freq[3][70] = 550;
        this.Big5Freq[22][17] = 549;
        this.Big5Freq[16][47] = 548;
        this.Big5Freq[3][53] = 547;
        this.Big5Freq[4][24] = 546;
        this.Big5Freq[32][120] = 545;
        this.Big5Freq[24][49] = 544;
        this.Big5Freq[24][142] = 543;
        this.Big5Freq[18][66] = 542;
        this.Big5Freq[29][150] = 541;
        this.Big5Freq[5][122] = 540;
        this.Big5Freq[5][114] = 539;
        this.Big5Freq[3][44] = 538;
        this.Big5Freq[10][128] = 537;
        this.Big5Freq[15][20] = 536;
        this.Big5Freq[13][33] = 535;
        this.Big5Freq[14][87] = 534;
        this.Big5Freq[3][126] = 533;
        this.Big5Freq[4][53] = 532;
        this.Big5Freq[4][40] = 531;
        this.Big5Freq[9][93] = 530;
        this.Big5Freq[15][137] = 529;
        this.Big5Freq[10][123] = 528;
        this.Big5Freq[4][56] = 527;
        this.Big5Freq[5][71] = 526;
        this.Big5Freq[10][8] = 525;
        this.Big5Freq[5][16] = 524;
        this.Big5Freq[5][146] = 523;
        this.Big5Freq[18][88] = 522;
        this.Big5Freq[24][4] = 521;
        this.Big5Freq[20][47] = 520;
        this.Big5Freq[5][33] = 519;
        this.Big5Freq[9][43] = 518;
        this.Big5Freq[20][12] = 517;
        this.Big5Freq[20][13] = 516;
        this.Big5Freq[5][156] = 515;
        this.Big5Freq[22][140] = 514;
        this.Big5Freq[8][146] = InputDeviceCompat.SOURCE_DPAD;
        this.Big5Freq[21][123] = 512;
        this.Big5Freq[4][90] = FrameMetricsAggregator.EVERY_DURATION;
        this.Big5Freq[5][62] = 510;
        this.Big5Freq[17][59] = 509;
        this.Big5Freq[10][37] = 508;
        this.Big5Freq[18][107] = 507;
        this.Big5Freq[14][53] = 506;
        this.Big5Freq[22][51] = 505;
        this.Big5Freq[8][13] = 504;
        this.Big5Freq[5][29] = 503;
        this.Big5Freq[9][7] = 502;
        this.Big5Freq[22][14] = 501;
        this.Big5Freq[8][55] = 500;
        this.Big5Freq[33][9] = 499;
        this.Big5Freq[16][64] = 498;
        this.Big5Freq[7][131] = 497;
        this.Big5Freq[34][4] = 496;
        this.Big5Freq[7][101] = 495;
        this.Big5Freq[11][139] = 494;
        this.Big5Freq[3][135] = 493;
        this.Big5Freq[7][102] = 492;
        this.Big5Freq[17][13] = 491;
        this.Big5Freq[3][20] = 490;
        this.Big5Freq[27][106] = 489;
        this.Big5Freq[5][88] = 488;
        this.Big5Freq[6][33] = 487;
        this.Big5Freq[5][139] = 486;
        this.Big5Freq[6][0] = 485;
        this.Big5Freq[17][58] = 484;
        this.Big5Freq[5][133] = 483;
        this.Big5Freq[9][107] = 482;
        this.Big5Freq[23][39] = 481;
        this.Big5Freq[5][23] = 480;
        this.Big5Freq[3][79] = 479;
        this.Big5Freq[32][97] = 478;
        this.Big5Freq[3][136] = 477;
        this.Big5Freq[4][94] = 476;
        this.Big5Freq[21][61] = 475;
        this.Big5Freq[23][123] = 474;
        this.Big5Freq[26][16] = 473;
        this.Big5Freq[24][137] = 472;
        this.Big5Freq[22][18] = 471;
        this.Big5Freq[5][1] = 470;
        this.Big5Freq[20][119] = 469;
        this.Big5Freq[3][7] = 468;
        this.Big5Freq[10][79] = 467;
        this.Big5Freq[15][105] = 466;
        this.Big5Freq[3][144] = 465;
        this.Big5Freq[12][80] = 464;
        this.Big5Freq[15][73] = 463;
        this.Big5Freq[3][19] = 462;
        this.Big5Freq[8][109] = 461;
        this.Big5Freq[3][15] = 460;
        this.Big5Freq[31][82] = 459;
        this.Big5Freq[3][43] = 458;
        this.Big5Freq[25][119] = 457;
        this.Big5Freq[16][111] = 456;
        this.Big5Freq[7][77] = 455;
        this.Big5Freq[3][95] = 454;
        this.Big5Freq[24][82] = 453;
        this.Big5Freq[7][52] = 452;
        this.Big5Freq[9][151] = 451;
        this.Big5Freq[3][NbtException.NOT_LISTENING_CALLING] = 450;
        this.Big5Freq[5][87] = 449;
        this.Big5Freq[3][55] = 448;
        this.Big5Freq[8][153] = 447;
        this.Big5Freq[4][83] = 446;
        this.Big5Freq[3][114] = SmbConstants.DEFAULT_PORT;
        this.Big5Freq[23][147] = 444;
        this.Big5Freq[15][31] = Handler.DEFAULT_HTTPS_PORT;
        this.Big5Freq[3][54] = 442;
        this.Big5Freq[11][122] = 441;
        this.Big5Freq[4][4] = 440;
        this.Big5Freq[34][149] = 439;
        this.Big5Freq[3][17] = 438;
        this.Big5Freq[21][64] = 437;
        this.Big5Freq[26][144] = 436;
        this.Big5Freq[4][62] = 435;
        this.Big5Freq[8][15] = 434;
        this.Big5Freq[35][80] = 433;
        this.Big5Freq[7][110] = 432;
        this.Big5Freq[23][114] = 431;
        this.Big5Freq[3][108] = 430;
        this.Big5Freq[3][62] = 429;
        this.Big5Freq[21][41] = 428;
        this.Big5Freq[15][99] = 427;
        this.Big5Freq[5][47] = 426;
        this.Big5Freq[4][96] = 425;
        this.Big5Freq[20][122] = 424;
        this.Big5Freq[5][21] = 423;
        this.Big5Freq[4][157] = 422;
        this.Big5Freq[16][14] = 421;
        this.Big5Freq[3][117] = 420;
        this.Big5Freq[7][NbtException.NOT_LISTENING_CALLING] = 419;
        this.Big5Freq[4][27] = 418;
        this.Big5Freq[5][30] = 417;
        this.Big5Freq[22][16] = HTTPStatus.INVALID_RANGE;
        this.Big5Freq[5][64] = 415;
        this.Big5Freq[17][99] = 414;
        this.Big5Freq[17][57] = 413;
        this.Big5Freq[8][105] = HTTPStatus.PRECONDITION_FAILED;
        this.Big5Freq[5][112] = 411;
        this.Big5Freq[20][59] = 410;
        this.Big5Freq[6][NbtException.NOT_LISTENING_CALLING] = 409;
        this.Big5Freq[18][17] = 408;
        this.Big5Freq[3][92] = 407;
        this.Big5Freq[28][118] = 406;
        this.Big5Freq[3][109] = 405;
        this.Big5Freq[31][51] = HTTPStatus.NOT_FOUND;
        this.Big5Freq[13][116] = 403;
        this.Big5Freq[6][15] = 402;
        this.Big5Freq[36][136] = 401;
        this.Big5Freq[12][74] = HTTPStatus.BAD_REQUEST;
        this.Big5Freq[20][88] = 399;
        this.Big5Freq[36][68] = 398;
        this.Big5Freq[3][147] = 397;
        this.Big5Freq[15][84] = 396;
        this.Big5Freq[16][32] = 395;
        this.Big5Freq[16][58] = 394;
        this.Big5Freq[7][66] = 393;
        this.Big5Freq[23][107] = 392;
        this.Big5Freq[9][6] = 391;
        this.Big5Freq[12][86] = 390;
        this.Big5Freq[23][112] = 389;
        this.Big5Freq[37][23] = 388;
        this.Big5Freq[3][138] = 387;
        this.Big5Freq[20][68] = 386;
        this.Big5Freq[15][116] = 385;
        this.Big5Freq[18][64] = 384;
        this.Big5Freq[12][139] = 383;
        this.Big5Freq[11][155] = 382;
        this.Big5Freq[4][156] = 381;
        this.Big5Freq[12][84] = 380;
        this.Big5Freq[18][49] = 379;
        this.Big5Freq[25][125] = 378;
        this.Big5Freq[25][147] = 377;
        this.Big5Freq[15][110] = 376;
        this.Big5Freq[19][96] = 375;
        this.Big5Freq[30][152] = 374;
        this.Big5Freq[6][31] = 373;
        this.Big5Freq[27][117] = 372;
        this.Big5Freq[3][10] = 371;
        this.Big5Freq[6][131] = 370;
        this.Big5Freq[13][112] = 369;
        this.Big5Freq[36][156] = 368;
        this.Big5Freq[4][60] = 367;
        this.Big5Freq[15][121] = 366;
        this.Big5Freq[4][112] = 365;
        this.Big5Freq[30][142] = 364;
        this.Big5Freq[23][154] = 363;
        this.Big5Freq[27][101] = 362;
        this.Big5Freq[9][140] = 361;
        this.Big5Freq[3][89] = 360;
        this.Big5Freq[18][148] = 359;
        this.Big5Freq[4][69] = 358;
        this.Big5Freq[16][49] = 357;
        this.Big5Freq[6][117] = 356;
        this.Big5Freq[36][55] = 355;
        this.Big5Freq[5][123] = 354;
        this.Big5Freq[4][126] = 353;
        this.Big5Freq[4][119] = 352;
        this.Big5Freq[9][95] = 351;
        this.Big5Freq[5][24] = 350;
        this.Big5Freq[16][133] = 349;
        this.Big5Freq[10][134] = 348;
        this.Big5Freq[26][59] = 347;
        this.Big5Freq[6][41] = 346;
        this.Big5Freq[6][146] = 345;
        this.Big5Freq[19][24] = 344;
        this.Big5Freq[5][113] = 343;
        this.Big5Freq[10][118] = 342;
        this.Big5Freq[34][151] = 341;
        this.Big5Freq[9][72] = 340;
        this.Big5Freq[31][25] = 339;
        this.Big5Freq[18][126] = 338;
        this.Big5Freq[18][28] = 337;
        this.Big5Freq[4][153] = 336;
        this.Big5Freq[3][84] = 335;
        this.Big5Freq[21][18] = 334;
        this.Big5Freq[25][NbtException.NOT_LISTENING_CALLING] = 333;
        this.Big5Freq[6][107] = 332;
        this.Big5Freq[12][25] = 331;
        this.Big5Freq[17][109] = 330;
        this.Big5Freq[7][76] = 329;
        this.Big5Freq[15][15] = 328;
        this.Big5Freq[4][14] = 327;
        this.Big5Freq[23][88] = 326;
        this.Big5Freq[18][2] = 325;
        this.Big5Freq[6][88] = 324;
        this.Big5Freq[16][84] = 323;
        this.Big5Freq[12][48] = 322;
        this.Big5Freq[7][68] = 321;
        this.Big5Freq[5][50] = 320;
        this.Big5Freq[13][54] = 319;
        this.Big5Freq[7][98] = 318;
        this.Big5Freq[11][6] = 317;
        this.Big5Freq[9][80] = 316;
        this.Big5Freq[16][41] = 315;
        this.Big5Freq[7][43] = 314;
        this.Big5Freq[28][117] = 313;
        this.Big5Freq[3][51] = 312;
        this.Big5Freq[7][3] = 311;
        this.Big5Freq[20][81] = 310;
        this.Big5Freq[4][2] = 309;
        this.Big5Freq[11][16] = 308;
        this.Big5Freq[10][4] = 307;
        this.Big5Freq[10][119] = 306;
        this.Big5Freq[6][142] = 305;
        this.Big5Freq[18][51] = 304;
        this.Big5Freq[8][144] = 303;
        this.Big5Freq[10][65] = 302;
        this.Big5Freq[11][64] = 301;
        this.Big5Freq[11][130] = 300;
        this.Big5Freq[9][92] = 299;
        this.Big5Freq[18][29] = 298;
        this.Big5Freq[18][78] = 297;
        this.Big5Freq[18][151] = 296;
        this.Big5Freq[33][127] = 295;
        this.Big5Freq[35][113] = 294;
        this.Big5Freq[10][155] = 293;
        this.Big5Freq[3][76] = 292;
        this.Big5Freq[36][123] = 291;
        this.Big5Freq[13][NbtException.UNSPECIFIED] = 290;
        this.Big5Freq[5][135] = 289;
        this.Big5Freq[23][116] = 288;
        this.Big5Freq[6][101] = 287;
        this.Big5Freq[14][74] = 286;
        this.Big5Freq[7][153] = 285;
        this.Big5Freq[3][101] = 284;
        this.Big5Freq[9][74] = 283;
        this.Big5Freq[3][156] = 282;
        this.Big5Freq[4][147] = 281;
        this.Big5Freq[9][12] = 280;
        this.Big5Freq[18][133] = 279;
        this.Big5Freq[4][0] = 278;
        this.Big5Freq[7][155] = 277;
        this.Big5Freq[9][144] = 276;
        this.Big5Freq[23][49] = 275;
        this.Big5Freq[5][89] = 274;
        this.Big5Freq[10][11] = 273;
        this.Big5Freq[3][110] = 272;
        this.Big5Freq[3][40] = 271;
        this.Big5Freq[29][115] = 270;
        this.Big5Freq[9][100] = 269;
        this.Big5Freq[21][67] = 268;
        this.Big5Freq[23][145] = 267;
        this.Big5Freq[10][47] = 266;
        this.Big5Freq[4][31] = 265;
        this.Big5Freq[4][81] = 264;
        this.Big5Freq[22][62] = 263;
        this.Big5Freq[4][28] = 262;
        this.Big5Freq[27][39] = 261;
        this.Big5Freq[27][54] = 260;
        this.Big5Freq[32][46] = ShaderParameter.VARYING_PARAMETER;
        this.Big5Freq[4][76] = ShaderParameter.ATTRIBUTE_PARAMETER;
        this.Big5Freq[26][15] = 257;
        this.Big5Freq[12][154] = 256;
        this.Big5Freq[9][150] = 255;
        this.Big5Freq[15][17] = 254;
        this.Big5Freq[5][NbtException.NOT_LISTENING_CALLING] = 253;
        this.Big5Freq[10][40] = 252;
        this.Big5Freq[13][37] = 251;
        this.Big5Freq[31][104] = 250;
        this.Big5Freq[3][152] = 249;
        this.Big5Freq[5][22] = 248;
        this.Big5Freq[8][48] = 247;
        this.Big5Freq[4][74] = 246;
        this.Big5Freq[6][17] = 245;
        this.Big5Freq[30][82] = 244;
        this.Big5Freq[4][116] = 243;
        this.Big5Freq[16][42] = 242;
        this.Big5Freq[5][55] = 241;
        this.Big5Freq[4][64] = 240;
        this.Big5Freq[14][19] = 239;
        this.Big5Freq[35][82] = 238;
        this.Big5Freq[30][139] = 237;
        this.Big5Freq[26][152] = 236;
        this.Big5Freq[32][32] = 235;
        this.Big5Freq[21][102] = WinError.ERROR_MORE_DATA;
        this.Big5Freq[10][131] = WinError.ERROR_PIPE_NOT_CONNECTED;
        this.Big5Freq[9][128] = WinError.ERROR_NO_DATA;
        this.Big5Freq[3][87] = WinError.ERROR_PIPE_BUSY;
        this.Big5Freq[4][51] = WinError.ERROR_BAD_PIPE;
        this.Big5Freq[10][15] = 229;
        this.Big5Freq[4][150] = 228;
        this.Big5Freq[7][4] = 227;
        this.Big5Freq[7][51] = 226;
        this.Big5Freq[7][157] = 225;
        this.Big5Freq[4][146] = 224;
        this.Big5Freq[4][91] = 223;
        this.Big5Freq[7][13] = 222;
        this.Big5Freq[17][116] = 221;
        this.Big5Freq[23][21] = 220;
        this.Big5Freq[5][106] = 219;
        this.Big5Freq[14][100] = 218;
        this.Big5Freq[10][152] = 217;
        this.Big5Freq[14][89] = 216;
        this.Big5Freq[6][138] = 215;
        this.Big5Freq[12][157] = 214;
        this.Big5Freq[10][102] = 213;
        this.Big5Freq[19][94] = 212;
        this.Big5Freq[7][74] = 211;
        this.Big5Freq[18][128] = 210;
        this.Big5Freq[27][111] = 209;
        this.Big5Freq[11][57] = 208;
        this.Big5Freq[3][131] = 207;
        this.Big5Freq[30][23] = HTTPStatus.PARTIAL_CONTENT;
        this.Big5Freq[30][126] = 205;
        this.Big5Freq[4][36] = 204;
        this.Big5Freq[26][124] = 203;
        this.Big5Freq[4][19] = 202;
        this.Big5Freq[9][152] = 201;
        this.EUC_TWFreq[48][49] = 599;
        this.EUC_TWFreq[35][65] = 598;
        this.EUC_TWFreq[41][27] = 597;
        this.EUC_TWFreq[35][0] = 596;
        this.EUC_TWFreq[39][19] = 595;
        this.EUC_TWFreq[35][42] = 594;
        this.EUC_TWFreq[38][66] = 593;
        this.EUC_TWFreq[35][8] = 592;
        this.EUC_TWFreq[35][6] = 591;
        this.EUC_TWFreq[35][66] = 590;
        this.EUC_TWFreq[43][14] = 589;
        this.EUC_TWFreq[69][80] = 588;
        this.EUC_TWFreq[50][48] = 587;
        this.EUC_TWFreq[36][71] = 586;
        this.EUC_TWFreq[37][10] = 585;
        this.EUC_TWFreq[60][52] = 584;
        this.EUC_TWFreq[51][21] = 583;
        this.EUC_TWFreq[40][2] = 582;
        this.EUC_TWFreq[67][35] = 581;
        this.EUC_TWFreq[38][78] = 580;
        this.EUC_TWFreq[49][18] = 579;
        this.EUC_TWFreq[35][23] = 578;
        this.EUC_TWFreq[42][83] = 577;
        this.EUC_TWFreq[79][47] = 576;
        this.EUC_TWFreq[61][82] = 575;
        this.EUC_TWFreq[38][7] = 574;
        this.EUC_TWFreq[35][29] = 573;
        this.EUC_TWFreq[37][77] = 572;
        this.EUC_TWFreq[54][67] = 571;
        this.EUC_TWFreq[38][80] = 570;
        this.EUC_TWFreq[52][74] = 569;
        this.EUC_TWFreq[36][37] = 568;
        this.EUC_TWFreq[74][8] = 567;
        this.EUC_TWFreq[41][83] = 566;
        this.EUC_TWFreq[36][75] = 565;
        this.EUC_TWFreq[49][63] = 564;
        this.EUC_TWFreq[42][58] = 563;
        this.EUC_TWFreq[56][33] = 562;
        this.EUC_TWFreq[37][76] = 561;
        this.EUC_TWFreq[62][39] = 560;
        this.EUC_TWFreq[35][21] = 559;
        this.EUC_TWFreq[70][19] = 558;
        this.EUC_TWFreq[77][88] = 557;
        this.EUC_TWFreq[51][14] = 556;
        this.EUC_TWFreq[36][17] = 555;
        this.EUC_TWFreq[44][51] = 554;
        this.EUC_TWFreq[38][72] = 553;
        this.EUC_TWFreq[74][90] = 552;
        this.EUC_TWFreq[35][48] = 551;
        this.EUC_TWFreq[35][69] = 550;
        this.EUC_TWFreq[66][86] = 549;
        this.EUC_TWFreq[57][20] = 548;
        this.EUC_TWFreq[35][53] = 547;
        this.EUC_TWFreq[36][87] = 546;
        this.EUC_TWFreq[84][67] = 545;
        this.EUC_TWFreq[70][56] = 544;
        this.EUC_TWFreq[71][54] = 543;
        this.EUC_TWFreq[60][70] = 542;
        this.EUC_TWFreq[80][1] = 541;
        this.EUC_TWFreq[39][59] = 540;
        this.EUC_TWFreq[39][51] = 539;
        this.EUC_TWFreq[35][44] = 538;
        this.EUC_TWFreq[48][4] = 537;
        this.EUC_TWFreq[55][24] = 536;
        this.EUC_TWFreq[52][4] = 535;
        this.EUC_TWFreq[54][26] = 534;
        this.EUC_TWFreq[36][31] = 533;
        this.EUC_TWFreq[37][22] = 532;
        this.EUC_TWFreq[37][9] = 531;
        this.EUC_TWFreq[46][0] = 530;
        this.EUC_TWFreq[56][46] = 529;
        this.EUC_TWFreq[47][93] = 528;
        this.EUC_TWFreq[37][25] = 527;
        this.EUC_TWFreq[39][8] = 526;
        this.EUC_TWFreq[46][73] = 525;
        this.EUC_TWFreq[38][48] = 524;
        this.EUC_TWFreq[39][83] = 523;
        this.EUC_TWFreq[60][92] = 522;
        this.EUC_TWFreq[70][11] = 521;
        this.EUC_TWFreq[63][84] = 520;
        this.EUC_TWFreq[38][65] = 519;
        this.EUC_TWFreq[45][45] = 518;
        this.EUC_TWFreq[63][49] = 517;
        this.EUC_TWFreq[63][50] = 516;
        this.EUC_TWFreq[39][93] = 515;
        this.EUC_TWFreq[68][20] = 514;
        this.EUC_TWFreq[44][84] = InputDeviceCompat.SOURCE_DPAD;
        this.EUC_TWFreq[66][34] = 512;
        this.EUC_TWFreq[37][58] = FrameMetricsAggregator.EVERY_DURATION;
        this.EUC_TWFreq[39][0] = 510;
        this.EUC_TWFreq[59][1] = 509;
        this.EUC_TWFreq[47][8] = 508;
        this.EUC_TWFreq[61][17] = 507;
        this.EUC_TWFreq[53][87] = 506;
        this.EUC_TWFreq[67][26] = 505;
        this.EUC_TWFreq[43][46] = 504;
        this.EUC_TWFreq[38][61] = 503;
        this.EUC_TWFreq[45][9] = 502;
        this.EUC_TWFreq[66][83] = 501;
        this.EUC_TWFreq[43][88] = 500;
        this.EUC_TWFreq[85][20] = 499;
        this.EUC_TWFreq[57][36] = 498;
        this.EUC_TWFreq[43][6] = 497;
        this.EUC_TWFreq[86][77] = 496;
        this.EUC_TWFreq[42][70] = 495;
        this.EUC_TWFreq[49][78] = 494;
        this.EUC_TWFreq[36][40] = 493;
        this.EUC_TWFreq[42][71] = 492;
        this.EUC_TWFreq[58][49] = 491;
        this.EUC_TWFreq[35][20] = 490;
        this.EUC_TWFreq[76][20] = 489;
        this.EUC_TWFreq[39][25] = 488;
        this.EUC_TWFreq[40][34] = 487;
        this.EUC_TWFreq[39][76] = 486;
        this.EUC_TWFreq[40][1] = 485;
        this.EUC_TWFreq[59][0] = 484;
        this.EUC_TWFreq[39][70] = 483;
        this.EUC_TWFreq[46][14] = 482;
        this.EUC_TWFreq[68][77] = 481;
        this.EUC_TWFreq[38][55] = 480;
        this.EUC_TWFreq[35][78] = 479;
        this.EUC_TWFreq[84][44] = 478;
        this.EUC_TWFreq[36][41] = 477;
        this.EUC_TWFreq[37][62] = 476;
        this.EUC_TWFreq[65][67] = 475;
        this.EUC_TWFreq[69][66] = 474;
        this.EUC_TWFreq[73][55] = 473;
        this.EUC_TWFreq[71][49] = 472;
        this.EUC_TWFreq[66][87] = 471;
        this.EUC_TWFreq[38][33] = 470;
        this.EUC_TWFreq[64][61] = 469;
        this.EUC_TWFreq[35][7] = 468;
        this.EUC_TWFreq[47][49] = 467;
        this.EUC_TWFreq[56][14] = 466;
        this.EUC_TWFreq[36][49] = 465;
        this.EUC_TWFreq[50][81] = 464;
        this.EUC_TWFreq[55][76] = 463;
        this.EUC_TWFreq[35][19] = 462;
        this.EUC_TWFreq[44][47] = 461;
        this.EUC_TWFreq[35][15] = 460;
        this.EUC_TWFreq[82][59] = 459;
        this.EUC_TWFreq[35][43] = 458;
        this.EUC_TWFreq[73][0] = 457;
        this.EUC_TWFreq[57][83] = 456;
        this.EUC_TWFreq[42][46] = 455;
        this.EUC_TWFreq[36][0] = 454;
        this.EUC_TWFreq[70][88] = 453;
        this.EUC_TWFreq[42][22] = 452;
        this.EUC_TWFreq[46][58] = 451;
        this.EUC_TWFreq[36][34] = 450;
        this.EUC_TWFreq[39][24] = 449;
        this.EUC_TWFreq[35][55] = 448;
        this.EUC_TWFreq[44][91] = 447;
        this.EUC_TWFreq[37][51] = 446;
        this.EUC_TWFreq[36][19] = SmbConstants.DEFAULT_PORT;
        this.EUC_TWFreq[69][90] = 444;
        this.EUC_TWFreq[55][35] = Handler.DEFAULT_HTTPS_PORT;
        this.EUC_TWFreq[35][54] = 442;
        this.EUC_TWFreq[49][61] = 441;
        this.EUC_TWFreq[36][67] = 440;
        this.EUC_TWFreq[88][34] = 439;
        this.EUC_TWFreq[35][17] = 438;
        this.EUC_TWFreq[65][69] = 437;
        this.EUC_TWFreq[74][89] = 436;
        this.EUC_TWFreq[37][31] = 435;
        this.EUC_TWFreq[43][48] = 434;
        this.EUC_TWFreq[89][27] = 433;
        this.EUC_TWFreq[42][79] = 432;
        this.EUC_TWFreq[69][57] = 431;
        this.EUC_TWFreq[36][13] = 430;
        this.EUC_TWFreq[35][62] = 429;
        this.EUC_TWFreq[65][47] = 428;
        this.EUC_TWFreq[56][8] = 427;
        this.EUC_TWFreq[38][79] = 426;
        this.EUC_TWFreq[37][64] = 425;
        this.EUC_TWFreq[64][64] = 424;
        this.EUC_TWFreq[38][53] = 423;
        this.EUC_TWFreq[38][31] = 422;
        this.EUC_TWFreq[56][81] = 421;
        this.EUC_TWFreq[36][22] = 420;
        this.EUC_TWFreq[43][4] = 419;
        this.EUC_TWFreq[36][90] = 418;
        this.EUC_TWFreq[38][62] = 417;
        this.EUC_TWFreq[66][85] = HTTPStatus.INVALID_RANGE;
        this.EUC_TWFreq[39][1] = 415;
        this.EUC_TWFreq[59][40] = 414;
        this.EUC_TWFreq[58][93] = 413;
        this.EUC_TWFreq[44][43] = HTTPStatus.PRECONDITION_FAILED;
        this.EUC_TWFreq[39][49] = 411;
        this.EUC_TWFreq[64][2] = 410;
        this.EUC_TWFreq[41][35] = 409;
        this.EUC_TWFreq[60][22] = 408;
        this.EUC_TWFreq[35][91] = 407;
        this.EUC_TWFreq[78][1] = 406;
        this.EUC_TWFreq[36][14] = 405;
        this.EUC_TWFreq[82][29] = HTTPStatus.NOT_FOUND;
        this.EUC_TWFreq[52][86] = 403;
        this.EUC_TWFreq[40][16] = 402;
        this.EUC_TWFreq[91][52] = 401;
        this.EUC_TWFreq[50][75] = HTTPStatus.BAD_REQUEST;
        this.EUC_TWFreq[64][30] = 399;
        this.EUC_TWFreq[90][78] = 398;
        this.EUC_TWFreq[36][52] = 397;
        this.EUC_TWFreq[55][87] = 396;
        this.EUC_TWFreq[57][5] = 395;
        this.EUC_TWFreq[57][31] = 394;
        this.EUC_TWFreq[42][35] = 393;
        this.EUC_TWFreq[69][50] = 392;
        this.EUC_TWFreq[45][8] = 391;
        this.EUC_TWFreq[50][87] = 390;
        this.EUC_TWFreq[69][55] = 389;
        this.EUC_TWFreq[92][3] = 388;
        this.EUC_TWFreq[36][43] = 387;
        this.EUC_TWFreq[64][10] = 386;
        this.EUC_TWFreq[56][25] = 385;
        this.EUC_TWFreq[60][68] = 384;
        this.EUC_TWFreq[51][46] = 383;
        this.EUC_TWFreq[50][0] = 382;
        this.EUC_TWFreq[38][30] = 381;
        this.EUC_TWFreq[50][85] = 380;
        this.EUC_TWFreq[60][54] = 379;
        this.EUC_TWFreq[73][6] = 378;
        this.EUC_TWFreq[73][28] = 377;
        this.EUC_TWFreq[56][19] = 376;
        this.EUC_TWFreq[62][69] = 375;
        this.EUC_TWFreq[81][66] = 374;
        this.EUC_TWFreq[40][32] = 373;
        this.EUC_TWFreq[76][31] = 372;
        this.EUC_TWFreq[35][10] = 371;
        this.EUC_TWFreq[41][37] = 370;
        this.EUC_TWFreq[52][82] = 369;
        this.EUC_TWFreq[91][72] = 368;
        this.EUC_TWFreq[37][29] = 367;
        this.EUC_TWFreq[56][30] = 366;
        this.EUC_TWFreq[37][80] = 365;
        this.EUC_TWFreq[81][56] = 364;
        this.EUC_TWFreq[70][3] = 363;
        this.EUC_TWFreq[76][15] = 362;
        this.EUC_TWFreq[46][47] = 361;
        this.EUC_TWFreq[35][88] = 360;
        this.EUC_TWFreq[61][58] = 359;
        this.EUC_TWFreq[37][37] = 358;
        this.EUC_TWFreq[57][22] = 357;
        this.EUC_TWFreq[41][23] = 356;
        this.EUC_TWFreq[90][66] = 355;
        this.EUC_TWFreq[39][60] = 354;
        this.EUC_TWFreq[38][0] = 353;
        this.EUC_TWFreq[37][87] = 352;
        this.EUC_TWFreq[46][2] = 351;
        this.EUC_TWFreq[38][56] = 350;
        this.EUC_TWFreq[58][11] = 349;
        this.EUC_TWFreq[48][10] = 348;
        this.EUC_TWFreq[74][4] = 347;
        this.EUC_TWFreq[40][42] = 346;
        this.EUC_TWFreq[41][52] = 345;
        this.EUC_TWFreq[61][92] = 344;
        this.EUC_TWFreq[39][50] = 343;
        this.EUC_TWFreq[47][88] = 342;
        this.EUC_TWFreq[88][36] = 341;
        this.EUC_TWFreq[45][73] = 340;
        this.EUC_TWFreq[82][3] = 339;
        this.EUC_TWFreq[61][36] = 338;
        this.EUC_TWFreq[60][33] = 337;
        this.EUC_TWFreq[38][27] = 336;
        this.EUC_TWFreq[35][83] = 335;
        this.EUC_TWFreq[65][24] = 334;
        this.EUC_TWFreq[73][10] = 333;
        this.EUC_TWFreq[41][13] = 332;
        this.EUC_TWFreq[50][27] = 331;
        this.EUC_TWFreq[59][50] = 330;
        this.EUC_TWFreq[42][45] = 329;
        this.EUC_TWFreq[55][19] = 328;
        this.EUC_TWFreq[36][77] = 327;
        this.EUC_TWFreq[69][31] = 326;
        this.EUC_TWFreq[60][7] = 325;
        this.EUC_TWFreq[40][88] = 324;
        this.EUC_TWFreq[57][56] = 323;
        this.EUC_TWFreq[50][50] = 322;
        this.EUC_TWFreq[42][37] = 321;
        this.EUC_TWFreq[38][82] = 320;
        this.EUC_TWFreq[52][25] = 319;
        this.EUC_TWFreq[42][67] = 318;
        this.EUC_TWFreq[48][40] = 317;
        this.EUC_TWFreq[45][81] = 316;
        this.EUC_TWFreq[57][14] = 315;
        this.EUC_TWFreq[42][13] = 314;
        this.EUC_TWFreq[78][0] = 313;
        this.EUC_TWFreq[35][51] = 312;
        this.EUC_TWFreq[41][67] = 311;
        this.EUC_TWFreq[64][23] = 310;
        this.EUC_TWFreq[36][65] = 309;
        this.EUC_TWFreq[48][50] = 308;
        this.EUC_TWFreq[46][69] = 307;
        this.EUC_TWFreq[47][89] = 306;
        this.EUC_TWFreq[41][48] = 305;
        this.EUC_TWFreq[60][56] = 304;
        this.EUC_TWFreq[44][82] = 303;
        this.EUC_TWFreq[47][35] = 302;
        this.EUC_TWFreq[49][3] = 301;
        this.EUC_TWFreq[49][69] = 300;
        this.EUC_TWFreq[45][93] = 299;
        this.EUC_TWFreq[60][34] = 298;
        this.EUC_TWFreq[60][82] = 297;
        this.EUC_TWFreq[61][61] = 296;
        this.EUC_TWFreq[86][42] = 295;
        this.EUC_TWFreq[89][60] = 294;
        this.EUC_TWFreq[48][31] = 293;
        this.EUC_TWFreq[35][75] = 292;
        this.EUC_TWFreq[91][39] = 291;
        this.EUC_TWFreq[53][19] = 290;
        this.EUC_TWFreq[39][72] = 289;
        this.EUC_TWFreq[69][59] = 288;
        this.EUC_TWFreq[41][7] = 287;
        this.EUC_TWFreq[54][13] = 286;
        this.EUC_TWFreq[43][28] = 285;
        this.EUC_TWFreq[36][6] = 284;
        this.EUC_TWFreq[45][75] = 283;
        this.EUC_TWFreq[36][61] = 282;
        this.EUC_TWFreq[38][21] = 281;
        this.EUC_TWFreq[45][14] = 280;
        this.EUC_TWFreq[61][43] = 279;
        this.EUC_TWFreq[36][63] = 278;
        this.EUC_TWFreq[43][30] = 277;
        this.EUC_TWFreq[46][51] = 276;
        this.EUC_TWFreq[68][87] = 275;
        this.EUC_TWFreq[39][26] = 274;
        this.EUC_TWFreq[46][76] = 273;
        this.EUC_TWFreq[36][15] = 272;
        this.EUC_TWFreq[35][40] = 271;
        this.EUC_TWFreq[79][60] = 270;
        this.EUC_TWFreq[46][7] = 269;
        this.EUC_TWFreq[65][72] = 268;
        this.EUC_TWFreq[69][88] = 267;
        this.EUC_TWFreq[47][18] = 266;
        this.EUC_TWFreq[37][0] = 265;
        this.EUC_TWFreq[37][49] = 264;
        this.EUC_TWFreq[67][37] = 263;
        this.EUC_TWFreq[36][91] = 262;
        this.EUC_TWFreq[75][48] = 261;
        this.EUC_TWFreq[75][63] = 260;
        this.EUC_TWFreq[83][87] = ShaderParameter.VARYING_PARAMETER;
        this.EUC_TWFreq[37][44] = ShaderParameter.ATTRIBUTE_PARAMETER;
        this.EUC_TWFreq[73][54] = 257;
        this.EUC_TWFreq[51][61] = 256;
        this.EUC_TWFreq[46][57] = 255;
        this.EUC_TWFreq[55][21] = 254;
        this.EUC_TWFreq[39][66] = 253;
        this.EUC_TWFreq[47][11] = 252;
        this.EUC_TWFreq[52][8] = 251;
        this.EUC_TWFreq[82][81] = 250;
        this.EUC_TWFreq[36][57] = 249;
        this.EUC_TWFreq[38][54] = 248;
        this.EUC_TWFreq[43][81] = 247;
        this.EUC_TWFreq[37][42] = 246;
        this.EUC_TWFreq[40][18] = 245;
        this.EUC_TWFreq[80][90] = 244;
        this.EUC_TWFreq[37][84] = 243;
        this.EUC_TWFreq[57][15] = 242;
        this.EUC_TWFreq[38][87] = 241;
        this.EUC_TWFreq[37][32] = 240;
        this.EUC_TWFreq[53][53] = 239;
        this.EUC_TWFreq[89][29] = 238;
        this.EUC_TWFreq[81][53] = 237;
        this.EUC_TWFreq[75][3] = 236;
        this.EUC_TWFreq[83][73] = 235;
        this.EUC_TWFreq[66][13] = WinError.ERROR_MORE_DATA;
        this.EUC_TWFreq[48][7] = WinError.ERROR_PIPE_NOT_CONNECTED;
        this.EUC_TWFreq[46][35] = WinError.ERROR_NO_DATA;
        this.EUC_TWFreq[35][86] = WinError.ERROR_PIPE_BUSY;
        this.EUC_TWFreq[37][20] = WinError.ERROR_BAD_PIPE;
        this.EUC_TWFreq[46][80] = 229;
        this.EUC_TWFreq[38][24] = 228;
        this.EUC_TWFreq[41][68] = 227;
        this.EUC_TWFreq[42][21] = 226;
        this.EUC_TWFreq[43][32] = 225;
        this.EUC_TWFreq[38][20] = 224;
        this.EUC_TWFreq[37][59] = 223;
        this.EUC_TWFreq[41][77] = 222;
        this.EUC_TWFreq[59][57] = 221;
        this.EUC_TWFreq[68][59] = 220;
        this.EUC_TWFreq[39][43] = 219;
        this.EUC_TWFreq[54][39] = 218;
        this.EUC_TWFreq[48][28] = 217;
        this.EUC_TWFreq[54][28] = 216;
        this.EUC_TWFreq[41][44] = 215;
        this.EUC_TWFreq[51][64] = 214;
        this.EUC_TWFreq[47][72] = 213;
        this.EUC_TWFreq[62][67] = 212;
        this.EUC_TWFreq[42][43] = 211;
        this.EUC_TWFreq[61][38] = 210;
        this.EUC_TWFreq[76][25] = 209;
        this.EUC_TWFreq[48][91] = 208;
        this.EUC_TWFreq[36][36] = 207;
        this.EUC_TWFreq[80][32] = HTTPStatus.PARTIAL_CONTENT;
        this.EUC_TWFreq[81][40] = 205;
        this.EUC_TWFreq[37][5] = 204;
        this.EUC_TWFreq[74][69] = 203;
        this.EUC_TWFreq[36][82] = 202;
        this.EUC_TWFreq[46][59] = 201;
        this.GBKFreq[52][132] = 600;
        this.GBKFreq[73][135] = 599;
        this.GBKFreq[49][123] = 598;
        this.GBKFreq[77][146] = 597;
        this.GBKFreq[81][123] = 596;
        this.GBKFreq[82][144] = 595;
        this.GBKFreq[51][179] = 594;
        this.GBKFreq[83][154] = 593;
        this.GBKFreq[71][139] = 592;
        this.GBKFreq[64][139] = 591;
        this.GBKFreq[85][144] = 590;
        this.GBKFreq[52][125] = 589;
        this.GBKFreq[88][25] = 588;
        this.GBKFreq[81][106] = 587;
        this.GBKFreq[81][148] = 586;
        this.GBKFreq[62][137] = 585;
        this.GBKFreq[94][0] = 584;
        this.GBKFreq[1][64] = 583;
        this.GBKFreq[67][163] = 582;
        this.GBKFreq[20][190] = 581;
        this.GBKFreq[57][131] = 580;
        this.GBKFreq[29][169] = 579;
        this.GBKFreq[72][NbtException.UNSPECIFIED] = 578;
        this.GBKFreq[0][173] = 577;
        this.GBKFreq[11][23] = 576;
        this.GBKFreq[61][141] = 575;
        this.GBKFreq[60][123] = 574;
        this.GBKFreq[81][114] = 573;
        this.GBKFreq[82][131] = 572;
        this.GBKFreq[67][156] = 571;
        this.GBKFreq[71][167] = 570;
        this.GBKFreq[20][50] = 569;
        this.GBKFreq[77][132] = 568;
        this.GBKFreq[84][38] = 567;
        this.GBKFreq[26][29] = 566;
        this.GBKFreq[74][187] = 565;
        this.GBKFreq[62][116] = 564;
        this.GBKFreq[67][135] = 563;
        this.GBKFreq[5][86] = 562;
        this.GBKFreq[72][186] = 561;
        this.GBKFreq[75][161] = 560;
        this.GBKFreq[78][130] = 559;
        this.GBKFreq[94][30] = 558;
        this.GBKFreq[84][72] = 557;
        this.GBKFreq[1][67] = 556;
        this.GBKFreq[75][172] = 555;
        this.GBKFreq[74][BuildConfig.VERSION_CODE] = 554;
        this.GBKFreq[53][160] = 553;
        this.GBKFreq[123][14] = 552;
        this.GBKFreq[79][97] = 551;
        this.GBKFreq[85][110] = 550;
        this.GBKFreq[78][171] = 549;
        this.GBKFreq[52][131] = 548;
        this.GBKFreq[56][100] = 547;
        this.GBKFreq[50][182] = 546;
        this.GBKFreq[94][64] = 545;
        this.GBKFreq[106][74] = 544;
        this.GBKFreq[11][102] = 543;
        this.GBKFreq[53][124] = 542;
        this.GBKFreq[24][3] = 541;
        this.GBKFreq[86][148] = 540;
        this.GBKFreq[53][184] = 539;
        this.GBKFreq[86][147] = 538;
        this.GBKFreq[96][161] = 537;
        this.GBKFreq[82][77] = 536;
        this.GBKFreq[59][146] = 535;
        this.GBKFreq[84][126] = 534;
        this.GBKFreq[79][132] = 533;
        this.GBKFreq[85][123] = 532;
        this.GBKFreq[71][101] = 531;
        this.GBKFreq[85][106] = 530;
        this.GBKFreq[6][184] = 529;
        this.GBKFreq[57][156] = 528;
        this.GBKFreq[75][104] = 527;
        this.GBKFreq[50][137] = 526;
        this.GBKFreq[79][133] = 525;
        this.GBKFreq[76][108] = 524;
        this.GBKFreq[57][142] = 523;
        this.GBKFreq[84][130] = 522;
        this.GBKFreq[52][128] = 521;
        this.GBKFreq[47][44] = 520;
        this.GBKFreq[52][152] = 519;
        this.GBKFreq[54][104] = 518;
        this.GBKFreq[30][47] = 517;
        this.GBKFreq[71][123] = 516;
        this.GBKFreq[52][107] = 515;
        this.GBKFreq[45][84] = 514;
        this.GBKFreq[107][118] = InputDeviceCompat.SOURCE_DPAD;
        this.GBKFreq[5][161] = 512;
        this.GBKFreq[48][126] = FrameMetricsAggregator.EVERY_DURATION;
        this.GBKFreq[67][170] = 510;
        this.GBKFreq[43][6] = 509;
        this.GBKFreq[70][112] = 508;
        this.GBKFreq[86][174] = 507;
        this.GBKFreq[84][166] = 506;
        this.GBKFreq[79][130] = 505;
        this.GBKFreq[57][141] = 504;
        this.GBKFreq[81][178] = 503;
        this.GBKFreq[56][187] = 502;
        this.GBKFreq[81][162] = 501;
        this.GBKFreq[53][104] = 500;
        this.GBKFreq[123][35] = 499;
        this.GBKFreq[70][169] = 498;
        this.GBKFreq[69][164] = 497;
        this.GBKFreq[109][61] = 496;
        this.GBKFreq[73][130] = 495;
        this.GBKFreq[62][134] = 494;
        this.GBKFreq[54][125] = 493;
        this.GBKFreq[79][105] = 492;
        this.GBKFreq[70][165] = 491;
        this.GBKFreq[71][189] = 490;
        this.GBKFreq[23][147] = 489;
        this.GBKFreq[51][139] = 488;
        this.GBKFreq[47][137] = 487;
        this.GBKFreq[77][123] = 486;
        this.GBKFreq[86][183] = 485;
        this.GBKFreq[63][173] = 484;
        this.GBKFreq[79][144] = 483;
        this.GBKFreq[84][159] = 482;
        this.GBKFreq[60][91] = 481;
        this.GBKFreq[66][187] = 480;
        this.GBKFreq[73][114] = 479;
        this.GBKFreq[85][56] = 478;
        this.GBKFreq[71][149] = 477;
        this.GBKFreq[84][189] = 476;
        this.GBKFreq[104][31] = 475;
        this.GBKFreq[83][82] = 474;
        this.GBKFreq[68][35] = 473;
        this.GBKFreq[11][77] = 472;
        this.GBKFreq[15][155] = 471;
        this.GBKFreq[83][153] = 470;
        this.GBKFreq[71][1] = 469;
        this.GBKFreq[53][190] = 468;
        this.GBKFreq[50][135] = 467;
        this.GBKFreq[3][147] = 466;
        this.GBKFreq[48][136] = 465;
        this.GBKFreq[66][166] = 464;
        this.GBKFreq[55][159] = 463;
        this.GBKFreq[82][150] = 462;
        this.GBKFreq[58][178] = 461;
        this.GBKFreq[64][102] = 460;
        this.GBKFreq[16][106] = 459;
        this.GBKFreq[68][110] = 458;
        this.GBKFreq[54][14] = 457;
        this.GBKFreq[60][140] = 456;
        this.GBKFreq[91][71] = 455;
        this.GBKFreq[54][150] = 454;
        this.GBKFreq[78][177] = 453;
        this.GBKFreq[78][117] = 452;
        this.GBKFreq[104][12] = 451;
        this.GBKFreq[73][150] = 450;
        this.GBKFreq[51][142] = 449;
        this.GBKFreq[81][145] = 448;
        this.GBKFreq[66][183] = 447;
        this.GBKFreq[51][178] = 446;
        this.GBKFreq[75][107] = SmbConstants.DEFAULT_PORT;
        this.GBKFreq[65][119] = 444;
        this.GBKFreq[69][176] = Handler.DEFAULT_HTTPS_PORT;
        this.GBKFreq[59][122] = 442;
        this.GBKFreq[78][160] = 441;
        this.GBKFreq[85][183] = 440;
        this.GBKFreq[105][16] = 439;
        this.GBKFreq[73][110] = 438;
        this.GBKFreq[104][39] = 437;
        this.GBKFreq[119][16] = 436;
        this.GBKFreq[76][162] = 435;
        this.GBKFreq[67][152] = 434;
        this.GBKFreq[82][24] = 433;
        this.GBKFreq[73][121] = 432;
        this.GBKFreq[83][83] = 431;
        this.GBKFreq[82][145] = 430;
        this.GBKFreq[49][133] = 429;
        this.GBKFreq[94][13] = 428;
        this.GBKFreq[58][139] = 427;
        this.GBKFreq[74][189] = 426;
        this.GBKFreq[66][177] = 425;
        this.GBKFreq[85][184] = 424;
        this.GBKFreq[55][183] = 423;
        this.GBKFreq[71][107] = 422;
        this.GBKFreq[11][98] = 421;
        this.GBKFreq[72][153] = 420;
        this.GBKFreq[2][137] = 419;
        this.GBKFreq[59][147] = 418;
        this.GBKFreq[58][152] = 417;
        this.GBKFreq[55][144] = HTTPStatus.INVALID_RANGE;
        this.GBKFreq[73][125] = 415;
        this.GBKFreq[52][154] = 414;
        this.GBKFreq[70][178] = 413;
        this.GBKFreq[79][148] = HTTPStatus.PRECONDITION_FAILED;
        this.GBKFreq[63][NbtException.UNSPECIFIED] = 411;
        this.GBKFreq[50][140] = 410;
        this.GBKFreq[47][145] = 409;
        this.GBKFreq[48][123] = 408;
        this.GBKFreq[56][107] = 407;
        this.GBKFreq[84][83] = 406;
        this.GBKFreq[59][112] = 405;
        this.GBKFreq[124][72] = HTTPStatus.NOT_FOUND;
        this.GBKFreq[79][99] = 403;
        this.GBKFreq[3][37] = 402;
        this.GBKFreq[114][55] = 401;
        this.GBKFreq[85][152] = HTTPStatus.BAD_REQUEST;
        this.GBKFreq[60][47] = 399;
        this.GBKFreq[65][96] = 398;
        this.GBKFreq[74][110] = 397;
        this.GBKFreq[86][182] = 396;
        this.GBKFreq[50][99] = 395;
        this.GBKFreq[67][186] = 394;
        this.GBKFreq[81][74] = 393;
        this.GBKFreq[80][37] = 392;
        this.GBKFreq[21][60] = 391;
        this.GBKFreq[110][12] = 390;
        this.GBKFreq[60][162] = 389;
        this.GBKFreq[29][115] = 388;
        this.GBKFreq[83][130] = 387;
        this.GBKFreq[52][136] = 386;
        this.GBKFreq[63][114] = 385;
        this.GBKFreq[49][127] = 384;
        this.GBKFreq[83][109] = 383;
        this.GBKFreq[66][128] = 382;
        this.GBKFreq[78][136] = 381;
        this.GBKFreq[81][DefaultGestureRecogniser.SHOW_PRESS_TIME] = 380;
        this.GBKFreq[76][104] = 379;
        this.GBKFreq[56][156] = 378;
        this.GBKFreq[61][23] = 377;
        this.GBKFreq[4][30] = 376;
        this.GBKFreq[69][154] = 375;
        this.GBKFreq[100][37] = 374;
        this.GBKFreq[54][177] = 373;
        this.GBKFreq[23][119] = 372;
        this.GBKFreq[71][171] = 371;
        this.GBKFreq[84][146] = 370;
        this.GBKFreq[20][184] = 369;
        this.GBKFreq[86][76] = 368;
        this.GBKFreq[74][132] = 367;
        this.GBKFreq[47][97] = 366;
        this.GBKFreq[82][137] = 365;
        this.GBKFreq[94][56] = 364;
        this.GBKFreq[92][30] = 363;
        this.GBKFreq[19][117] = 362;
        this.GBKFreq[48][173] = 361;
        this.GBKFreq[2][136] = 360;
        this.GBKFreq[7][182] = 359;
        this.GBKFreq[74][188] = 358;
        this.GBKFreq[14][132] = 357;
        this.GBKFreq[62][172] = 356;
        this.GBKFreq[25][39] = 355;
        this.GBKFreq[85][NbtException.NOT_LISTENING_CALLING] = 354;
        this.GBKFreq[64][98] = 353;
        this.GBKFreq[67][127] = 352;
        this.GBKFreq[72][167] = 351;
        this.GBKFreq[57][NbtException.UNSPECIFIED] = 350;
        this.GBKFreq[76][187] = 349;
        this.GBKFreq[83][181] = 348;
        this.GBKFreq[84][10] = 347;
        this.GBKFreq[55][166] = 346;
        this.GBKFreq[55][188] = 345;
        this.GBKFreq[13][151] = 344;
        this.GBKFreq[62][124] = 343;
        this.GBKFreq[53][136] = 342;
        this.GBKFreq[106][57] = 341;
        this.GBKFreq[47][166] = 340;
        this.GBKFreq[109][30] = 339;
        this.GBKFreq[78][114] = 338;
        this.GBKFreq[83][19] = 337;
        this.GBKFreq[56][162] = 336;
        this.GBKFreq[60][177] = 335;
        this.GBKFreq[88][9] = 334;
        this.GBKFreq[74][163] = 333;
        this.GBKFreq[52][156] = 332;
        this.GBKFreq[71][DefaultGestureRecogniser.SHOW_PRESS_TIME] = 331;
        this.GBKFreq[60][57] = 330;
        this.GBKFreq[72][173] = 329;
        this.GBKFreq[82][91] = 328;
        this.GBKFreq[51][186] = 327;
        this.GBKFreq[75][86] = 326;
        this.GBKFreq[75][78] = 325;
        this.GBKFreq[76][170] = 324;
        this.GBKFreq[60][147] = 323;
        this.GBKFreq[82][75] = 322;
        this.GBKFreq[80][148] = 321;
        this.GBKFreq[86][150] = 320;
        this.GBKFreq[13][95] = 319;
        this.GBKFreq[0][11] = 318;
        this.GBKFreq[84][190] = 317;
        this.GBKFreq[76][166] = 316;
        this.GBKFreq[14][72] = 315;
        this.GBKFreq[67][144] = 314;
        this.GBKFreq[84][44] = 313;
        this.GBKFreq[72][125] = 312;
        this.GBKFreq[66][127] = 311;
        this.GBKFreq[60][25] = 310;
        this.GBKFreq[70][146] = 309;
        this.GBKFreq[79][135] = 308;
        this.GBKFreq[54][135] = 307;
        this.GBKFreq[60][104] = 306;
        this.GBKFreq[55][132] = 305;
        this.GBKFreq[94][2] = 304;
        this.GBKFreq[54][133] = 303;
        this.GBKFreq[56][190] = 302;
        this.GBKFreq[58][174] = 301;
        this.GBKFreq[80][144] = 300;
        this.GBKFreq[85][113] = 299;
    }
}
