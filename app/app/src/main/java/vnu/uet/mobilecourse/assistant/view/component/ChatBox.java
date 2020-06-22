package vnu.uet.mobilecourse.assistant.view.component;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

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
        if(getLineCount() > 1 && refactor < 2)
        {
            refactor++;
            float longestLineWidth = -1;
            for (int lineIndex = 0; lineIndex < getLineCount(); lineIndex++)
            {
                Log.d("CHATBOX", "lineIndex: " + lineIndex);
                int lineStartIndex = getLayout().getLineStart(lineIndex);
                int lineEndIndex = getLayout().getLineEnd(lineIndex);
                String currentTextLine = getText().toString().substring(lineStartIndex, lineEndIndex);
                Log.d("CHATBOX", "currentTextLine: " + currentTextLine);
                // Added "_____" for your paddings.
                float currentLineWidth = getPaint().measureText(currentTextLine)
                        + DimensionUtils.dpToPx(16, getContext());
                Log.d("CHATBOX", "currentLineWidth: " + currentLineWidth);

                if (longestLineWidth < currentLineWidth)
                {
                    longestLineWidth = currentLineWidth;
                }
            }

            if (longestLineWidth > maxWidth) {
                longestLineWidth = maxWidth;
            }

            ViewGroup.LayoutParams paramsNew = getLayoutParams();
            paramsNew.width = (int) longestLineWidth;
            setLayoutParams(paramsNew);
        }
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
    }
}
