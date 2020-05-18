package vnu.uet.mobilecourse.assistant.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {
    public static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("'Ngày' dd/MM/yyyy", Locale.ROOT);

    public static final SimpleDateFormat SHORT_DATE_FORMAT =
            new SimpleDateFormat("dd/MM/yy", Locale.ROOT);

    public static final SimpleDateFormat MONTH_FORMAT =
            new SimpleDateFormat("'Tháng' MM, yyyy", Locale.ROOT);

    public static final SimpleDateFormat DATE_TIME_FORMAT =
            new SimpleDateFormat("dd/MM/yy hh:mm", Locale.ROOT);

    public static Date fromSecond(long second) {
        return new Date(second * 1000);
    }

    public static boolean isSameMonthAndYear(Calendar current, Calendar target) {
        int targetMonth = target.get(Calendar.MONTH) + 1;
        int currentMonth = current.get(Calendar.MONTH) + 1;
        int targetYear = target.get(Calendar.YEAR);
        int currentYear = current.get(Calendar.YEAR);

        return targetMonth == currentMonth && targetYear == currentYear;
    }

    public static boolean isSameDate(Date d1, Date d2) {
        return DATE_FORMAT.format(d1).equals(DATE_FORMAT.format(d2));
    }

    public static boolean isSameMonthAndYear(Date d1, Date d2) {
        return MONTH_FORMAT.format(d1).equals(MONTH_FORMAT.format(d2));
    }
}
