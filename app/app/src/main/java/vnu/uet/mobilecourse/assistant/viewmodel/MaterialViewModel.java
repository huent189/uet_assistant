package vnu.uet.mobilecourse.assistant.viewmodel;

import android.text.SpannableStringBuilder;
import android.text.Spanned;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.material.MaterialContent;
import vnu.uet.mobilecourse.assistant.repository.course.MaterialRepository;
import vnu.uet.mobilecourse.assistant.util.StringUtils;

public class MaterialViewModel extends ViewModel {

    private MaterialRepository materialRepo = MaterialRepository.getInstance();

    /**
     * Convert html raw content to text view
     *
     * @param html raw content
     * @return spannable string for text view
     */
    public SpannableStringBuilder convertHtml(@NonNull String html) {
        Spanned content = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY);

        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        builder = StringUtils.removeExtraLineBreak(builder);

        return builder;
    }

    public LiveData<? extends MaterialContent> getDetailContent(int id, String type) {
        return materialRepo.getDetails(id, type);
    }
}
