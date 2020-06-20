package vnu.uet.mobilecourse.assistant.view.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.Date;
import java.util.List;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.AllCoursesAdapter;
import vnu.uet.mobilecourse.assistant.model.ICourse;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.model.firebase.UserInfo;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.viewmodel.FriendProfileViewModel;

public class FriendProfileFragment extends Fragment {

    private FriendProfileViewModel mViewModel;
    private NavController mNavController;
    private FragmentActivity mActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(FriendProfileViewModel.class);

        mActivity = getActivity();

        if (mActivity != null) {
            mNavController = Navigation.findNavController(mActivity, R.id.nav_host_fragment);
        }

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
            if (!active) {
                btnChat.setVisibility(View.GONE);

                btnChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onChatClick(args);
                    }
                });
            }

            LinearLayout layoutDob = root.findViewById(R.id.layoutDob);
            LinearLayout layoutClass = root.findViewById(R.id.layoutClass);

            ShimmerFrameLayout sflDobAndClass = root.findViewById(R.id.sflDobAndClass);
            sflDobAndClass.startShimmerAnimation();

            mViewModel.getUserInfo(code).observe(getViewLifecycleOwner(), stateModel -> {
                switch (stateModel.getStatus()) {
                    case LOADING:
                        // hide text
                        layoutDob.setVisibility(View.INVISIBLE);
                        layoutClass.setVisibility(View.INVISIBLE);

                        // show shimmer layout
                        sflDobAndClass.setVisibility(View.VISIBLE);

                        break;

                    case SUCCESS:
                        UserInfo userInfo = stateModel.getData();

                        Date dob = new Date(userInfo.getDOB());
                        tvDob.setText(DateTimeUtils.DATE_FORMAT.format(dob));

                        tvClass.setText(userInfo.getUetClass());

                        // show text
                        layoutDob.setVisibility(View.VISIBLE);
                        layoutClass.setVisibility(View.VISIBLE);

                        // hide shimmer layout
                        sflDobAndClass.setVisibility(View.GONE);

                        break;
                }
            });

            TextView tvCommonCourses = root.findViewById(R.id.tvCommonCourses);

            ShimmerFrameLayout sflCommonCourses = root.findViewById(R.id.sflCommonCourses);
            sflCommonCourses.startShimmerAnimation();

            RecyclerView rvCommonCourses = initializeCommonCoursesView(root);
            mViewModel.getCommonCourses(code).observe(getViewLifecycleOwner(), stateModel -> {
                switch (stateModel.getStatus()) {
                    case LOADING:
                        // hide
                        tvCommonCourses.setVisibility(View.INVISIBLE);
                        rvCommonCourses.setVisibility(View.GONE);

                        // show
                        sflCommonCourses.setVisibility(View.VISIBLE);

                        break;

                    case SUCCESS:
                        List<ICourse> courses = stateModel.getData();

                        AllCoursesAdapter adapter = new AllCoursesAdapter(courses, FriendProfileFragment.this);
                        rvCommonCourses.setAdapter(adapter);

                        // show
                        tvCommonCourses.setVisibility(View.VISIBLE);
                        rvCommonCourses.setVisibility(View.VISIBLE);

                        // hide
                        sflCommonCourses.setVisibility(View.GONE);

                        break;
                }
            });
        }

        return root;
    }

    private void onChatClick(Bundle prevArgs) {
        if (prevArgs.getBoolean("fromChat", false)) {
            mNavController.navigateUp();
        } else {
            Bundle bundle = new Bundle(prevArgs);
            bundle.remove("active");
            bundle.remove("fromChat");
            bundle.putString("type", GroupChat.DIRECT);
            mNavController.navigate(R.id.action_navigation_friend_profile_to_navigation_chat_room, bundle);
        }
    }

    private RecyclerView initializeCommonCoursesView(View root) {
        RecyclerView rvCommonCourses = root.findViewById(R.id.rvCommonCourses);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());

        rvCommonCourses.setLayoutManager(layoutManager);

        return rvCommonCourses;
    }
}
