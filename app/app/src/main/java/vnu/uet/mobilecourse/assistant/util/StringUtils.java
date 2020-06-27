package vnu.uet.mobilecourse.assistant.util;

import android.graphics.Paint;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;

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

    private static SpannableStringBuilder removeExtraLineBreak(SpannableStringBuilder builder) {
        while (builder.length() > 0 && builder.charAt(builder.length() - 1) == StringConst.LINE_BREAK_CHAR) {
            CharSequence subSequence = builder.subSequence(0, builder.length() - 1);

            if (subSequence instanceof SpannableStringBuilder) {
                builder = (SpannableStringBuilder) subSequence;
            }
        }

        return builder;
    }

    /**
     * Convert html raw content to text view
     *
     * @param html raw content
     * @return spannable string for text view
     */
    public static SpannableStringBuilder convertHtml(@NonNull String html) {
        Spanned content = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY);

        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        builder = StringUtils.removeExtraLineBreak(builder);

        return builder;
    }

    public static String getLastSegment(String origin, int number) {
        StringBuilder result = new StringBuilder();

        String[] items = origin.split("\\s+");
        int length = items.length;

        if (length > number) {
            for (int i = length - number; i < length; i++) {
                result.append(items[i]);
                if (i != length - 1) {
                    result.append(StringConst.SPACE_CHAR);
                }
            }
        } else {
            result.append(origin);
        }

        return result.toString();
    }

    public static String getLastSegment(String origin) {
        return getLastSegment(origin, 1);
    }

    public static List<String> getAllMatches(String content, String regex) {
        List<String> allMatches = new ArrayList<>();

        Matcher m = Pattern.compile(regex).matcher(content);

        while (m.find()) {
            allMatches.add(m.group());
        }

        return allMatches;
    }
}
