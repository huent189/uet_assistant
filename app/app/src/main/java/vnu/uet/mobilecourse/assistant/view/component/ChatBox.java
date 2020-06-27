package vnu.uet.mobilecourse.assistant.view.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import vnu.uet.mobilecourse.assistant.util.DimensionUtils;

public class ChatBox extends androidx.appcompat.widget.AppCompatTextView {

    private int refactor = 0;

    public ChatBox(Context context) {
        super(context);
    }

    public ChatBox(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void reduce(int maxWidth) {
        if (getLineCount() > 1 && refactor < 1) {
            setVisibility(INVISIBLE);

            float longestLineWidth = -1;

            final int PADDING = DimensionUtils.dpToPx(16, getContext());

            for (int lineIndex = 0; lineIndex < getLineCount(); lineIndex++) {

                int lineStartIndex = getLayout().getLineStart(lineIndex);
                int lineEndIndex = getLayout().getLineEnd(lineIndex);

                String currentTextLine = getText().toString().substring(lineStartIndex, lineEndIndex);

                float currentLineWidth = getPaint().measureText(currentTextLine) + PADDING;

                if (longestLineWidth < currentLineWidth) longestLineWidth = currentLineWidth;
            }

            if (longestLineWidth > maxWidth) longestLineWidth = maxWidth;

            ViewGroup.LayoutParams paramsNew = getLayoutParams();
            paramsNew.width = (int) longestLineWidth;
            setLayoutParams(paramsNew);

            refactor++;
        }
    }
}
