package vnu.uet.mobilecourse.assistant.view.component;

import android.text.TextPaint;
import android.text.style.ClickableSpan;

public abstract class MyClickableSpan extends ClickableSpan {

    @Override
    public void updateDrawState(TextPaint ds) {
        // set to false to remove underline
        ds.setUnderlineText(false);
    }
}