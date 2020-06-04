package vnu.uet.mobilecourse.assistant.view.profile;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.AllCoursesAdapter;
import vnu.uet.mobilecourse.assistant.adapter.RecentlyCoursesAdapter;
import vnu.uet.mobilecourse.assistant.model.ICourse;
import vnu.uet.mobilecourse.assistant.model.firebase.UserInfo;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.view.course.CoursesFragment;
import vnu.uet.mobilecourse.assistant.viewmodel.FriendProfileViewModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class FriendProfileFragment extends Fragment {

    private FriendProfileViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(FriendProfileViewModel.class);

        View root = inflater.inflate(R.layout.fragment_friend_profile, container, false);

        Bundle args = getArguments();
        if (args != null) {
            TextView tvUsername = root.findViewById(R.id.tvUsername);
            String name = args.getString("name");
            tvUsername.setText(name);

            TextView tvUserId = root.findViewById(R.id.tvUserId);
            String code = args.getString("code");
            tvUserId.setText(code);

            TextView tvDob = root.findViewById(R.id.tvDoB);
            TextView tvClass = root.findViewById(R.id.tvClass);

            Button btnChat = root.findViewById(R.id.btnChat);
            boolean active = args.getBoolean("active");
            if (!active) btnChat.setVisibility(View.GONE);

            mViewModel.getUserInfo(code).observe(getViewLifecycleOwner(), new Observer<StateModel<UserInfo>>() {
                @Override
                public void onChanged(StateModel<UserInfo> stateModel) {
                    switch (stateModel.getStatus()) {
                        case SUCCESS:
                            UserInfo userInfo = stateModel.getData();

                            Date dob = new Date(userInfo.getDOB());
                            tvDob.setText(DateTimeUtils.DATE_FORMAT.format(dob));

                            tvClass.setText(userInfo.getUetClass());

                            break;
                    }
                }
            });

            RecyclerView rvCommonCourses = initializeCommonCoursesView(root);
            mViewModel.getCommonCourses(code).observe(getViewLifecycleOwner(), new Observer<StateModel<List<ICourse>>>() {
                @Override
                public void onChanged(StateModel<List<ICourse>> stateModel) {
                    switch (stateModel.getStatus()) {
                        case SUCCESS:
                            List<ICourse> courses = stateModel.getData();

                            AllCoursesAdapter adapter = new AllCoursesAdapter(courses, FriendProfileFragment.this);
                            rvCommonCourses.setAdapter(adapter);

                            break;
                    }
                }
            });
        }

        return root;
    }

    private RecyclerView initializeCommonCoursesView(View root) {
        RecyclerView rvCommonCourses = root.findViewById(R.id.rvCommonCourses);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());

        rvCommonCourses.setLayoutManager(layoutManager);

        return rvCommonCourses;
    }
}
