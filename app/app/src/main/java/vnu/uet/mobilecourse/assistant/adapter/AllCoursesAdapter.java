package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.Course;
import vnu.uet.mobilecourse.assistant.view.course.CoursesFragment;

public class AllCoursesAdapter extends RecyclerView.Adapter<AllCoursesAdapter.CourseViewHolder> {

    private List<Course> mCourses;
    private CoursesFragment mOwner;
    private NavController mNavController;

    public AllCoursesAdapter(List<Course> courses, CoursesFragment owner) {
        this.mCourses = courses;
        this.mOwner = owner;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mOwner.getLayoutInflater()
                .inflate(R.layout.layout_course_item, parent, false);

        Activity activity = mOwner.getActivity();

        if (activity != null) {
            mNavController = Navigation
                    .findNavController(activity, R.id.nav_host_fragment);
        }

        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        final Course currentCourse = mCourses.get(position);
        holder.bind(currentCourse, mNavController);
    }

    @Override
    public int getItemCount() {
        return mCourses.size();
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

        void bind(Course course, NavController navController) {
            String courseTitle = course.getTitle();
            String courseCode = course.getCode();

            mTvCourseTitle.setText(courseTitle);

            if (!courseCode.isEmpty()) {
                mTvCourseId.setText(courseCode);
            }

            mBtnAccessCourse.setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                bundle.putInt("courseId", course.getId());
                bundle.putString("courseTitle", courseTitle);
                bundle.putString("courseCode", courseCode);

                navController
                        .navigate(R.id.action_navigation_courses_to_navigation_explore_course, bundle);
            });
        }
    }
}
