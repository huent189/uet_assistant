package vnu.uet.mobilecourse.assistant.ui.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import vnu.uet.mobilecourse.assistant.R;

public class ClassMateAdapter extends RecyclerView.Adapter<ClassMateAdapter.ViewHolder> {
    private static final String TAG = ClassMateAdapter.class.getSimpleName();

    private List<String> classMates;

    private Fragment owner;

    private NavController navController;

    public ClassMateAdapter(List<String> classMates, Fragment owner) {
        this.classMates = classMates;
        this.owner = owner;
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
        }
    }
}
