package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.TodoViewHolder;
import vnu.uet.mobilecourse.assistant.model.event.CourseSessionEvent;
import vnu.uet.mobilecourse.assistant.model.event.IEvent;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.model.event.DailyEventList;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.view.calendar.CalendarFragment;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public class DailyEventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private DailyEventList mDailyList;
    private CalendarFragment mOwner;
    private NavController mNavController;

    public DailyEventAdapter(DailyEventList todoList, CalendarFragment owner) {
        this.mDailyList = todoList;
        this.mOwner = owner;
    }

    @Override
    public int getItemViewType(int position) {
        IEvent event = mDailyList.get(position);

        if (event instanceof Todo)
            return TYPE_TODO;
        else if (event instanceof CourseSessionEvent)
            return TYPE_COURSE_SESSION;

        return -1;
    }

    private static final int TYPE_TODO = 0;
    private static final int TYPE_COURSE_SESSION = 1;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mOwner.getLayoutInflater()
                .inflate(R.layout.layout_todo_item, parent, false);

        RecyclerView.ViewHolder holder = null;

        switch (viewType) {
            case TYPE_TODO:
                holder = new TodoViewHolder(view) {
                    @Override
                    protected IStateLiveData<String> onMarkAsDone(Todo todo) {
                        mOwner.saveRecycleViewState();
                        return mOwner.getViewModel().markTodoAsDone(todo.getId());
                    }

                    @Override
                    protected IStateLiveData<String> onMarkAsDoing(Todo todo) {
                        mOwner.saveRecycleViewState();
                        return mOwner.getViewModel().markTodoAsDoing(todo.getId());
                    }
                };

                break;

            default:
                holder = new BasicEventHolder(view);
                break;
        }

        Activity activity = mOwner.getActivity();

        if (activity != null) {
            mNavController = Navigation
                    .findNavController(activity, R.id.nav_host_fragment);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final IEvent event = mDailyList.get(position);

        if (holder instanceof TodoViewHolder && event instanceof Todo) {
            ((TodoViewHolder) holder).bind((Todo) event, true, mOwner.getViewLifecycleOwner());
        } else if (holder instanceof BasicEventHolder) {
            ((BasicEventHolder) holder).bind(event);
        }
    }

    @Override
    public int getItemCount() {
        return mDailyList.size();
    }

    public DailyEventList getTodoList() {
        return mDailyList;
    }

    public static class BasicEventHolder extends RecyclerView.ViewHolder {

        private ImageView mIvAlarm;
        private TextView mTvDeadline;
        private TextView mTvTodoTitle;
        private TextView mTvCategory;
        private CheckBox mCbDone;
        private StrikethroughSpan mStrikeSpan = new StrikethroughSpan();
        private SpannableString mTitleText;
        private TextView mLayoutDisable;

        protected BasicEventHolder(@NonNull View itemView) {
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
            mTvDeadline.setText(DateTimeUtils.TIME_12H_FORMAT.format(deadline));

            // setup category text
            mTvCategory.setText(event.getCategory());

            if (event.isCompleted()) {
                updateDoneEffect();
            } else {
                updateDoingEffect(deadline.getTime());
            }
        }

        private void updateDoneEffect() {
            mTitleText.setSpan(mStrikeSpan, 0, mTitleText.length() - 1, 0);
            mTvTodoTitle.setText(mTitleText);

            mLayoutDisable.setVisibility(View.VISIBLE);

            mTvDeadline.setTextColor(Color.WHITE);
            mIvAlarm.setColorFilter(Color.WHITE);

            mCbDone.setChecked(true);
        }

        private void updateDoingEffect(long deadline) {
            mTitleText.removeSpan(mStrikeSpan);
            mTvTodoTitle.setText(mTitleText);

            mLayoutDisable.setVisibility(View.GONE);

            mCbDone.setChecked(false);

            if (deadline - System.currentTimeMillis() < WARNING_BOUNDARY) {
                mTvDeadline.setTextColor(RED_COLOR);
                mIvAlarm.setColorFilter(RED_COLOR);
            }
        }

        private static final int WARNING_BOUNDARY = 60 * 60 * 1000; // 1 hour

        private static final int RED_COLOR = Color.parseColor("#FFF44336");
    }
}
