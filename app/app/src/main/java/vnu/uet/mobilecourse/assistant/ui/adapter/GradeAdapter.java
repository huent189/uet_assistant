package vnu.uet.mobilecourse.assistant.ui.adapter;

import android.app.Activity;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.ui.model.Course;
import vnu.uet.mobilecourse.assistant.ui.view.course.CoursesFragment;

public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.ViewHolder> {
    private static final String TAG = GradeAdapter.class.getSimpleName();

    private List<String> grades;

    private Fragment owner;

    private NavController navController;

    public GradeAdapter(List<String> grades, Fragment owner) {
        this.grades = grades;
        this.owner = owner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = owner.getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.layout_grade_item, parent, false);

        ViewHolder holder = new ViewHolder(view);

        Activity activity = owner.getActivity();

        if (activity != null)
            navController = Navigation.findNavController(activity, R.id.nav_host_fragment);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "call onBindViewHolder");

        final String currentGrade = grades.get(position);

        holder.tvGradeTitle.setText(currentGrade);
    }


    @Override
    public int getItemCount() {
        return grades.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvGradeTitle;

        public ViewHolder(@NonNull View view) {
            super(view);

            tvGradeTitle = view.findViewById(R.id.tvGradeTitle);
        }
    }
}
