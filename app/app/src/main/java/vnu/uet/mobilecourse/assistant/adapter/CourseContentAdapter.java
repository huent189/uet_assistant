package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.os.Bundle;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.CourseContent;
import vnu.uet.mobilecourse.assistant.model.ExpandableCourseContent;
import vnu.uet.mobilecourse.assistant.model.Material;

public class CourseContentAdapter extends
        ExpandableRecyclerViewAdapter<CourseContentAdapter.WeeklyMaterialViewHolder, CourseContentAdapter.MaterialViewHolder> {

    private Fragment owner;

    private List<CourseContent> contents;

    private LayoutInflater inflater;

    private NavController navController;

    public CourseContentAdapter(List<CourseContent> contents, Fragment owner) {
        super(ExpandableCourseContent.convert(contents));

        this.owner = owner;
        this.inflater = owner.getLayoutInflater();

        Activity activity = owner.getActivity();

        if (activity != null)
            navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
    }

    public void setContents(List<CourseContent> contents) {
        this.contents = contents;
    }

    @Override
    public WeeklyMaterialViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_expandable_parent_item, parent, false);
        return new WeeklyMaterialViewHolder(view);
    }

    @Override
    public MaterialViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_material_item, parent, false);
        return new MaterialViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(MaterialViewHolder holder, int flatPosition,
                                      ExpandableGroup group, int childIndex) {

        if (group instanceof ExpandableCourseContent) {
            ExpandableCourseContent content = (ExpandableCourseContent) group;
            final Material material = content.getItems().get(childIndex);
            holder.bind(material);
        }
    }

    @Override
    public void onBindGroupViewHolder(WeeklyMaterialViewHolder holder, int flatPosition,
                                      ExpandableGroup group) {

        if (group instanceof ExpandableCourseContent) {
            ExpandableCourseContent content = (ExpandableCourseContent) group;
            holder.bind(content);
        }
    }

    public class WeeklyMaterialViewHolder extends GroupViewHolder {
        private TextView tvWeeklyTitle;

        private ImageView ivExpandArrow;

        public WeeklyMaterialViewHolder(@NonNull View itemView) {
            super(itemView);

            tvWeeklyTitle = itemView.findViewById(R.id.tvTitle);
            ivExpandArrow = itemView.findViewById(R.id.ivExpandArrow);
        }

        public void bind(ExpandableCourseContent content) {
            tvWeeklyTitle.setText(content.getTitle());
        }
    }


    public class MaterialViewHolder extends ChildViewHolder {
        private TextView tvTaskTitle;

        private ImageView ivTaskStatus;

        private View itemView;

        public MaterialViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            ivTaskStatus = itemView.findViewById(R.id.ivTaskStatus);
            this.itemView = itemView;
        }

        public void bind(Material material) {
            tvTaskTitle.setText(material.getTitle());

            if (material.getCompletion() == 1) {
                ivTaskStatus.setImageResource(R.drawable.ic_check_circle_24dp);
            } else {
                ivTaskStatus.setImageResource(R.drawable.ic_unchecked_circle_24dp);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("material", material);

                    navController.navigate(R.id.action_navigation_explore_course_to_navigation_material, bundle);
                }
            });
        }
    }
}
