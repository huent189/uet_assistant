package vnu.uet.mobilecourse.assistant.util;

import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static String courseTitleFormat(String old){
        String formatted = Html.fromHtml(old, Html.FROM_HTML_MODE_COMPACT).toString();
        String regex = "\\(.*?\\)";
        return formatted.replaceAll(regex, "");
    }
    public static boolean compareHTML(String s1, String s2){
        if(s1.equals(s2)){
            return true;
        }
        String nonHtml1 = Html.fromHtml(s1, Html.FROM_HTML_MODE_COMPACT).toString();
        String nonHtml2 = Html.fromHtml(s2, Html.FROM_HTML_MODE_COMPACT).toString();
        nonHtml1 = nonHtml1.replaceAll("\\s","");
        nonHtml2 = nonHtml2.replaceAll("\\s","");
        return nonHtml1.equals(nonHtml2);
    }
    public static String mergePageContent(String intro, String content) {
        if(content.equals("<p>Như mô tả</p>")|| compareHTML(intro, content)){
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
