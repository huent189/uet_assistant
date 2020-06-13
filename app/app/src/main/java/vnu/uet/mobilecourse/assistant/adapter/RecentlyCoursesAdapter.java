package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.Course;

public class RecentlyCoursesAdapter extends RecyclerView.Adapter<RecentlyCoursesAdapter.ViewHolder> {

    private List<Course> mCourses;
    private Fragment mOwner;
    private NavController mNavController;

    public RecentlyCoursesAdapter(List<Course> courses, Fragment owner) {
        this.mCourses = courses;
        this.mOwner = owner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mOwner.getLayoutInflater()
                .inflate(R.layout.card_course, parent, false);

        ViewHolder holder = new ViewHolder(view);

        Activity activity = mOwner.getActivity();

        if (activity != null) {
            mNavController = Navigation
                    .findNavController(activity, R.id.nav_host_fragment);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Course course = mCourses.get(position);
        holder.bind(course, mNavController);
    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mIvThumbnail;
        private TextView mTvTitle;
        private TextView mTvCourseId;
        private CardView mCvCourseContainer;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            mIvThumbnail = itemView.findViewById(R.id.ivCourseThumbnail);
            mTvTitle = itemView.findViewById(R.id.tvCourseTitle);
            mTvCourseId = itemView.findViewById(R.id.tvCourseId);

            mCvCourseContainer = itemView.findViewById(R.id.cvCourseContainer);
        }

        void bind(Course course, NavController navController) {
            String courseTitle = course.getTitle();
            String courseCode = course.getCode();

            mTvTitle.setText(courseTitle);
            mTvCourseId.setText(courseCode);

            int thumbnailResId = course.getThumbnail();
            mIvThumbnail.setImageResource(thumbnailResId);

            mCvCourseContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("course", course);

                    navController
                            .navigate(R.id.action_navigation_courses_to_navigation_explore_course, bundle);
                }
            });
        }


    }
}
