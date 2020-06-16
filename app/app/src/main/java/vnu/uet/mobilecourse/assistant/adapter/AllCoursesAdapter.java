package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.Course;
import vnu.uet.mobilecourse.assistant.model.ICourse;
import vnu.uet.mobilecourse.assistant.view.profile.FriendProfileFragment;

public class AllCoursesAdapter extends RecyclerView.Adapter<AllCoursesAdapter.CourseViewHolder> implements Filterable {

    private List<ICourse> mCourses;
    private List<ICourse> mFullList;
    private Fragment mOwner;
    private Filter mFilter;

    public AllCoursesAdapter(List<ICourse> courses, Fragment owner) {
        this.mFullList = courses;
        this.mCourses = new ArrayList<>(courses);
        this.mOwner = owner;
        this.mFilter = new CourseFilter();
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mOwner.getLayoutInflater()
                .inflate(R.layout.layout_course_item, parent, false);

        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        final ICourse currentCourse = mCourses.get(position);
        holder.bind(currentCourse, mOwner);
    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public class CourseFilter extends MyFilter<ICourse> {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ICourse> filteredList;

            if (constraint == null || constraint.length() == 0)
                filteredList = new ArrayList<>(mFullList);
            else {
                final String filterPattern = constraint.toString().trim();

                filteredList = mFullList.stream()
                        .filter(i -> i.getCode().contains(filterPattern) || i.getTitle().contains(filterPattern))
                        .collect(Collectors.toList());
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mCourses = getListFromResults(results);
            notifyDataSetChanged();
        }
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvCourseTitle;
        private TextView mTvCourseId;
        private Button mBtnAccessCourse;

        CourseViewHolder(@NonNull View view) {
            super(view);

            mTvCourseTitle = view.findViewById(R.id.tvCourseTitle);
            mTvCourseId = view.findViewById(R.id.tvCourseId);
            mBtnAccessCourse = view.findViewById(R.id.btnCourseAccess);
        }

        void bind(ICourse course, Fragment owner) {
            String courseTitle = course.getTitle();
            String courseCode = course.getCode();

            mTvCourseTitle.setText(courseTitle);

            if (courseCode.isEmpty()) {
                mTvCourseId.setText(R.string.course_code_error);
            } else {
                mTvCourseId.setText(courseCode);
            }

            Activity activity = owner.getActivity();
            assert activity != null;
            NavController navController = Navigation
                        .findNavController(activity, R.id.nav_host_fragment);

            mBtnAccessCourse.setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                bundle.putParcelable("course", course);

                int actionId = R.id.action_navigation_courses_to_navigation_explore_course;

                if (owner instanceof FriendProfileFragment) {
                    actionId = R.id.action_navigation_friend_profile_to_navigation_explore_course;
                }

                navController.navigate(actionId, bundle);
            });
        }
    }
}
