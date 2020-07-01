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
import vnu.uet.mobilecourse.assistant.adapter.viewholder.ExpandableGroupHolder;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.MaterialViewHolder;
import vnu.uet.mobilecourse.assistant.model.CourseOverview;
import vnu.uet.mobilecourse.assistant.model.Material;
import vnu.uet.mobilecourse.assistant.model.material.CourseConstant;
import vnu.uet.mobilecourse.assistant.view.course.CourseProgressFragment;
import vnu.uet.mobilecourse.assistant.viewmodel.expandable.ExpandableCourseContent;

import java.util.List;

public class CourseContentAdapter extends
        ExpandableRecyclerViewAdapter<CourseContentAdapter.WeeklyMaterialViewHolder, MaterialViewHolder> {

    private List<CourseOverview> mContents;
    private CourseProgressFragment mOwner;
    private LayoutInflater mInflater;
    private NavController mNavController;

    public CourseContentAdapter(List<CourseOverview> contents, CourseProgressFragment owner) {
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

        return new MaterialViewHolder(view, mNavController) {
            @Override
            protected void onCompleteChange() {
                mOwner.saveRecycleViewState();
            }
        };
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
            int index = expandableList.groups.indexOf(content);
            boolean expand = expandableList.expandedGroupIndexes[index];
            holder.bind(content, expand);
        }
    }

    static class WeeklyMaterialViewHolder extends ExpandableGroupHolder {

        private TextView mTvWeeklyTitle;

        WeeklyMaterialViewHolder(@NonNull View itemView) {
            super(itemView);

            mTvWeeklyTitle = itemView.findViewById(R.id.tvTitle);
        }

        void bind(ExpandableCourseContent content, boolean expand) {
            bindArrow(expand);
            mTvWeeklyTitle.setText(content.getTitle());
        }
    }


}
