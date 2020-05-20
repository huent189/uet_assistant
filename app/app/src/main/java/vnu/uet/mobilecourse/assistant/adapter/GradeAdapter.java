package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
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

public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.GradeViewHolder> {

    private List<Grade> mGrades;
    private Fragment mOwner;
    private NavController mNavController;

    public GradeAdapter(List<Grade> grades, Fragment owner) {
        this.mGrades = grades;
        this.mOwner = owner;
    }

    @NonNull
    @Override
    public GradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mOwner.getLayoutInflater()
                .inflate(R.layout.layout_grade_item, parent, false);

        Activity activity = mOwner.getActivity();

        if (activity != null) {
            mNavController = Navigation
                    .findNavController(activity, R.id.nav_host_fragment);
        }

        return new GradeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GradeViewHolder holder, int position) {
        final Grade currentGrade = mGrades.get(position);
        holder.bind(currentGrade);
    }


    @Override
    public int getItemCount() {
        return mGrades.size();
    }

    static class GradeViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvTitle;

        private TextView mTvGrade;

        private ImageView mIvThumbnail;

        GradeViewHolder(@NonNull View view) {
            super(view);

            mTvTitle = view.findViewById(R.id.tvTitle);
            mTvGrade = view.findViewById(R.id.tvGrade);
            mIvThumbnail = view.findViewById(R.id.ivThumbnail);
        }

        void bind(Grade grade) {
            String title = grade.getName();
            mTvTitle.setText(title);

            double userGrade = grade.getUserGrade();
            double maxGrade = grade.getMaxGrade();

            String gradeProgress;

            // assignment/quiz has been graded
            if (userGrade > 0) {
                gradeProgress = String.format(Locale.ROOT, "%.0f/%.0f", userGrade, maxGrade);
            } else {
                gradeProgress = String.format(Locale.ROOT, "/%.0f", maxGrade);
            }

            mTvGrade.setText(gradeProgress);

            switch (grade.getType()) {
                case Grade.ASSIGN:
                    break;

                case Grade.QUIZ:
                    mIvThumbnail.setImageResource(R.drawable.ic_format_list_bulleted_32dp);
                    break;
            }
        }
    }
}
