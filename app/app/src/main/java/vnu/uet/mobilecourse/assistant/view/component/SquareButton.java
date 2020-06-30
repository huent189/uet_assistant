package vnu.uet.mobilecourse.assistant.view.component;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.util.DimensionUtils;

public class SquareButton extends AppCompatButton {

    public SquareButton(Context context) {
        super(context, null, R.attr.squareButtonStyle);
        initialize();
    }

    public SquareButton(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.squareButtonStyle);
        initialize();
    }

    public SquareButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, R.attr.squareButtonStyle);
        initialize();
    }

    private void initialize() {
        setupDrawable();
    }

    private void setupDrawable() {
        final int SIZE = DimensionUtils.dpToPx(24, getContext());

        Drawable[] drawables = getCompoundDrawablesRelative();
        for (Drawable drawable : drawables) {
            if (drawable != null) {
                drawable.setBounds(0, 0, SIZE, SIZE);
            }
        }

        setCompoundDrawablesRelative(drawables[0], drawables[1], drawables[2], drawables[3]);
    }

}
