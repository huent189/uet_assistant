package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.Grade;

public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.ViewHolder> {
    private static final String TAG = GradeAdapter.class.getSimpleName();

    private List<Grade> grades;

    private Fragment owner;

    private NavController navController;

    public GradeAdapter(List<Grade> grades, Fragment owner) {
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

        final Grade currentGrade = grades.get(position);

        holder.bind(currentGrade);
    }


    @Override
    public int getItemCount() {
        return grades.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;

        private TextView tvGrade;

        private ImageView ivThumbnail;

        public ViewHolder(@NonNull View view) {
            super(view);

            tvTitle = view.findViewById(R.id.tvTitle);
            tvGrade = view.findViewById(R.id.tvGrade);
            ivThumbnail = view.findViewById(R.id.ivThumbnail);
        }

        public void bind(Grade grade) {
            String title = grade.getName();
            tvTitle.setText(title);

            String gradeProgress = String.format(Locale.ROOT,
                    "%.0f/%.0f",
                    grade.getUserGrade(),
                    grade.getMaxGrade());

            tvGrade.setText(gradeProgress);

            Log.d("GRADE_TYPE", grade.getType());
        }
    }
}
