package vnu.uet.mobilecourse.assistant.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseSession;
import vnu.uet.mobilecourse.assistant.view.course.CourseGeneralFragment;

public class CourseSessionAdapter extends RecyclerView.Adapter<CourseSessionAdapter.ViewHolder> {

    private List<CourseSession> mSessions;
    private CourseGeneralFragment mOwner;

    public CourseSessionAdapter(List<CourseSession> sessions, CourseGeneralFragment owner) {
        this.mSessions = new ArrayList<>(sessions);
        this.mOwner = owner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mOwner.getLayoutInflater()
                .inflate(R.layout.card_course_session, parent, false);

        return new ViewHolder(view);
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

    static class ViewHolder extends RecyclerView.ViewHolder {

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
