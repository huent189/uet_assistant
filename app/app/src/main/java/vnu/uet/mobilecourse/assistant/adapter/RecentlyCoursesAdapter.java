package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.view.course.CoursesFragment;
import vnu.uet.mobilecourse.assistant.model.Course;

public class RecentlyCoursesAdapter extends RecyclerView.Adapter<RecentlyCoursesAdapter.ViewHolder> {

    private List<Course> mCourses;
    private CoursesFragment mOwner;
    private NavController mNavController;

    public RecentlyCoursesAdapter(List<Course> courses, CoursesFragment owner) {
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
        holder.bind(course);
    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mIvThumbnail;
        private TextView mTvTitle;
        private TextView mTvCourseId;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            mIvThumbnail = itemView.findViewById(R.id.ivCourseThumbnail);
            mTvTitle = itemView.findViewById(R.id.tvCourseTitle);
            mTvCourseId = itemView.findViewById(R.id.tvCourseId);

            CardView cvCourseContainer = itemView.findViewById(R.id.cvCourseContainer);
            int cardColor = getBackgroundColor(R.drawable.isometric_course_thumbnail);
            cvCourseContainer.setCardBackgroundColor(cardColor);
        }

        private void bind(Course course) {
            mTvTitle.setText(course.getTitle());
            mTvCourseId.setText(course.getCode());
        }

        private int getBackgroundColor(int thumbnail) {
            int cardColor;

            switch (thumbnail) {
                case R.drawable.isometric_course_thumbnail:
                    cardColor = R.color.cardColor1;
                    break;

                case R.drawable.isometric_math_course_background:
                    cardColor = R.color.cardColor2;
                    break;

                default:
                    cardColor = R.color.cardColor1;
                    break;
            }

            Context context = mOwner.requireContext();

            return ContextCompat.getColor(context, cardColor);
        }
    }
}
