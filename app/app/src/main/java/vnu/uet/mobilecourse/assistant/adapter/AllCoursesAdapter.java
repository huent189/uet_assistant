package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.Course;
import vnu.uet.mobilecourse.assistant.view.course.CoursesFragment;

public class AllCoursesAdapter extends RecyclerView.Adapter<AllCoursesAdapter.ViewHolder> {
    private static final String TAG = AllCoursesAdapter.class.getSimpleName();

    private List<Course> courses;

    private CoursesFragment owner;

    private NavController navController;

    public AllCoursesAdapter(List<Course> courses, CoursesFragment owner) {
        this.courses = courses;
        this.owner = owner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = owner.getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.layout_course_item, parent, false);

        ViewHolder holder = new ViewHolder(view);

        Activity activity = owner.getActivity();

        if (activity != null)
            navController = Navigation.findNavController(activity, R.id.nav_host_fragment);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "call onBindViewHolder");

        final Course currentCourse = courses.get(position);

        holder.tvCourseTitle.setText(currentCourse.getTitle());
        holder.tvCourseId.setText(currentCourse.getId());

        holder.btnAccessCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_navigation_courses_to_navigation_explore_course);

                String message = "onClick" + currentCourse.getTitle();
                Log.d(TAG, message);
                Toast.makeText(owner.getContext(), message, Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCourseTitle;

        private TextView tvCourseId;

        private Button btnAccessCourse;

//        private LinearLayout layoutContainer;

        public ViewHolder(@NonNull View view) {
            super(view);

            tvCourseTitle = view.findViewById(R.id.tvCourseTitle);
            tvCourseId = view.findViewById(R.id.tvCourseId);
            btnAccessCourse = view.findViewById(R.id.btnCourseAccess);
//            layoutContainer = view.findViewById(R.id.layoutContainer);
        }
    }
}
