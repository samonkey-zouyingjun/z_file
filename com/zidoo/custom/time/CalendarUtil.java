package com.zidoo.custom.time;

import android.support.v4.media.session.PlaybackStateCompat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

class CalendarUtil {
    private static final String[] CHINESE_NUMBER = new String[]{"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "腊"};
    private static final long[] LUNAR_INFO = new long[]{19416, 19168, 42352, 21717, 53856, 55632, 91476, 22176, 39632, 21970, 19168, 42422, 42192, 53840, 119381, 46400, 54944, 44450, 38320, 84343, 18800, 42160, 46261, 27216, 27968, 109396, 11104, 38256, 21234, 18800, 25958, 54432, 59984, 28309, 23248, 11104, 100067, 37600, 116951, 51536, 54432, 120998, 46416, 22176, 107956, 9680, 37584, 53938, 43344, 46423, 27808, 46416, 86869, 19872, 42448, 83315, 21200, 43432, 59728, 27296, 44710, 43856, 19296, 43748, 42352, 21088, 62051, 55632, 23383, 22176, 38608, 19925, 19152, 42192, 54484, 53840, 54616, 46400, 46496, 103846, 38320, 18864, 43380, 42160, 45690, 27216, 27968, 44870, 43872, 38256, 19189, 18800, 25776, 29859, 59984, 27480, 21952, 43872, 38613, 37600, 51552, 55636, 54432, 55888, 30034, 22176, 43959, 9680, 37584, 51893, 43344, 46240, 47780, 44368, 21977, 19360, 42416, 86390, 21168, 43312, 31060, 27296, 44368, 23378, 19296, 42726, 42208, 53856, 60005, 54576, 23200, 30371, 38608, 19415, 19152, 42192, 118966, 53840, 54560, 56645, 46496, 22224, 21938, 18864, 42359, 42160, 43600, 111189, 27936, 44448};
    private static final String[] WEEK_NUMBER = new String[]{"日", "一", "二", "三", "四", "五", "六"};
    private static SimpleDateFormat chineseDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private boolean isLoap;
    private Calendar mCurrenCalendar;
    private int mLuchDay;
    private int mLuchMonth;
    private int mLuchYear;

    private static int yearDays(int year) {
        int sum = 348;
        for (int i = 32768; i > 8; i >>= 1) {
            if ((LUNAR_INFO[year - 1900] & ((long) i)) != 0) {
                sum++;
            }
        }
        return leapDays(year) + sum;
    }

    private static int leapDays(int year) {
        if (leapMonth(year) == 0) {
            return 0;
        }
        if ((LUNAR_INFO[year - 1900] & PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH) != 0) {
            return 30;
        }
        return 29;
    }

    private static int leapMonth(int year) {
        int mount = year - 1900;
        if (mount >= LUNAR_INFO.length) {
            mount = LUNAR_INFO.length - 1;
        }
        return (int) (LUNAR_INFO[mount] & 15);
    }

    private static int monthDays(int year, int month) {
        if ((LUNAR_INFO[year - 1900] & ((long) (65536 >> month))) == 0) {
            return 29;
        }
        return 30;
    }

    public String animalsYear() {
        return new String[]{"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"}[(this.mLuchYear - 4) % 12];
    }

    private static String cyclicalm(int num) {
        return new StringBuilder(String.valueOf(new String[]{"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"}[num % 10])).append(new String[]{"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"}[num % 12]).toString();
    }

    public String cyclical() {
        return cyclicalm((this.mLuchYear - 1900) + 36);
    }

    public CalendarUtil(Calendar cal) {
        this.mCurrenCalendar = cal;
        Date baseDate = null;
        try {
            baseDate = chineseDateFormat.parse("1900年1月31日");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int offset = (int) ((cal.getTime().getTime() - baseDate.getTime()) / 86400000);
        int dayCyl = offset + 40;
        int monCyl = 14;
        int daysOfYear = 0;
        int iYear = 1900;
        while (iYear < 2050 && offset > 0) {
            daysOfYear = yearDays(iYear);
            offset -= daysOfYear;
            monCyl += 12;
            iYear++;
        }
        if (offset < 0) {
            offset += daysOfYear;
            iYear--;
            monCyl -= 12;
        }
        this.mLuchYear = iYear;
        int yearCyl = iYear - 1864;
        int leapMonth = leapMonth(iYear);
        this.isLoap = false;
        int daysOfMonth = 0;
        int iMonth = 1;
        while (iMonth < 13 && offset > 0) {
            if (leapMonth <= 0 || iMonth != leapMonth + 1 || this.isLoap) {
                daysOfMonth = monthDays(this.mLuchYear, iMonth);
            } else {
                iMonth--;
                this.isLoap = true;
                daysOfMonth = leapDays(this.mLuchYear);
            }
            offset -= daysOfMonth;
            if (this.isLoap && iMonth == leapMonth + 1) {
                this.isLoap = false;
            }
            if (!this.isLoap) {
                monCyl++;
            }
            iMonth++;
        }
        if (offset == 0 && leapMonth > 0 && iMonth == leapMonth + 1) {
            if (this.isLoap) {
                this.isLoap = false;
            } else {
                this.isLoap = true;
                iMonth--;
                monCyl--;
            }
        }
        if (offset < 0) {
            offset += daysOfMonth;
            iMonth--;
            monCyl--;
        }
        this.mLuchMonth = iMonth;
        this.mLuchDay = offset + 1;
    }

    public static String getChinaDayString(int day) {
        String[] chineseTen = new String[]{"初", "十", "廿", "卅"};
        int n = day % 10 == 0 ? 9 : (day % 10) - 1;
        if (day > 30) {
            return "";
        }
        if (day == 10) {
            return "初十";
        }
        return chineseTen[day / 10] + CHINESE_NUMBER[n];
    }

    public String toString() {
        String message = "";
        if (this.mLuchDay % 10 != 0) {
            int i = (this.mLuchDay % 10) - 1;
        }
        message = getChinaCalendarMsg(this.mLuchYear, this.mLuchMonth, this.mLuchDay);
        if (!isNullOrEmpty(message)) {
            return message;
        }
        String solarMsg = new SolarTermsUtil(this.mCurrenCalendar).getSolartermsMsg();
        if (!isNullOrEmpty(solarMsg)) {
            return solarMsg;
        }
        String gremessage = new GregorianUtil(this.mCurrenCalendar).getGremessage();
        if (!isNullOrEmpty(gremessage)) {
            return gremessage;
        }
        if (this.mLuchDay == 1) {
            return new StringBuilder(String.valueOf(CHINESE_NUMBER[this.mLuchMonth - 1])).append("月").append("初一").toString();
        }
        return new StringBuilder(String.valueOf(CHINESE_NUMBER[this.mLuchMonth - 1])).append("月").append(getChinaDayString(this.mLuchDay)).toString();
    }

    public int getLuchMonth() {
        return this.mLuchMonth;
    }

    public int getLuchDay() {
        return this.mLuchDay;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    public String getDay() {
        return new StringBuilder(String.valueOf(this.isLoap ? "闰" : "")).append(CHINESE_NUMBER[this.mLuchMonth - 1]).append("月").append(getChinaDayString(this.mLuchDay)).toString();
    }

    public static String getDay(Calendar calendar) {
        return simpleDateFormat.format(calendar.getTime());
    }

    public static boolean compare(Date compareDate, Date currentDate) {
        return chineseDateFormat.format(compareDate).compareTo(chineseDateFormat.format(currentDate)) >= 0;
    }

    public static String getWeek(Calendar calendar) {
        return "周" + WEEK_NUMBER[calendar.get(7) - 1];
    }

    public static String getCurrentDay(Calendar calendar) {
        return getDay(calendar) + " 农历" + new CalendarUtil(calendar).getDay() + " " + getWeek(calendar);
    }

    private String getChinaCalendarMsg(int year, int month, int day) {
        String message = "";
        if (month == 1 && day == 1) {
            return "春节";
        }
        if (month == 1 && day == 15) {
            return "元宵";
        }
        if (month == 5 && day == 5) {
            return "端午";
        }
        if (month == 7 && day == 7) {
            return "七夕";
        }
        if (month == 8 && day == 15) {
            return "中秋";
        }
        if (month == 9 && day == 9) {
            return "重阳";
        }
        if (month == 12 && day == 8) {
            return "腊八";
        }
        if (month != 12) {
            return message;
        }
        if ((monthDays(year, month) == 29 && day == 29) || (monthDays(year, month) == 30 && day == 30)) {
            return "除夕";
        }
        return message;
    }
}
