package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
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

public class ClassMateAdapter extends RecyclerView.Adapter<ClassMateAdapter.ClassMateViewHolder> implements Filterable {
    private List<String> mClassMates;
    private List<String> mFullList;
    private NavController mNavController;
    private Fragment mOwner;
    private Filter mFilter;

    public ClassMateAdapter(List<String> classMates, Fragment owner) {
        this.mClassMates = classMates;
        this.mFullList = new ArrayList<>(classMates);
        this.mOwner = owner;
        this.mFilter = new ClassMateFilter();
    }

    @NonNull
    @Override
    public ClassMateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mOwner.getLayoutInflater()
                .inflate(R.layout.layout_classmate_item, parent, false);

        Activity activity = mOwner.getActivity();

        if (activity != null) {
            mNavController = Navigation.findNavController(activity, R.id.nav_host_fragment);
        }

        return new ClassMateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassMateViewHolder holder, int position) {
        final String current = mClassMates.get(position);
        holder.bind(current);
    }

    @Override
    public int getItemCount() {
        return mClassMates.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    class ClassMateViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView mCivAvatar;
        private TextView mTvClassMateName;
        private TextView mTvClassMateId;
        private Button mBtnChat;

        ClassMateViewHolder(@NonNull View view) {
            super(view);

            mCivAvatar = view.findViewById(R.id.civAvatar);
            mTvClassMateName = view.findViewById(R.id.tvClassMateName);
            mTvClassMateId = view.findViewById(R.id.tvClassMateId);
            mBtnChat = view.findViewById(R.id.btnChat);

            view.setOnClickListener(v ->
                    mNavController.navigate(
                            R.id.action_navigation_explore_course_to_navigation_friend_profile
                    )
            );
        }

        void bind(String classMate) {
            mTvClassMateName.setText(classMate);
        }
    }

    public class ClassMateFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<String> filteredList;

            if (constraint == null || constraint.length() == 0)
                filteredList = new ArrayList<>(mFullList);
            else {
                final String filterPattern = constraint.toString().toLowerCase().trim();

                filteredList = mFullList.stream()
                        .filter(i -> i.toLowerCase().contains(filterPattern))
                        .collect(Collectors.toList());
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mClassMates = new ArrayList<>();

            if (results.values instanceof List) {
                List list = (List) results.values;

                for (Object item : list) {
                    if (item instanceof String) {
                        mClassMates.add((String) item);
                    }
                }
            }

            notifyDataSetChanged();
        }
    }
}
