package vnu.uet.mobilecourse.assistant.util;

import android.text.Html;

public class StringUtils {
    public static String courseTitleFormat(String old){
        String formatted = Html.fromHtml(old, Html.FROM_HTML_MODE_COMPACT).toString();
        String regex = "\\(.*?\\)";
        return formatted.replaceAll(regex, "");
    }
}
