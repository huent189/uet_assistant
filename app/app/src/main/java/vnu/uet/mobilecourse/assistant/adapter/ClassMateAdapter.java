package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
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

public class ClassMateAdapter extends RecyclerView.Adapter<ClassMateAdapter.ViewHolder> implements Filterable {
    private static final String TAG = ClassMateAdapter.class.getSimpleName();

    private List<String> classMates;

    private List<String> classMateFullList;

    private Fragment owner;

    private NavController navController;

    private Filter filter;

    public ClassMateAdapter(List<String> classMates, Fragment owner) {
        this.classMates = classMates;
        this.classMateFullList = new ArrayList<>(classMates);
        this.owner = owner;
        this.filter = new ClassMateFilter();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = owner.getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.layout_classmate_item, parent, false);

        ViewHolder holder = new ViewHolder(view);

        Activity activity = owner.getActivity();

        if (activity != null)
            navController = Navigation.findNavController(activity, R.id.nav_host_fragment);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "call onBindViewHolder");

        final String current = classMates.get(position);

        holder.tvClassMateName.setText(current);
    }

    @Override
    public int getItemCount() {
        return classMates.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView civAvatar;

        private TextView tvClassMateName;

        private TextView tvClassMateId;

        private Button btnChat;

        public ViewHolder(@NonNull View view) {
            super(view);

            civAvatar = view.findViewById(R.id.civAvatar);
            tvClassMateName = view.findViewById(R.id.tvClassMateName);
            tvClassMateId = view.findViewById(R.id.tvClassMateId);
            btnChat = view.findViewById(R.id.btnChat);

            view.setOnClickListener(v ->
                    navController.navigate(
                            R.id.action_navigation_explore_course_to_navigation_friend_profile
                    )
            );
        }
    }

    public class ClassMateFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<String> filteredList;

            if (constraint == null || constraint.length() == 0)
                filteredList = new ArrayList<>(classMateFullList);

            else {
                final String filterPattern = constraint.toString().toLowerCase().trim();

                filteredList = classMateFullList.stream()
                        .filter(i -> i.toLowerCase().contains(filterPattern))
                        .collect(Collectors.toList());
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            classMates = ((List<String>) results.values);
            notifyDataSetChanged();
        }
    }
}
