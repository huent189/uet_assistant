package vnu.uet.mobilecourse.assistant.view.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.List;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.AllCoursesAdapter;
import vnu.uet.mobilecourse.assistant.model.ICourse;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.model.firebase.UserInfo;
import vnu.uet.mobilecourse.assistant.storage.StorageAccess;
import vnu.uet.mobilecourse.assistant.util.AvatarLoader;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.view.component.AvatarView;
import vnu.uet.mobilecourse.assistant.view.component.FullscreenImageView;
import vnu.uet.mobilecourse.assistant.viewmodel.FriendProfileViewModel;

public class FriendProfileFragment extends Fragment {

    private FriendProfileViewModel mViewModel;
    private NavController mNavController;
    private FragmentActivity mActivity;

    private String mName, mCode;

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
            mName = args.getString("name");
            tvUsername.setText(mName);

            TextView tvUserId = root.findViewById(R.id.tvUserId);
            mCode = args.getString("code");
            tvUserId.setText(mCode);

            TextView tvDob = root.findViewById(R.id.tvDoB);
            TextView tvClass = root.findViewById(R.id.tvClass);

            Button btnChat = root.findViewById(R.id.btnChat);
            boolean active = args.getBoolean("active");
            if (active) {
                btnChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onChatClick(args);
                    }
                });

                AvatarView avatarView = root.findViewById(R.id.avatarView);
                avatarView.setLifecycleOwner(getViewLifecycleOwner());
                avatarView.loadUser(mCode);

                avatarView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAvatarFullscreen();
                    }
                });

//                new AvatarLoader(mActivity, getViewLifecycleOwner())
//                        .loadUser(mCode, mCivAvatar);

            } else {
                btnChat.setVisibility(View.GONE);
            }

            LinearLayout layoutDob = root.findViewById(R.id.layoutDob);
            LinearLayout layoutClass = root.findViewById(R.id.layoutClass);

            ShimmerFrameLayout sflDobAndClass = root.findViewById(R.id.sflDobAndClass);
            sflDobAndClass.startShimmerAnimation();

            mViewModel.getUserInfo(mCode).observe(getViewLifecycleOwner(), stateModel -> {
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

                        if (!getArguments().containsKey("name")) {
                            String nameFetched = userInfo.getName();
                            tvUsername.setText(nameFetched);
                            getArguments().putString("name", nameFetched);
                        }

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
            mViewModel.getCommonCourses(mCode).observe(getViewLifecycleOwner(), stateModel -> {
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

        initializeToolbar(root);

        return root;
    }

    private void initializeToolbar(View root) {
        if (mActivity instanceof AppCompatActivity) {
            Toolbar toolbar = root.findViewById(R.id.toolbar);

            ((AppCompatActivity) mActivity).setSupportActionBar(toolbar);
        }
    }

    private void showAvatarFullscreen() {
        FullscreenImageView d = new FullscreenImageView(mActivity, "Ảnh đại diện");

        d.loadUser(mCode, getViewLifecycleOwner());
        d.show();
    }

    private void onChatClick(Bundle prevArgs) {
        if (prevArgs.getBoolean("fromChat", false)) {
            mNavController.navigateUp();
        } else {
            Bundle bundle = new Bundle(prevArgs);
            bundle.putString("title", mName);
            bundle.putString("code", mCode);
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
