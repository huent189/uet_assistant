package vnu.uet.mobilecourse.assistant.viewmodel;

import android.text.SpannableStringBuilder;
import android.text.Spanned;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.ViewModel;

public class MaterialViewModel extends ViewModel {

    public SpannableStringBuilder convertHtml(@NonNull String html) {
        Spanned content = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY);
        return new SpannableStringBuilder(content);
    }
}
