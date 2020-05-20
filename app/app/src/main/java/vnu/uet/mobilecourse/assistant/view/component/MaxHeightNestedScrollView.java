package vnu.uet.mobilecourse.assistant.view.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import vnu.uet.mobilecourse.assistant.R;

public class MaxHeightNestedScrollView extends NestedScrollView {

    private int mMaxHeight = -1;

    public MaxHeightNestedScrollView(@NonNull Context context) {
        super(context);
    }

    public MaxHeightNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeMaxHeight(context, attrs);
    }

    public MaxHeightNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeMaxHeight(context, attrs);
    }

    public int getMaxHeight() {
        return mMaxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.mMaxHeight = maxHeight;
    }

    private void initializeMaxHeight(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightNestedScrollView, 0, 0);
        try {
            float maxHeightDps = ta.getDimension(R.styleable.MaxHeightNestedScrollView_maxHeight, -1);
            setMaxHeight((int) maxHeightDps);
        } finally {
            ta.recycle();
        }
    }

    public void setMaxHeightDensity(float dps){
        this.mMaxHeight = (int) (dps * getContext().getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMaxHeight > 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}