package vnu.uet.mobilecourse.assistant.view.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import vnu.uet.mobilecourse.assistant.R;

public class EmptyView extends LinearLayout {

    private TextView mTvContent;

    public EmptyView(Context context) {
        super(context);
        initializeLayout(context);
    }

    public EmptyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeLayout(context);
        initialize(context, attrs);
    }

    public EmptyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeLayout(context);
        initialize(context, attrs);
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initializeLayout(context);
        initialize(context, attrs);
    }

    private void initializeLayout(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            View view = inflater.inflate(R.layout.layout_empty, this);
            mTvContent = view.findViewById(R.id.tvContent);
        }
    }

    private void initialize(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EmptyView, 0, 0);
        try {
            String content = ta.getString(R.styleable.EmptyView_textContent);
            mTvContent.setText(content);
        } finally {
            ta.recycle();
        }
    }
}
