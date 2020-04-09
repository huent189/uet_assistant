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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.ui.model.Course;
import vnu.uet.mobilecourse.assistant.ui.view.course.CoursesFragment;

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

        navController = Navigation.findNavController(owner.getActivity(), R.id.nav_host_fragment);

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
//                openExploreCourseFragment();

                String message = "onClick" + currentCourse.getTitle();
                Log.d(TAG, message);
                Toast.makeText(owner.getContext(), message, Toast.LENGTH_SHORT).show();

            }
        });
    }

//    private void openExploreCourseFragment() {
//        DashboardFragmentManager fragmentManager = new DashboardFragmentManager(owner);
//
//        fragmentManager.change(CoursesFragment.class, ExploreCourseFragment.class);
//
////        FragmentActivity activity = owner.getActivity();
////
////        if (activity == null)
////            return;
////
////        FragmentManager fragmentManager = activity.getSupportFragmentManager();
////
////        Fragment prev = fragmentManager.getPrimaryNavigationFragment();
////
////        Fragment fragment = new ExploreCourseFragment();
////
////        // open explore course fragment
////        fragmentManager.beginTransaction()
////                // insert the fragment by replacing an existing fragment
////                .replace(R.id.nav_host_fragment, fragment)
////                // add previous fragment into back stack
////                .addToBackStack(CoursesFragment.class.getName())
////                // commit transaction
////                .commit();
//    }

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
