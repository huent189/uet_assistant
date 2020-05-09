package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.todo.Todo;
import vnu.uet.mobilecourse.assistant.view.course.CoursesFragment;
import vnu.uet.mobilecourse.assistant.model.Course;

public class RecentlyCoursesAdapter extends RecyclerView.Adapter<RecentlyCoursesAdapter.ViewHolder> {
    private static final String TAG = RecentlyCoursesAdapter.class.getSimpleName();

    private List<Course> courses;

    private LayoutInflater layoutInflater;

    private CoursesFragment owner;

    private NavController navController;

    public RecentlyCoursesAdapter(List<Course> courses, CoursesFragment owner) {
        this.courses = courses;
        this.owner = owner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = owner.getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.card_course, parent, false);

        ViewHolder holder = new ViewHolder(view);

        Activity activity = owner.getActivity();

        if (activity != null)
            navController = Navigation.findNavController(activity, R.id.nav_host_fragment);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "call onBindViewHolder");

        final Course course = courses.get(position);

        holder.tvCourseTitle.setText(course.getTitle());
        holder.tvCourseId.setText(course.getId());
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCourseThumbnail;

        private TextView tvCourseTitle;

        private TextView tvCourseId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivCourseThumbnail = itemView.findViewById(R.id.ivCourseThumbnail);
            tvCourseTitle = itemView.findViewById(R.id.tvCourseTitle);
            tvCourseId = itemView.findViewById(R.id.tvCourseId);

            CardView cvCourseContainer = itemView.findViewById(R.id.cvCourseContainer);

            int cardColor = getBackgroundColor(R.drawable.isometric_course_thumbnail);
            cvCourseContainer.setCardBackgroundColor(cardColor);
        }

        private int getBackgroundColor(int thumbnailImage) {
            int cardColor;

            switch (thumbnailImage) {
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

            Context context = Objects.requireNonNull(owner.getContext());

            return ContextCompat.getColor(context, cardColor);
        }
    }
}
