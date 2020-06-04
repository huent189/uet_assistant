package vnu.uet.mobilecourse.assistant.view.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.ISwipeToDeleteHolder;

public abstract class SwipeToDeleteCallback extends ItemTouchHelper.Callback {

    private Paint mClearPaint;
    private ColorDrawable mBackground;

    private static Drawable sDeleteIcon;
    private static int sBackgroundColor;

    private int mIntrinsicWidth;
    private int mIntrinsicHeight;

    protected SwipeToDeleteCallback(Context context) {
        sBackgroundColor = ContextCompat.getColor(context, R.color.red);
        sDeleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete_24dp);

        mBackground = new ColorDrawable();
        mClearPaint = new Paint();

        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        mIntrinsicWidth = sDeleteIcon.getIntrinsicWidth();
        mIntrinsicHeight = sDeleteIcon.getIntrinsicHeight();
    }


    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder) {

        int swipeFlag = ItemTouchHelper.ACTION_STATE_IDLE;

        if (viewHolder instanceof ISwipeToDeleteHolder) {
            swipeFlag = ItemTouchHelper.LEFT;
        }

        return makeMovementFlags(0, swipeFlag);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isActive) {

        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isActive);

        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getHeight();

        boolean isCancelled = dX == 0 && !isActive;

        if (isCancelled) {
            float left = itemView.getRight() + dX;
            float top = itemView.getTop();
            float right = itemView.getRight();
            float bottom = itemView.getBottom();

            clearCanvas(canvas, left, top, right, bottom);

            super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isActive);
            return;
        }

        mBackground.setColor(sBackgroundColor);

        int left = itemView.getRight() + (int) dX;
        mBackground.setBounds(left, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        mBackground.draw(canvas);

        int deleteIconTop = itemView.getTop() + (itemHeight - mIntrinsicHeight) / 2;
        int deleteIconMargin = (itemHeight - mIntrinsicHeight) / 2;
        int deleteIconLeft = itemView.getRight() - deleteIconMargin - mIntrinsicWidth;
        int deleteIconRight = itemView.getRight() - deleteIconMargin;
        int deleteIconBottom = deleteIconTop + mIntrinsicHeight;

        sDeleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
        sDeleteIcon.draw(canvas);

        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isActive);
    }

    private void clearCanvas(Canvas canvas, Float left, Float top, Float right, Float bottom) {
        canvas.drawRect(left, top, right, bottom, mClearPaint);
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.7f;
    }
}