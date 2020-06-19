package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.hdodenhof.circleimageview.CircleImageView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.viewholder.StudentViewHolder;
import vnu.uet.mobilecourse.assistant.model.IStudent;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.model.firebase.Participant_CourseSubCol;

public class ClassMateAdapter extends RecyclerView.Adapter<StudentViewHolder> implements Filterable {

    private List<Participant_CourseSubCol> mClassMates;
    private List<Participant_CourseSubCol> mFullList;
    private NavController mNavController;
    private Fragment mOwner;
    private Filter mFilter;

    public ClassMateAdapter(List<Participant_CourseSubCol> classMates, Fragment owner) {
        this.mClassMates = new ArrayList<>(classMates);
        this.mFullList = classMates;
        this.mOwner = owner;
        this.mFilter = new ClassMateFilter();
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mOwner.getLayoutInflater()
                .inflate(R.layout.layout_student_item, parent, false);

        Activity activity = mOwner.getActivity();

        if (activity != null) {
            mNavController = Navigation.findNavController(activity, R.id.nav_host_fragment);
        }

        return new StudentViewHolder(view) {

            @Override
            protected void onChatClick(IStudent student) {
                Bundle bundle = new Bundle();
                bundle.putString("title", student.getName());
                bundle.putString("type", GroupChat.DIRECT);
                bundle.putString("code", student.getCode());
                mNavController.navigate(R.id.action_navigation_explore_course_to_navigation_chat_room, bundle);
            }
        };
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        final Participant_CourseSubCol current = mClassMates.get(position);

        holder.bind(current);
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current.getCode().equals(User.getInstance().getStudentId())) {
                    mNavController.navigate(R.id.action_navigation_explore_course_to_navigation_my_profile);

                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("name", current.getName());
                    bundle.putString("code", current.getCode());
                    bundle.putString("avatar", current.getAvatar());
                    bundle.putBoolean("active", current.isActive());

                    mNavController.navigate(R.id.action_navigation_explore_course_to_navigation_friend_profile, bundle);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mClassMates.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public class ClassMateFilter extends MyFilter<Participant_CourseSubCol> {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Participant_CourseSubCol> filteredList;

            if (constraint == null || constraint.length() == 0)
                filteredList = new ArrayList<>(mFullList);
            else {
                final String filterPattern = constraint.toString().trim();

                filteredList = mFullList.stream()
                        .filter(i -> i.getCode().contains(filterPattern) || i.getName().contains(filterPattern))
                        .collect(Collectors.toList());
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mClassMates = getListFromResults(results);
            notifyDataSetChanged();
        }
    }
}
