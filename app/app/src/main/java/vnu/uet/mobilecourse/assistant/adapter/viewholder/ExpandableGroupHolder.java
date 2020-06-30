package vnu.uet.mobilecourse.assistant.adapter.viewholder;

import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;
import static android.view.animation.Animation.RELATIVE_TO_SELF;
import vnu.uet.mobilecourse.assistant.R;

public abstract class ExpandableGroupHolder extends GroupViewHolder {

    private ImageView mIvExpandArrow;

    public ExpandableGroupHolder(View itemView) {
        super(itemView);

        mIvExpandArrow = itemView.findViewById(R.id.ivExpandArrow);
    }

    protected void bindArrow(boolean expand) {
        if (expand) mIvExpandArrow.setRotation(180);
        else mIvExpandArrow.setRotation(0);
    }

    @Override
    public void expand() {
        animateExpand();
    }

    @Override
    public void collapse() {
        animateCollapse();
    }

    private void animateExpand() {
        RotateAnimation rotate = new RotateAnimation(DOWN_ANGLE, UP_ANGLE,
                RELATIVE_TO_SELF, PIVOT, RELATIVE_TO_SELF, PIVOT);
        rotate.setDuration(DURATION);
        rotate.setFillAfter(true);
        mIvExpandArrow.setAnimation(rotate);
    }

    private void animateCollapse() {
        RotateAnimation rotate = new RotateAnimation(UP_ANGLE, DOWN_ANGLE,
                RELATIVE_TO_SELF, PIVOT, RELATIVE_TO_SELF, PIVOT);
        rotate.setDuration(DURATION);
        rotate.setFillAfter(true);
        mIvExpandArrow.setAnimation(rotate);
    }

    private static final int DOWN_ANGLE = 360;
    private static final int UP_ANGLE = 180;
    private static final float PIVOT = 0.5f;
    private static final int DURATION = 300;
}
