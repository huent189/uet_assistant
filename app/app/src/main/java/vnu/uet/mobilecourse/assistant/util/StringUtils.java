package vnu.uet.mobilecourse.assistant.util;

import android.graphics.Paint;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

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

    public static String splitTextToFitWidth(String[] _texts, Paint _paint, int maxWidth) {

        String formattedText = "";

        String workingText = "";
        for (String section : _texts)
        {
            String newPart = (workingText.length() > 0 ? " " : "") + section;
            workingText += newPart;

            int width = (int)_paint.measureText(workingText, 0, workingText.length());

            if (width > maxWidth)
            {
                formattedText += (formattedText.length() > 0 ? "\n" : "") + workingText.substring(0, workingText.length() - newPart.length());
                workingText = section;
            }
        }

        if (workingText.length() > 0)
            formattedText += (formattedText.length() > 0 ? "\n" : "") + workingText;

        return formattedText;
    }
}
