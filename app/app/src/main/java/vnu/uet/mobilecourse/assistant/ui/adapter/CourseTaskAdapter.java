package vnu.uet.mobilecourse.assistant.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.ui.model.CourseTask;
import vnu.uet.mobilecourse.assistant.ui.model.WeeklyTasks;

public class CourseTaskAdapter extends
        ExpandableRecyclerViewAdapter<CourseTaskAdapter.WeeklyTaskViewHolder, CourseTaskAdapter.TaskViewHolder> {

    private Fragment owner;

    private List<WeeklyTasks> taskList;

    private LayoutInflater inflater;

    public CourseTaskAdapter(List<WeeklyTasks> taskList, Fragment owner) {
        super(taskList);
        this.owner = owner;
        this.inflater = owner.getLayoutInflater();
    }


    @Override
    public WeeklyTaskViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_weekly_tasks_item, parent, false);
        return new WeeklyTaskViewHolder(view);
    }

    @Override
    public TaskViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(TaskViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final CourseTask task = (CourseTask) group.getItems().get(childIndex);
        holder.bind(task);
    }

    @Override
    public void onBindGroupViewHolder(WeeklyTaskViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.bind((WeeklyTasks) group);
    }

    public class WeeklyTaskViewHolder extends GroupViewHolder {
        private TextView tvWeeklyTitle;

        private ImageView ivExpandArrow;

        public WeeklyTaskViewHolder(@NonNull View itemView) {
            super(itemView);

            tvWeeklyTitle = itemView.findViewById(R.id.tvWeeklyTitle);
            ivExpandArrow = itemView.findViewById(R.id.ivExpandArrow);
        }

        public void bind(WeeklyTasks weeklyTasks) {
            tvWeeklyTitle.setText(weeklyTasks.getTitle());
        }
    }


    public class TaskViewHolder extends ChildViewHolder {
        private TextView tvTaskTitle;

        private ImageView ivTaskStatus;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            ivTaskStatus = itemView.findViewById(R.id.ivTaskStatus);
        }

        public void bind(CourseTask task) {
            tvTaskTitle.setText(task.getTitle());

            if (task.isDone()) {
                ivTaskStatus.setImageResource(R.drawable.ic_check_circle_24dp);
            } else {
                ivTaskStatus.setImageResource(R.drawable.ic_unchecked_circle_24dp);
            }

        }
    }
}
