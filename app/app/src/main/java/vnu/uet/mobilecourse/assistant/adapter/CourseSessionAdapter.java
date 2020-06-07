package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.Course;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseSession;
import vnu.uet.mobilecourse.assistant.view.course.CourseGeneralFragment;
import vnu.uet.mobilecourse.assistant.view.course.CoursesFragment;

public class CourseSessionAdapter extends RecyclerView.Adapter<CourseSessionAdapter.ViewHolder> {

    private List<CourseSession> mSessions;
    private CourseGeneralFragment mOwner;
    private NavController mNavController;

    public CourseSessionAdapter(List<CourseSession> sessions, CourseGeneralFragment owner) {
        this.mSessions = new ArrayList<>(sessions);
        mSessions.addAll(sessions);
        this.mOwner = owner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mOwner.getLayoutInflater()
                .inflate(R.layout.card_course_session, parent, false);

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
        final CourseSession session = mSessions.get(position);
        holder.bind(session);
    }

    @Override
    public int getItemCount() {
        return mSessions.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvDayOfWeek;
        private TextView mTvSessionTime;
        private TextView mTvClassroom;
        private TextView mTvTeacherName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTvDayOfWeek = itemView.findViewById(R.id.tvDayOfWeek);
            mTvSessionTime = itemView.findViewById(R.id.tvSessionTime);
            mTvClassroom = itemView.findViewById(R.id.tvClassroom);
            mTvTeacherName = itemView.findViewById(R.id.tvTeacherName);
        }

        void bind(CourseSession session) {
            String type;
            if (session.getType() > 0) {
                type = "N" + session.getType();
            } else {
                type = "CL";
            }

            String dayOfWeek = String.format(Locale.ROOT, "Thứ %d (%s)", session.getDayOfWeek(), type);
            mTvDayOfWeek.setText(dayOfWeek);

            mTvClassroom.setText(session.getClassroom());

            mTvTeacherName.setText(session.getTeacherName());

            String time = String.format(Locale.ROOT, "Tiết %d - %d", session.getStart(), session.getEnd());
            mTvSessionTime.setText(time);
        }
    }
}
