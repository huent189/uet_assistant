package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.CourseOverview;
import vnu.uet.mobilecourse.assistant.model.Material;
import vnu.uet.mobilecourse.assistant.model.material.CourseConstant;
import vnu.uet.mobilecourse.assistant.viewmodel.expandable.ExpandableCourseContent;

import java.util.List;

public class CourseContentAdapter extends
        ExpandableRecyclerViewAdapter<CourseContentAdapter.WeeklyMaterialViewHolder, CourseContentAdapter.MaterialViewHolder> {

    private List<CourseOverview> mContents;
    private Fragment mOwner;
    private LayoutInflater mInflater;
    private NavController mNavController;

    public CourseContentAdapter(List<CourseOverview> contents, Fragment owner) {
        super(ExpandableCourseContent.convert(contents));

        this.mOwner = owner;
        this.mInflater = owner.getLayoutInflater();

        Activity activity = owner.getActivity();

        if (activity != null) {
            mNavController = Navigation
                    .findNavController(activity, R.id.nav_host_fragment);
        }
    }

    @Override
    public WeeklyMaterialViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater
                .inflate(R.layout.layout_expandable_parent_item, parent, false);

        return new WeeklyMaterialViewHolder(view);
    }

    @Override
    public MaterialViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater
                .inflate(R.layout.layout_material_item, parent, false);

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

    static class WeeklyMaterialViewHolder extends GroupViewHolder {
        private TextView mTvWeeklyTitle;

        private ImageView mIvExpandArrow;

        WeeklyMaterialViewHolder(@NonNull View itemView) {
            super(itemView);

            mTvWeeklyTitle = itemView.findViewById(R.id.tvTitle);
            mIvExpandArrow = itemView.findViewById(R.id.ivExpandArrow);
        }

        void bind(ExpandableCourseContent content) {
            mTvWeeklyTitle.setText(content.getTitle());
        }
    }


    class MaterialViewHolder extends ChildViewHolder {
        private TextView mTvTaskTitle;
        private ImageView mIvTaskStatus;
        private ImageView mIvMaterialIcon;
        private View mView;

        MaterialViewHolder(@NonNull View itemView) {
            super(itemView);

            mTvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            mIvTaskStatus = itemView.findViewById(R.id.ivTaskStatus);
            mIvMaterialIcon = itemView.findViewById(R.id.ivMaterialIcon);
            this.mView = itemView;
        }

        void bind(Material material) {
            mTvTaskTitle.setText(material.getTitle());

            switch (material.getType()) {
                case CourseConstant.MaterialType.PAGE:
                    mIvMaterialIcon.setImageResource(R.drawable.ic_description_32dp);
                    break;

                case CourseConstant.MaterialType.ASSIGN:
                    mIvMaterialIcon.setImageResource(R.drawable.ic_assignment_32dp);
                    break;

                case CourseConstant.MaterialType.QUIZ:
                    mIvMaterialIcon.setImageResource(R.drawable.ic_format_list_bulleted_32dp);
                    break;

                case CourseConstant.MaterialType.URL:
                    mIvMaterialIcon.setImageResource(R.drawable.ic_language_32dp);
                    break;

                case CourseConstant.MaterialType.RESOURCE:
                    mIvMaterialIcon.setImageResource(R.drawable.ic_image_32dp);
                    break;

                default:
                    mIvMaterialIcon.setImageResource(R.drawable.ic_school_32dp);
                    break;
            }

            if (material.getCompletion() == 1) {
                mIvTaskStatus.setImageResource(R.drawable.ic_check_circle_24dp);
            } else {
                mIvTaskStatus.setImageResource(R.drawable.ic_unchecked_circle_24dp);
            }

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("material", material);

                    int actionId = R.id.action_navigation_explore_course_to_navigation_material;

                    mNavController.navigate(actionId, bundle);
                }
            });
        }
    }
}
