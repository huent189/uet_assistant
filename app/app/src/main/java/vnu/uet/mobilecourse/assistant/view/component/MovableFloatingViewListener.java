package vnu.uet.mobilecourse.assistant.view.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MovableFloatingViewListener implements View.OnTouchListener {

    // Often, there will be a slight, unintentional,
    // drag when the user taps the FAB,
    // so we need to account for this.
    private final static float CLICK_DRAG_TOLERANCE = 10;

    private float downRawX, downRawY;
    private float dX, dY;

    private View mView;

    public MovableFloatingViewListener(View view) {
        mView = view;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent){

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams)view.getLayoutParams();

        int action = motionEvent.getAction();
        if (action == MotionEvent.ACTION_DOWN) {

            downRawX = motionEvent.getRawX();
            downRawY = motionEvent.getRawY();
            dX = view.getX() - downRawX;
            dY = view.getY() - downRawY;

            return true; // Consumed

        }
        else if (action == MotionEvent.ACTION_MOVE) {

            int viewWidth = view.getWidth();
            int viewHeight = view.getHeight();

            View viewParent = (View)view.getParent();
            int parentWidth = viewParent.getWidth();
            int parentHeight = viewParent.getHeight();

            float newX = motionEvent.getRawX() + dX;
            // Don't allow the FAB past the left hand side of the parent
            newX = Math.max(layoutParams.leftMargin, newX);
            // Don't allow the FAB past the right hand side of the parent
            newX = Math.min(parentWidth - viewWidth - layoutParams.rightMargin, newX);

            float newY = motionEvent.getRawY() + dY;
            // Don't allow the FAB past the top of the parent
            newY = Math.max(layoutParams.topMargin, newY);
            // Don't allow the FAB past the bottom of the parent
            newY = Math.min(parentHeight - viewHeight - layoutParams.bottomMargin, newY);

            view.animate()
                    .x(newX)
                    .y(newY)
                    .setDuration(0)
                    .start();

            // Consumed
            return true;

        }
        else if (action == MotionEvent.ACTION_UP) {

            float upRawX = motionEvent.getRawX();
            float upRawY = motionEvent.getRawY();

            float upDX = upRawX - downRawX;
            float upDY = upRawY - downRawY;

            // A click
            if (Math.abs(upDX) < CLICK_DRAG_TOLERANCE && Math.abs(upDY) < CLICK_DRAG_TOLERANCE) {
                return mView.performClick();
            }
            // A drag
            else {
                // Consumed
                return true;
            }

        }
        else {
            return mView.onTouchEvent(motionEvent);
        }

    }

}