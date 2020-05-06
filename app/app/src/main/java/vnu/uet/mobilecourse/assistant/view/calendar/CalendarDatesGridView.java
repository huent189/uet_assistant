package vnu.uet.mobilecourse.assistant.view.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.GridView;

import androidx.core.view.GestureDetectorCompat;

public class CalendarDatesGridView extends GridView {

    private GestureDetectorCompat detector;

    public CalendarDatesGridView(Context context) {
        super(context);
    }

    public CalendarDatesGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CalendarDatesGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnSwipeListener(OnSwipeListener listener) {
        detector = new GestureDetectorCompat(getContext(), listener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (detector != null)
            detector.onTouchEvent(ev);

        return super.onTouchEvent(ev);
    }

    public static abstract class OnSwipeListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 20;
        private static final int SWIPE_VELOCITY_THRESHOLD = 30;

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            float diffY = event2.getY() - event1.getY();
            float diffX = event2.getX() - event1.getX();

            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD/* && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD*/) {
                    if (diffX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                }
            }
//            else {
//                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
//                    if (diffY > 0) {
//                        onSwipeBottom();
//                    } else {
//                        onSwipeTop();
//                    }
//                }
//            }
            return true;
        }

        public abstract void onSwipeLeft();

        public abstract void onSwipeRight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec;

        if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
            // The two leftmost bits in the height measure spec have
            // a special meaning, hence we can't use them to describe height.
            heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        } else {
            // Any other height should be respected as is.
            heightSpec = heightMeasureSpec;
        }

        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}
