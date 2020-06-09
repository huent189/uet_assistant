package vnu.uet.mobilecourse.assistant.adapter.viewholder;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.event.IEvent;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.model.firebase.TodoList;
import vnu.uet.mobilecourse.assistant.repository.firebase.TodoRepository;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public abstract class EventViewHolder extends ChildViewHolder {

    protected ImageView mIvAlarm;
    protected TextView mTvDeadline;
    protected TextView mTvTodoTitle;
    protected TextView mTvCategory;
    protected CheckBox mCbDone;
    protected StrikethroughSpan mStrikeSpan = new StrikethroughSpan();
    protected SpannableString mTitleText;
    protected TextView mLayoutDisable;

    protected EventViewHolder(@NonNull View itemView) {
        super(itemView);

        mTvTodoTitle = itemView.findViewById(R.id.tvTodoTitle);
        mIvAlarm = itemView.findViewById(R.id.ivAlarm);
        mTvDeadline = itemView.findViewById(R.id.tvDeadline);
        mCbDone = itemView.findViewById(R.id.cbDone);
        mTvCategory = itemView.findViewById(R.id.tvCategory);
        mLayoutDisable = itemView.findViewById(R.id.layout_disable);
    }

    public void bind(IEvent event) {
        // setup title text
        String title = event.getTitle();
        mTitleText = new SpannableString(title);
        mTvTodoTitle.setText(mTitleText);

        // setup deadline text
        Date deadline = event.getTime();
        SimpleDateFormat dateFormat = DateTimeUtils.TIME_12H_FORMAT;
        mTvDeadline.setText(dateFormat.format(deadline));

        mTvCategory.setText(event.getCategory());

        if (event.isCompleted()) {
            updateDoneEffect();
        } else {
            updateDoingEffect(deadline.getTime());
        }
    }

    protected void updateDoneEffect() {
        mTitleText.setSpan(mStrikeSpan, 0, mTitleText.length() - 1, 0);
        mTvTodoTitle.setText(mTitleText);

        mLayoutDisable.setVisibility(View.VISIBLE);

        mTvDeadline.setTextColor(Color.WHITE);
        mIvAlarm.setColorFilter(Color.WHITE);

        mCbDone.setChecked(true);
    }

    protected void updateDoingEffect(long deadline) {
        mTitleText.removeSpan(mStrikeSpan);
        mTvTodoTitle.setText(mTitleText);

        mLayoutDisable.setVisibility(View.GONE);

        mCbDone.setChecked(false);

        if (deadline - System.currentTimeMillis() < WARNING_BOUNDARY) {
            mTvDeadline.setTextColor(RED_COLOR);
            mIvAlarm.setColorFilter(RED_COLOR);
        }
    }

    protected static final int WARNING_BOUNDARY = 60 * 60 * 1000; // 1 hour

    protected static final int RED_COLOR = Color.parseColor("#FFF44336");
}