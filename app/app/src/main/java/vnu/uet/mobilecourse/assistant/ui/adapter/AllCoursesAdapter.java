package vnu.uet.mobilecourse.assistant.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.ui.model.Course;
import vnu.uet.mobilecourse.assistant.ui.view.course.CoursesFragment;
import vnu.uet.mobilecourse.assistant.ui.view.course.ExploreCourseFragment;

public class AllCoursesAdapter extends RecyclerView.Adapter<AllCoursesAdapter.ViewHolder> {
    private static final String TAG = AllCoursesAdapter.class.getSimpleName();

    private List<Course> courses;

    private CoursesFragment owner;

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
                Fragment fragment = new ExploreCourseFragment();
                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = owner.getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(CoursesFragment.class.getName())
                        .commit();

                Log.d(TAG, "onClick" + currentCourse.getTitle());
                Toast.makeText(owner.getContext(), "onClick" + currentCourse.getTitle(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCourseTitle;

        private TextView tvCourseId;

        private Button btnAccessCourse;

        private LinearLayout layoutContainer;

        public ViewHolder(@NonNull View view) {
            super(view);

            tvCourseTitle = view.findViewById(R.id.tvCourseTitle);
            tvCourseId = view.findViewById(R.id.tvCourseId);
            btnAccessCourse = view.findViewById(R.id.btnCourseAccess);
            layoutContainer = view.findViewById(R.id.layoutContainer);
        }
    }
}
