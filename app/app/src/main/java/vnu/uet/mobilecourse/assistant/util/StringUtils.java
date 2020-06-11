package vnu.uet.mobilecourse.assistant.util;

import android.text.Html;
import android.text.SpannableStringBuilder;

public class StringUtils {

    public static String courseTitleFormat(String old){
        String formatted = Html.fromHtml(old, Html.FROM_HTML_MODE_COMPACT).toString();
        String regex = "\\(.*?\\)";
        return formatted.replaceAll(regex, "");
    }

    public static String mergePageContent(String intro, String content) {
        if(intro.equals(content) || content.equals("<p>Như mô tả</p>")){
            return intro;
        }
        return intro + content;
    }

    public static SpannableStringBuilder removeExtraLineBreak(SpannableStringBuilder builder) {
        while (builder.length() > 0 && builder.charAt(builder.length() - 1) == CONST.LINE_BREAK_CHAR) {
            CharSequence subSequence = builder.subSequence(0, builder.length() - 1);

            if (subSequence instanceof SpannableStringBuilder) {
                builder = (SpannableStringBuilder) subSequence;
            }
        }

        return builder;
    }
}
