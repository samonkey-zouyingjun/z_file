package com.zidoo.custom.time;

import com.zidoo.fileexplorer.BuildConfig;
import java.util.Calendar;
import jcifs.netbios.NbtException;
import jcifs.smb.WinError;
import zidoo.http.HTTPStatus;
import zidoo.tarot.kernel.input.DefaultGestureRecogniser;
import zidoo.tarot.kernel.shader.ShaderParameter;

class SolarTermsUtil {
    private static int baseChineseDate = 11;
    private static int baseChineseMonth = 11;
    private static int baseChineseYear = 4597;
    private static int baseDate = 1;
    private static int baseIndex = 0;
    private static int baseMonth = 1;
    private static int baseYear = 1901;
    private static int[] bigLeapMonthYears = new int[]{6, 14, 19, 25, 33, 36, 38, 41, 44, 52, 55, 79, 117, 136, 147, 150, 155, 158, BuildConfig.VERSION_CODE, 193};
    private static char[] chineseMonths;
    private static char[] daysInGregorianMonth = new char[]{'\u001f', '\u001c', '\u001f', '\u001e', '\u001f', '\u001e', '\u001f', '\u001f', '\u001e', '\u001f', '\u001e', '\u001f'};
    private static char[][] principleTermMap = new char[][]{new char[]{'\u0015', '\u0015', '\u0015', '\u0015', '\u0015', '\u0014', '\u0015', '\u0015', '\u0015', '\u0014', '\u0014', '\u0015', '\u0015', '\u0014', '\u0014', '\u0014', '\u0014', '\u0014', '\u0014', '\u0014', '\u0014', '\u0013', '\u0014', '\u0014', '\u0014', '\u0013', '\u0013', '\u0014'}, new char[]{'\u0014', '\u0013', '\u0013', '\u0014', '\u0014', '\u0013', '\u0013', '\u0013', '\u0013', '\u0013', '\u0013', '\u0013', '\u0013', '\u0012', '\u0013', '\u0013', '\u0013', '\u0012', '\u0012', '\u0013', '\u0013', '\u0012', '\u0012', '\u0012', '\u0012', '\u0012', '\u0012', '\u0012'}, new char[]{'\u0015', '\u0015', '\u0015', '\u0016', '\u0015', '\u0015', '\u0015', '\u0015', '\u0014', '\u0015', '\u0015', '\u0015', '\u0014', '\u0014', '\u0015', '\u0015', '\u0014', '\u0014', '\u0014', '\u0015', '\u0014', '\u0014', '\u0014', '\u0014', '\u0013', '\u0014', '\u0014', '\u0014', '\u0014'}, new char[]{'\u0014', '\u0015', '\u0015', '\u0015', '\u0014', '\u0014', '\u0015', '\u0015', '\u0014', '\u0014', '\u0014', '\u0015', '\u0014', '\u0014', '\u0014', '\u0014', '\u0013', '\u0014', '\u0014', '\u0014', '\u0013', '\u0013', '\u0014', '\u0014', '\u0013', '\u0013', '\u0013', '\u0014', '\u0014'}, new char[]{'\u0015', '\u0016', '\u0016', '\u0016', '\u0015', '\u0015', '\u0016', '\u0016', '\u0015', '\u0015', '\u0015', '\u0016', '\u0015', '\u0015', '\u0015', '\u0015', '\u0014', '\u0015', '\u0015', '\u0015', '\u0014', '\u0014', '\u0015', '\u0015', '\u0014', '\u0014', '\u0014', '\u0015', '\u0015'}, new char[]{'\u0016', '\u0016', '\u0016', '\u0016', '\u0015', '\u0016', '\u0016', '\u0016', '\u0015', '\u0015', '\u0016', '\u0016', '\u0015', '\u0015', '\u0015', '\u0016', '\u0015', '\u0015', '\u0015', '\u0015', '\u0014', '\u0015', '\u0015', '\u0015', '\u0014', '\u0014', '\u0015', '\u0015', '\u0015'}, new char[]{'\u0017', '\u0017', '\u0018', '\u0018', '\u0017', '\u0017', '\u0017', '\u0018', '\u0017', '\u0017', '\u0017', '\u0017', '\u0016', '\u0017', '\u0017', '\u0017', '\u0016', '\u0016', '\u0017', '\u0017', '\u0016', '\u0016', '\u0016', '\u0017', '\u0016', '\u0016', '\u0016', '\u0016', '\u0017'}, new char[]{'\u0017', '\u0018', '\u0018', '\u0018', '\u0017', '\u0017', '\u0018', '\u0018', '\u0017', '\u0017', '\u0017', '\u0018', '\u0017', '\u0017', '\u0017', '\u0017', '\u0016', '\u0017', '\u0017', '\u0017', '\u0016', '\u0016', '\u0017', '\u0017', '\u0016', '\u0016', '\u0016', '\u0017', '\u0017'}, new char[]{'\u0017', '\u0018', '\u0018', '\u0018', '\u0017', '\u0017', '\u0018', '\u0018', '\u0017', '\u0017', '\u0017', '\u0018', '\u0017', '\u0017', '\u0017', '\u0017', '\u0016', '\u0017', '\u0017', '\u0017', '\u0016', '\u0016', '\u0017', '\u0017', '\u0016', '\u0016', '\u0016', '\u0017', '\u0017'}, new char[]{'\u0018', '\u0018', '\u0018', '\u0018', '\u0017', '\u0018', '\u0018', '\u0018', '\u0017', '\u0017', '\u0018', '\u0018', '\u0017', '\u0017', '\u0017', '\u0018', '\u0017', '\u0017', '\u0017', '\u0017', '\u0016', '\u0017', '\u0017', '\u0017', '\u0016', '\u0016', '\u0017', '\u0017', '\u0017'}, new char[]{'\u0017', '\u0017', '\u0017', '\u0017', '\u0016', '\u0017', '\u0017', '\u0017', '\u0016', '\u0016', '\u0017', '\u0017', '\u0016', '\u0016', '\u0016', '\u0017', '\u0016', '\u0016', '\u0016', '\u0016', '\u0015', '\u0016', '\u0016', '\u0016', '\u0015', '\u0015', '\u0016', '\u0016', '\u0016'}, new char[]{'\u0016', '\u0016', '\u0017', '\u0017', '\u0016', '\u0016', '\u0016', '\u0017', '\u0016', '\u0016', '\u0016', '\u0016', '\u0015', '\u0016', '\u0016', '\u0016', '\u0015', '\u0015', '\u0016', '\u0016', '\u0015', '\u0015', '\u0015', '\u0016', '\u0015', '\u0015', '\u0015', '\u0015', '\u0016'}};
    private static String[] principleTermNames = new String[]{"大寒", "雨水", "春分", "谷雨", "小满", "夏至", "大暑", "处暑", "秋分", "霜降", "小雪", "冬至"};
    private static char[][] principleTermYear = new char[][]{new char[]{'\r', '-', 'Q', 'q', '', '¹', 'É'}, new char[]{'\u0015', '9', ']', '}', '¡', 'Á', 'É'}, new char[]{'\u0015', '8', 'X', 'x', '', '¼', 'È', 'É'}, new char[]{'\u0015', '1', 'Q', 't', '', '°', 'È', 'É'}, new char[]{'\u0011', '1', 'M', 'p', '', '¨', 'È', 'É'}, new char[]{'\u001c', '<', 'X', 't', '', '´', 'È', 'É'}, new char[]{'\u0019', '5', 'T', 'p', '', '¬', 'È', 'É'}, new char[]{'\u001d', '9', 'Y', 'x', '', '´', 'È', 'É'}, new char[]{'\u0011', '-', 'I', 'l', '', '¨', 'È', 'É'}, new char[]{'\u001c', '<', '\\', '|', ' ', 'À', 'È', 'É'}, new char[]{'\u0010', ',', 'P', 'p', '', '´', 'È', 'É'}, new char[]{'\u0011', '5', 'X', 'x', '', '¼', 'È', 'É'}};
    private static char[][] sectionalTermMap = new char[][]{new char[]{'\u0007', '\u0006', '\u0006', '\u0006', '\u0006', '\u0006', '\u0006', '\u0006', '\u0006', '\u0005', '\u0006', '\u0006', '\u0006', '\u0005', '\u0005', '\u0006', '\u0006', '\u0005', '\u0005', '\u0005', '\u0005', '\u0005', '\u0005', '\u0005', '\u0005', '\u0004', '\u0005', '\u0005'}, new char[]{'\u0005', '\u0004', '\u0005', '\u0005', '\u0005', '\u0004', '\u0004', '\u0005', '\u0005', '\u0004', '\u0004', '\u0004', '\u0004', '\u0004', '\u0004', '\u0004', '\u0004', '\u0003', '\u0004', '\u0004', '\u0004', '\u0003', '\u0003', '\u0004', '\u0004', '\u0003', '\u0003', '\u0003'}, new char[]{'\u0006', '\u0006', '\u0006', '\u0007', '\u0006', '\u0006', '\u0006', '\u0006', '\u0005', '\u0006', '\u0006', '\u0006', '\u0005', '\u0005', '\u0006', '\u0006', '\u0005', '\u0005', '\u0005', '\u0006', '\u0005', '\u0005', '\u0005', '\u0005', '\u0004', '\u0005', '\u0005', '\u0005', '\u0005'}, new char[]{'\u0005', '\u0005', '\u0006', '\u0006', '\u0005', '\u0005', '\u0005', '\u0006', '\u0005', '\u0005', '\u0005', '\u0005', '\u0004', '\u0005', '\u0005', '\u0005', '\u0004', '\u0004', '\u0005', '\u0005', '\u0004', '\u0004', '\u0004', '\u0005', '\u0004', '\u0004', '\u0004', '\u0004', '\u0005'}, new char[]{'\u0006', '\u0006', '\u0006', '\u0007', '\u0006', '\u0006', '\u0006', '\u0006', '\u0005', '\u0006', '\u0006', '\u0006', '\u0005', '\u0005', '\u0006', '\u0006', '\u0005', '\u0005', '\u0005', '\u0006', '\u0005', '\u0005', '\u0005', '\u0005', '\u0004', '\u0005', '\u0005', '\u0005', '\u0005'}, new char[]{'\u0006', '\u0006', '\u0007', '\u0007', '\u0006', '\u0006', '\u0006', '\u0007', '\u0006', '\u0006', '\u0006', '\u0006', '\u0005', '\u0006', '\u0006', '\u0006', '\u0005', '\u0005', '\u0006', '\u0006', '\u0005', '\u0005', '\u0005', '\u0006', '\u0005', '\u0005', '\u0005', '\u0005', '\u0004', '\u0005', '\u0005', '\u0005', '\u0005'}, new char[]{'\u0007', '\b', '\b', '\b', '\u0007', '\u0007', '\b', '\b', '\u0007', '\u0007', '\u0007', '\b', '\u0007', '\u0007', '\u0007', '\u0007', '\u0006', '\u0007', '\u0007', '\u0007', '\u0006', '\u0006', '\u0007', '\u0007', '\u0006', '\u0006', '\u0006', '\u0007', '\u0007'}, new char[]{'\b', '\b', '\b', '\t', '\b', '\b', '\b', '\b', '\u0007', '\b', '\b', '\b', '\u0007', '\u0007', '\b', '\b', '\u0007', '\u0007', '\u0007', '\b', '\u0007', '\u0007', '\u0007', '\u0007', '\u0006', '\u0007', '\u0007', '\u0007', '\u0006', '\u0006', '\u0007', '\u0007', '\u0007'}, new char[]{'\b', '\b', '\b', '\t', '\b', '\b', '\b', '\b', '\u0007', '\b', '\b', '\b', '\u0007', '\u0007', '\b', '\b', '\u0007', '\u0007', '\u0007', '\b', '\u0007', '\u0007', '\u0007', '\u0007', '\u0006', '\u0007', '\u0007', '\u0007', '\u0007'}, new char[]{'\t', '\t', '\t', '\t', '\b', '\t', '\t', '\t', '\b', '\b', '\t', '\t', '\b', '\b', '\b', '\t', '\b', '\b', '\b', '\b', '\u0007', '\b', '\b', '\b', '\u0007', '\u0007', '\b', '\b', '\b'}, new char[]{'\b', '\b', '\b', '\b', '\u0007', '\b', '\b', '\b', '\u0007', '\u0007', '\b', '\b', '\u0007', '\u0007', '\u0007', '\b', '\u0007', '\u0007', '\u0007', '\u0007', '\u0006', '\u0007', '\u0007', '\u0007', '\u0006', '\u0006', '\u0007', '\u0007', '\u0007'}, new char[]{'\u0007', '\b', '\b', '\b', '\u0007', '\u0007', '\b', '\b', '\u0007', '\u0007', '\u0007', '\b', '\u0007', '\u0007', '\u0007', '\u0007', '\u0006', '\u0007', '\u0007', '\u0007', '\u0006', '\u0006', '\u0007', '\u0007', '\u0006', '\u0006', '\u0006', '\u0007', '\u0007'}};
    private static String[] sectionalTermNames = new String[]{"小寒", "立春", "惊蛰", "清明", "立夏", "芒种", "小暑", "立秋", "白露", "寒露", "立冬", "大雪"};
    private static char[][] sectionalTermYear = new char[][]{new char[]{'\r', '1', 'U', 'u', '', '¹', 'É', 'ú', 'ú'}, new char[]{'\r', '-', 'Q', 'u', '', '¹', 'É', 'ú', 'ú'}, new char[]{'\r', '0', 'T', 'p', '', '¸', 'È', 'É', 'ú'}, new char[]{'\r', '-', 'L', 'l', '', '¬', 'È', 'É', 'ú'}, new char[]{'\r', ',', 'H', 'h', '', '¨', 'È', 'É', 'ú'}, new char[]{'\u0005', '!', 'D', '`', '|', '', '¼', 'È', 'É'}, new char[]{'\u001d', '9', 'U', 'x', '', '°', 'È', 'É', 'ú'}, new char[]{'\r', '0', 'L', 'h', '', '¨', 'Ä', 'È', 'É'}, new char[]{'\u0019', '<', 'X', 'x', '', '¸', 'È', 'É', 'ú'}, new char[]{'\u0010', ',', 'L', 'l', '', '¬', 'È', 'É', 'ú'}, new char[]{'\u001c', '<', '\\', '|', ' ', 'À', 'È', 'É', 'ú'}, new char[]{'\u0011', '5', 'U', '|', '', '¼', 'È', 'É', 'ú'}};
    private int chineseDate;
    private int chineseMonth;
    private int chineseYear;
    private int gregorianDate;
    private int gregorianMonth;
    private int gregorianYear;
    private int principleTerm;
    private int sectionalTerm;

    static {
        char[] cArr = new char[402];
        cArr[1] = '\u0004';
        cArr[2] = '­';
        cArr[3] = '\b';
        cArr[4] = 'Z';
        cArr[5] = '\u0001';
        cArr[6] = 'Õ';
        cArr[7] = 'T';
        cArr[8] = '´';
        cArr[9] = '\t';
        cArr[10] = 'd';
        cArr[11] = '\u0005';
        cArr[12] = 'Y';
        cArr[13] = 'E';
        cArr[14] = '';
        cArr[15] = '\n';
        cArr[16] = '¦';
        cArr[17] = '\u0004';
        cArr[18] = 'U';
        cArr[19] = '$';
        cArr[20] = '­';
        cArr[21] = '\b';
        cArr[22] = 'Z';
        cArr[23] = 'b';
        cArr[24] = 'Ú';
        cArr[25] = '\u0004';
        cArr[26] = '´';
        cArr[27] = '\u0005';
        cArr[28] = '´';
        cArr[29] = 'U';
        cArr[30] = 'R';
        cArr[31] = '\r';
        cArr[32] = '';
        cArr[33] = '\n';
        cArr[34] = 'J';
        cArr[35] = '*';
        cArr[36] = 'V';
        cArr[37] = '\u0002';
        cArr[38] = 'm';
        cArr[39] = 'q';
        cArr[40] = 'm';
        cArr[41] = '\u0001';
        cArr[42] = 'Ú';
        cArr[43] = '\u0002';
        cArr[44] = 'Ò';
        cArr[45] = 'R';
        cArr[46] = '©';
        cArr[47] = '\u0005';
        cArr[48] = 'I';
        cArr[49] = '\r';
        cArr[50] = '*';
        cArr[51] = 'E';
        cArr[52] = '+';
        cArr[53] = '\t';
        cArr[54] = 'V';
        cArr[55] = '\u0001';
        cArr[56] = 'µ';
        cArr[57] = ' ';
        cArr[58] = 'm';
        cArr[59] = '\u0001';
        cArr[60] = 'Y';
        cArr[61] = 'i';
        cArr[62] = 'Ô';
        cArr[63] = '\n';
        cArr[64] = '¨';
        cArr[65] = '\u0005';
        cArr[66] = '©';
        cArr[67] = 'V';
        cArr[68] = '¥';
        cArr[69] = '\u0004';
        cArr[70] = '+';
        cArr[71] = '\t';
        cArr[72] = '';
        cArr[73] = '8';
        cArr[74] = '¶';
        cArr[75] = '\b';
        cArr[76] = 'ì';
        cArr[77] = 't';
        cArr[78] = 'l';
        cArr[79] = '\u0005';
        cArr[80] = 'Ô';
        cArr[81] = '\n';
        cArr[82] = 'ä';
        cArr[83] = 'j';
        cArr[84] = 'R';
        cArr[85] = '\u0005';
        cArr[86] = '';
        cArr[87] = '\n';
        cArr[88] = 'Z';
        cArr[89] = 'B';
        cArr[90] = '[';
        cArr[91] = '\u0004';
        cArr[92] = '¶';
        cArr[93] = '\u0004';
        cArr[94] = '´';
        cArr[95] = '\"';
        cArr[96] = 'j';
        cArr[97] = '\u0005';
        cArr[98] = 'R';
        cArr[99] = 'u';
        cArr[100] = 'É';
        cArr[101] = '\n';
        cArr[102] = 'R';
        cArr[103] = '\u0005';
        cArr[104] = '5';
        cArr[105] = 'U';
        cArr[106] = 'M';
        cArr[107] = '\n';
        cArr[108] = 'Z';
        cArr[109] = '\u0002';
        cArr[110] = ']';
        cArr[111] = '1';
        cArr[112] = 'µ';
        cArr[113] = '\u0002';
        cArr[114] = 'j';
        cArr[115] = '';
        cArr[116] = 'h';
        cArr[117] = '\u0005';
        cArr[118] = '©';
        cArr[119] = '\n';
        cArr[120] = '';
        cArr[121] = 'j';
        cArr[122] = '*';
        cArr[123] = '\u0005';
        cArr[124] = '-';
        cArr[125] = '\t';
        cArr[126] = 'ª';
        cArr[127] = 'H';
        cArr[128] = 'Z';
        cArr[NbtException.NOT_LISTENING_CALLING] = '\u0001';
        cArr[130] = 'µ';
        cArr[131] = '\t';
        cArr[132] = '°';
        cArr[133] = '9';
        cArr[134] = 'd';
        cArr[135] = '\u0005';
        cArr[136] = '%';
        cArr[137] = 'u';
        cArr[138] = '';
        cArr[139] = '\n';
        cArr[140] = '';
        cArr[141] = '\u0004';
        cArr[142] = 'M';
        cArr[NbtException.UNSPECIFIED] = 'T';
        cArr[144] = '­';
        cArr[145] = '\u0004';
        cArr[146] = 'Ú';
        cArr[147] = '\u0004';
        cArr[148] = 'Ô';
        cArr[149] = 'D';
        cArr[150] = '´';
        cArr[151] = '\u0005';
        cArr[152] = 'T';
        cArr[153] = '';
        cArr[154] = 'R';
        cArr[155] = '\r';
        cArr[156] = '';
        cArr[157] = '\n';
        cArr[158] = 'V';
        cArr[159] = 'j';
        cArr[160] = 'V';
        cArr[161] = '\u0002';
        cArr[162] = 'm';
        cArr[163] = '\u0002';
        cArr[164] = 'j';
        cArr[165] = 'A';
        cArr[166] = 'Ú';
        cArr[167] = '\u0002';
        cArr[168] = '²';
        cArr[169] = '¡';
        cArr[170] = '©';
        cArr[171] = '\u0005';
        cArr[172] = 'I';
        cArr[173] = '\r';
        cArr[174] = '\n';
        cArr[175] = 'm';
        cArr[176] = '*';
        cArr[177] = '\t';
        cArr[178] = 'V';
        cArr[179] = '\u0001';
        cArr[DefaultGestureRecogniser.SHOW_PRESS_TIME] = '­';
        cArr[181] = 'P';
        cArr[182] = 'm';
        cArr[183] = '\u0001';
        cArr[184] = 'Ù';
        cArr[BuildConfig.VERSION_CODE] = '\u0002';
        cArr[186] = 'Ñ';
        cArr[187] = ':';
        cArr[188] = '¨';
        cArr[189] = '\u0005';
        cArr[190] = ')';
        cArr[191] = '';
        cArr[192] = '¥';
        cArr[193] = '\f';
        cArr[194] = '*';
        cArr[195] = '\t';
        cArr[196] = '';
        cArr[197] = 'T';
        cArr[198] = '¶';
        cArr[199] = '\b';
        cArr[200] = 'l';
        cArr[201] = '\t';
        cArr[202] = 'd';
        cArr[203] = 'E';
        cArr[204] = 'Ô';
        cArr[205] = '\n';
        cArr[HTTPStatus.PARTIAL_CONTENT] = '¤';
        cArr[207] = '\u0005';
        cArr[208] = 'Q';
        cArr[209] = '%';
        cArr[210] = '';
        cArr[211] = '\n';
        cArr[212] = '*';
        cArr[213] = 'r';
        cArr[214] = '[';
        cArr[215] = '\u0004';
        cArr[216] = '¶';
        cArr[217] = '\u0004';
        cArr[218] = '¬';
        cArr[219] = 'R';
        cArr[220] = 'j';
        cArr[221] = '\u0005';
        cArr[222] = 'Ò';
        cArr[223] = '\n';
        cArr[224] = '¢';
        cArr[225] = 'J';
        cArr[226] = 'J';
        cArr[227] = '\u0005';
        cArr[228] = 'U';
        cArr[229] = '';
        cArr[WinError.ERROR_BAD_PIPE] = '-';
        cArr[WinError.ERROR_PIPE_BUSY] = '\n';
        cArr[WinError.ERROR_NO_DATA] = 'Z';
        cArr[WinError.ERROR_PIPE_NOT_CONNECTED] = '\u0002';
        cArr[WinError.ERROR_MORE_DATA] = 'u';
        cArr[235] = 'a';
        cArr[236] = 'µ';
        cArr[237] = '\u0002';
        cArr[238] = 'j';
        cArr[239] = '\u0003';
        cArr[240] = 'a';
        cArr[241] = 'E';
        cArr[242] = '©';
        cArr[243] = '\n';
        cArr[244] = 'J';
        cArr[245] = '\u0005';
        cArr[246] = '%';
        cArr[247] = '%';
        cArr[248] = '-';
        cArr[249] = '\t';
        cArr[250] = '';
        cArr[251] = 'h';
        cArr[252] = 'Ú';
        cArr[253] = '\b';
        cArr[254] = '´';
        cArr[255] = '\t';
        cArr[256] = '¨';
        cArr[257] = 'Y';
        cArr[ShaderParameter.ATTRIBUTE_PARAMETER] = 'T';
        cArr[ShaderParameter.VARYING_PARAMETER] = '\u0003';
        cArr[260] = '¥';
        cArr[261] = '\n';
        cArr[262] = '';
        cArr[263] = ':';
        cArr[264] = '';
        cArr[265] = '\u0004';
        cArr[266] = '­';
        cArr[267] = '°';
        cArr[268] = '­';
        cArr[269] = '\u0004';
        cArr[270] = 'Ú';
        cArr[271] = '\u0004';
        cArr[272] = 'ô';
        cArr[273] = 'b';
        cArr[274] = '´';
        cArr[275] = '\u0005';
        cArr[276] = 'T';
        cArr[277] = '\u000b';
        cArr[278] = 'D';
        cArr[279] = ']';
        cArr[280] = 'R';
        cArr[281] = '\n';
        cArr[282] = '';
        cArr[283] = '\u0004';
        cArr[284] = 'U';
        cArr[285] = '\"';
        cArr[286] = 'm';
        cArr[287] = '\u0002';
        cArr[288] = 'Z';
        cArr[289] = 'q';
        cArr[290] = 'Ú';
        cArr[291] = '\u0002';
        cArr[292] = 'ª';
        cArr[293] = '\u0005';
        cArr[294] = '²';
        cArr[295] = 'U';
        cArr[296] = 'I';
        cArr[297] = '\u000b';
        cArr[298] = 'J';
        cArr[299] = '\n';
        cArr[300] = '-';
        cArr[301] = '9';
        cArr[302] = '6';
        cArr[303] = '\u0001';
        cArr[304] = 'm';
        cArr[305] = '';
        cArr[306] = 'm';
        cArr[307] = '\u0001';
        cArr[308] = 'Ù';
        cArr[309] = '\u0002';
        cArr[310] = 'é';
        cArr[311] = 'j';
        cArr[312] = '¨';
        cArr[313] = '\u0005';
        cArr[314] = ')';
        cArr[315] = '\u000b';
        cArr[316] = '';
        cArr[317] = 'L';
        cArr[318] = 'ª';
        cArr[319] = '\b';
        cArr[320] = '¶';
        cArr[321] = '\b';
        cArr[322] = '´';
        cArr[323] = '8';
        cArr[324] = 'l';
        cArr[325] = '\t';
        cArr[326] = 'T';
        cArr[327] = 'u';
        cArr[328] = 'Ô';
        cArr[329] = '\n';
        cArr[330] = '¤';
        cArr[331] = '\u0005';
        cArr[332] = 'E';
        cArr[333] = 'U';
        cArr[334] = '';
        cArr[335] = '\n';
        cArr[336] = '';
        cArr[337] = '\u0004';
        cArr[338] = 'U';
        cArr[339] = 'D';
        cArr[340] = 'µ';
        cArr[341] = '\u0004';
        cArr[342] = 'j';
        cArr[343] = '';
        cArr[344] = 'j';
        cArr[345] = '\u0005';
        cArr[346] = 'Ò';
        cArr[347] = '\n';
        cArr[348] = '';
        cArr[349] = 'j';
        cArr[350] = 'J';
        cArr[351] = '\u0005';
        cArr[352] = 'U';
        cArr[353] = '\n';
        cArr[354] = '*';
        cArr[355] = 'J';
        cArr[356] = 'Z';
        cArr[357] = '\u0002';
        cArr[358] = 'µ';
        cArr[359] = '\u0002';
        cArr[360] = '²';
        cArr[361] = '1';
        cArr[362] = 'i';
        cArr[363] = '\u0003';
        cArr[364] = '1';
        cArr[365] = 's';
        cArr[366] = '©';
        cArr[367] = '\n';
        cArr[368] = 'J';
        cArr[369] = '\u0005';
        cArr[370] = '-';
        cArr[371] = 'U';
        cArr[372] = '-';
        cArr[373] = '\t';
        cArr[374] = 'Z';
        cArr[375] = '\u0001';
        cArr[376] = 'Õ';
        cArr[377] = 'H';
        cArr[378] = '´';
        cArr[379] = '\t';
        cArr[380] = 'h';
        cArr[381] = '';
        cArr[382] = 'T';
        cArr[383] = '\u000b';
        cArr[384] = '¤';
        cArr[385] = '\n';
        cArr[386] = '¥';
        cArr[387] = 'j';
        cArr[388] = '';
        cArr[389] = '\u0004';
        cArr[390] = '­';
        cArr[391] = '\b';
        cArr[392] = 'j';
        cArr[393] = 'D';
        cArr[394] = 'Ú';
        cArr[395] = '\u0004';
        cArr[396] = 't';
        cArr[397] = '\u0005';
        cArr[398] = '°';
        cArr[399] = '%';
        cArr[HTTPStatus.BAD_REQUEST] = 'T';
        cArr[401] = '\u0003';
        chineseMonths = cArr;
    }

    public SolarTermsUtil(Calendar calendar) {
        this.gregorianYear = calendar.get(1);
        this.gregorianMonth = calendar.get(2) + 1;
        this.gregorianDate = calendar.get(5);
        computeChineseFields();
        computeSolarTerms();
    }

    public int computeChineseFields() {
        if (this.gregorianYear < 1901 || this.gregorianYear > 2100) {
            return 1;
        }
        int i;
        int startYear = baseYear;
        int startMonth = baseMonth;
        int startDate = baseDate;
        this.chineseYear = baseChineseYear;
        this.chineseMonth = baseChineseMonth;
        this.chineseDate = baseChineseDate;
        if (this.gregorianYear >= 2000) {
            startYear = baseYear + 99;
            startMonth = 1;
            startDate = 1;
            this.chineseYear = baseChineseYear + 99;
            this.chineseMonth = 11;
            this.chineseDate = 25;
        }
        int daysDiff = 0;
        for (i = startYear; i < this.gregorianYear; i++) {
            daysDiff += 365;
            if (isGregorianLeapYear(i)) {
                daysDiff++;
            }
        }
        for (i = startMonth; i < this.gregorianMonth; i++) {
            daysDiff += daysInGregorianMonth(this.gregorianYear, i);
        }
        this.chineseDate += daysDiff + (this.gregorianDate - startDate);
        int lastDate = daysInChineseMonth(this.chineseYear, this.chineseMonth);
        int nextMonth = nextChineseMonth(this.chineseYear, this.chineseMonth);
        while (this.chineseDate > lastDate) {
            if (Math.abs(nextMonth) < Math.abs(this.chineseMonth)) {
                this.chineseYear++;
            }
            this.chineseMonth = nextMonth;
            this.chineseDate -= lastDate;
            lastDate = daysInChineseMonth(this.chineseYear, this.chineseMonth);
            nextMonth = nextChineseMonth(this.chineseYear, this.chineseMonth);
        }
        return 0;
    }

    public int computeSolarTerms() {
        if (this.gregorianYear < 1901 || this.gregorianYear > 2100) {
            return 1;
        }
        this.sectionalTerm = sectionalTerm(this.gregorianYear, this.gregorianMonth);
        this.principleTerm = principleTerm(this.gregorianYear, this.gregorianMonth);
        return 0;
    }

    public static int sectionalTerm(int y, int m) {
        if (y < 1901 || y > 2100) {
            return 0;
        }
        int index = 0;
        char ry = (y - baseYear) + 1;
        while (ry >= sectionalTermYear[m - 1][index]) {
            index++;
        }
        int term = sectionalTermMap[m - 1][(index * 4) + (ry % 4)];
        if (ry == 'y' && m == 4) {
            term = 5;
        }
        if (ry == '' && m == 4) {
            term = 5;
        }
        if (ry == 'Â' && m == 6) {
            return 6;
        }
        return term;
    }

    public static int principleTerm(int y, int m) {
        if (y < 1901 || y > 2100) {
            return 0;
        }
        int index = 0;
        char ry = (y - baseYear) + 1;
        while (ry >= principleTermYear[m - 1][index]) {
            index++;
        }
        int term = principleTermMap[m - 1][(index * 4) + (ry % 4)];
        if (ry == '«' && m == 3) {
            term = 21;
        }
        if (ry == 'µ' && m == 5) {
            return 21;
        }
        return term;
    }

    public static boolean isGregorianLeapYear(int year) {
        boolean isLeap = false;
        if (year % 4 == 0) {
            isLeap = true;
        }
        if (year % 100 == 0) {
            isLeap = false;
        }
        if (year % HTTPStatus.BAD_REQUEST == 0) {
            return true;
        }
        return isLeap;
    }

    public static int daysInGregorianMonth(int y, int m) {
        int d = daysInGregorianMonth[m - 1];
        if (m == 2 && isGregorianLeapYear(y)) {
            return d + 1;
        }
        return d;
    }

    public static int daysInChineseMonth(int y, int m) {
        int index = (y - baseChineseYear) + baseIndex;
        if (1 > m || m > 8) {
            if (9 > m || m > 12) {
                if (((chineseMonths[(index * 2) + 1] >> 4) & 15) != Math.abs(m)) {
                    return 0;
                }
                for (int i : bigLeapMonthYears) {
                    if (i == index) {
                        return 30;
                    }
                }
                return 29;
            } else if (((chineseMonths[(index * 2) + 1] >> (m - 9)) & 1) == 1) {
                return 29;
            } else {
                return 30;
            }
        } else if (((chineseMonths[index * 2] >> (m - 1)) & 1) == 1) {
            return 29;
        } else {
            return 30;
        }
    }

    public static int nextChineseMonth(int y, int m) {
        int n = Math.abs(m) + 1;
        if (m > 0) {
            if (((chineseMonths[(((y - baseChineseYear) + baseIndex) * 2) + 1] >> 4) & 15) == m) {
                n = -m;
            }
        }
        if (n == 13) {
            return 1;
        }
        return n;
    }

    public String getSolartermsMsg() {
        String str = "";
        String gm = String.valueOf(this.gregorianMonth);
        if (gm.length() == 1) {
            gm = new StringBuilder(String.valueOf(' ')).append(gm).toString();
        }
        String cm = String.valueOf(Math.abs(this.chineseMonth));
        if (cm.length() == 1) {
            cm = new StringBuilder(String.valueOf(' ')).append(cm).toString();
        }
        String gd = String.valueOf(this.gregorianDate);
        if (gd.length() == 1) {
            gd = new StringBuilder(String.valueOf(' ')).append(gd).toString();
        }
        String cd = String.valueOf(this.chineseDate);
        if (cd.length() == 1) {
            cd = new StringBuilder(String.valueOf(' ')).append(cd).toString();
        }
        if (this.gregorianDate == this.sectionalTerm) {
            return " " + sectionalTermNames[this.gregorianMonth - 1];
        }
        if (this.gregorianDate == this.principleTerm) {
            return " " + principleTermNames[this.gregorianMonth - 1];
        }
        return str;
    }
}
