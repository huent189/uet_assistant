package vnu.uet.mobilecourse.assistant.util;

import android.app.AlarmManager;

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
            new SimpleDateFormat("dd/MM/yy hh:mm a", Locale.ROOT);

    public static final SimpleDateFormat TIME_12H_FORMAT =
            new SimpleDateFormat("hh:mm a", Locale.ROOT);

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

    public static String generateViewText(long seconds) {
        Date date = DateTimeUtils.fromSecond(seconds);
        String time;

        long diff = Math.abs(System.currentTimeMillis() - date.getTime());
//        // under 1 minute
//        if (diff < 60 * 1000) {
//            time = String.format(Locale.ROOT, "%d giây trước", diff / 1000);
//        }
//        // under 1 hour
//        else if (diff < 60 * 60 * 1000) {
//            time = String.format(Locale.ROOT, "%d phút trước", diff / 1000 / 60);
//        }
//        // under 1 day
//        else if (diff < 24 * 60 * 60 * 1000) {
//            time = String.format(Locale.ROOT, "%d giờ trước", diff / 1000 / 60 / 60);
//        }
        if (diff < AlarmManager.INTERVAL_DAY) {
            time = TIME_12H_FORMAT.format(date);
        } else if (diff < 2 * AlarmManager.INTERVAL_DAY) {
            time = "Hôm qua, " + TIME_12H_FORMAT.format(date);
        } else {
            time = DateTimeUtils.DATE_TIME_FORMAT.format(date);
        }

        return time;
    }
}
